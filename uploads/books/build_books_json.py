import os
import re
import json
import unicodedata
import fitz  # PyMuPDF
from langdetect import detect
from unidecode import unidecode
from rapidfuzz import fuzz
from concurrent.futures import ProcessPoolExecutor, as_completed
import multiprocessing
import random



PDF_DIR = r"D:\BookHub_F_KIT2023\BookHub_F_KIT2023\bookhub_postgress\uploads\books\pdf"
COVER_DIR = r"D:\BookHub_F_KIT2023\BookHub_F_KIT2023\bookhub_postgress\uploads\books\covers"
OUTPUT_JSON = "books_output.json"


# ================== LOAD COVER LIST ==================
def build_cover_list():
    covers = []
    for f in sorted(os.listdir(COVER_DIR)):
        if f.lower().endswith((".jpg", ".png", ".jpeg", ".webp")):
            covers.append(os.path.join("books/covers", f).replace("\\", "/"))
    return covers


# ================== EXTRACT PDF TEXT (FIXED) ==================
def extract_pdf_text(pdf_path, max_pages=None):
    text_parts = []
    with fitz.open(pdf_path) as doc:
        pages = doc if not max_pages else doc[:max_pages]
        for page in pages:
            t = page.get_text("text")
            if t:
                text_parts.append(t)
    return "\n".join(text_parts)


# ================== DETECT LANGUAGE ==================
def detect_language(text):
    try:
        lang = detect(text[:2000])
        return "vi" if lang == "vi" else "en"
    except:
        return "en"


# ================== FIND DESCRIPTION ==================
def extract_description(text):
    lines = text.split("\n")[:40]
    for line in lines:
        if len(line) > 100:
            return line.strip()
    return "Không có mô tả"


# ================== OCR NORMALIZE ==================
def normalize_ocr_text(text):
    text = unicodedata.normalize("NFC", text)
    text = re.sub(r'(.)\1{1,}', r'\1', text)
    text = re.sub(r'(?<=\w)\s+(?=\w)', '', text)
    return text.strip().lower()


# ================== FUZZY CHAPTER TITLE DETECTION ==================
def is_chapter_title(line):
    raw = line.strip()
    if len(raw) > 40 or len(raw) < 1:
        return False

    text = normalize_ocr_text(raw)
    keywords = ["chuong", "chương", "chapter", "tap", "tập", "phan", "phần", "part"]

    for kw in keywords:
        if fuzz.partial_ratio(kw, text) > 80:
            if re.search(r'\d+|[ivxlcdm]+', text):
                return True

    if re.fullmatch(r'(chapter\s*)?\d{1,3}', text):
        return True

    if re.fullmatch(r'[ivxlcdm]{1,6}', text):
        return True

    return False


# ================== CHAPTER NUMBER ==================
def extract_chapter_number(text):
    text = normalize_ocr_text(text)
    m = re.search(r'(\d{1,4})', text)
    if m:
        return int(m.group(1))

    roman_map = {'i':1,'v':5,'x':10,'l':50,'c':100,'d':500,'m':1000}
    total = 0
    prev = 0
    for ch in reversed(text):
        val = roman_map.get(ch, 0)
        if val < prev:
            total -= val
        else:
            total += val
            prev = val

    return total if total > 0 else None


# ================== SPLIT BY CONTENT (GIỮ NGUYÊN) ==================
def split_chapters_by_content(full_text):
    text = full_text.replace('\r', '\n')
    text = re.sub(r'(.)\1+', r'\1', text)

    chapter_pattern = re.compile(
        r'(?i)(chuong|chương|chapter)\s*[\.:_-]?\s*([0-9]{1,3}|[ivxlcdm]{1,7}|[lI]{1,4})'
    )

    matches = list(chapter_pattern.finditer(text))
    chapters = []
    expected = 1
    started = False

    for i, match in enumerate(matches):
        raw = match.group()
        number = extract_chapter_number(raw)
        if not number:
            continue

        if not started:
            if number == 1:
                started = True
            else:
                continue

        if number != expected:
            continue

        start = match.start()
        end = matches[i + 1].start() if i + 1 < len(matches) else len(text)
        content = text[start:end].strip()

        if len(content) < 800:
            continue

        chapters.append({
            "chapterTitle": f"Chương {number}",
            "chapterOrder": len(chapters) + 1,
            "textContent": content
        })

        expected += 1

    if not chapters:
        return [{
            "chapterTitle": "Full Book",
            "chapterOrder": 1,
            "textContent": full_text
        }]

    return chapters


# ================== PROCESS ONE BOOK ==================
def process_book(pdf_file, cover_url, book_id):
    pdf_path = os.path.join(PDF_DIR, pdf_file)
    print("📘 Processing:", pdf_file)

    first_pages = extract_pdf_text(pdf_path, max_pages=5)

    title = os.path.splitext(pdf_file)[0]
    language = detect_language(first_pages)
    description = extract_description(first_pages)
    full_text = extract_pdf_text(pdf_path)
    chapters = split_chapters_by_content(full_text)

    return {
        "isbn": str(9780000000000 + book_id),
        "title": title,
        "authorId": random.randint(1, 100),
        "language": language,
        "description": description,
        "coverUrl": cover_url,
        "genreIds": random.sample(range(1, 51), k=random.randint(1, 3)),  # 🎲 1-3 genre
        "chapters": [
            {
                "chapterTitle": c["chapterTitle"],
                "chapterOrder": c["chapterOrder"],
                "textContent": c["textContent"],
                "audioUrl": None,
                "duration": None
            } for c in chapters
        ],
        "mediaAssets": [
            {
                "fileUrl": f"books/pdf/{pdf_file}",
                "type": "pdf"
            }
        ]
    }


# ================== MAIN ==================
BATCH_SIZE = 10
OUTPUT_DIR = "output_json"
os.makedirs(OUTPUT_DIR, exist_ok=True)


def main():
    cover_list = build_cover_list()
    pdf_files = sorted([f for f in os.listdir(PDF_DIR) if f.lower().endswith(".pdf")])

    batch_books = []
    file_index = 1
    book_id = 1

    for i, pdf_file in enumerate(pdf_files):
        cover_url = cover_list[i] if i < len(cover_list) else "books/covers/default.jpg"
        print(f"📖 Processing {pdf_file}")
        book_json = process_book(pdf_file, cover_url, book_id)
        batch_books.append(book_json)
        book_id += 1

        if len(batch_books) == BATCH_SIZE:
            out_path = os.path.join(OUTPUT_DIR, f"books_batch_{file_index}.json")
            with open(out_path, "w", encoding="utf-8") as f:
                json.dump(batch_books, f, ensure_ascii=False, indent=2)

            print(f"💾 Saved {out_path}")
            batch_books.clear()
            file_index += 1

    if batch_books:
        out_path = os.path.join(OUTPUT_DIR, f"books_batch_{file_index}.json")
        with open(out_path, "w", encoding="utf-8") as f:
            json.dump(batch_books, f, ensure_ascii=False, indent=2)

        print(f"💾 Saved {out_path}")


if __name__ == "__main__":
    main()
