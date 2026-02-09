package com.bookhup.controller.service;

import com.bookhup.model.Author;
import com.bookhup.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DevAuthorService {

    private final AuthorRepository authorRepository;
    private final Random random = new Random();


    private static final String[] QUOC_GIA = {
            "Việt Nam", "Mỹ", "Anh", "Pháp", "Đức", "Nhật Bản", "Hàn Quốc", "Trung Quốc"
    };
    private static final String[] HO_VN = {
            "Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Phan", "Vũ", "Đặng",
            "Bùi", "Đỗ", "Hồ", "Ngô", "Dương", "Lý", "Mai", "Trương"
    };

    private static final String[] TEN_DEM_VN = {
            "Văn", "Thị", "Ngọc", "Hoài", "Thanh", "Minh", "Quốc", "Gia", "Hữu", "Đức"
    };

    private static final String[] TEN_VN = {
            "An", "Anh", "Bình", "Chi", "Dũng", "Giang", "Hà", "Hương",
            "Khánh", "Lan", "Linh", "Mai", "Minh", "Nam", "Ngọc", "Phúc",
            "Quân", "Quang", "Trang", "Vy", "Yến", "Sơn", "Tú", "Trí"
    };

    private static final String[] FIRST_EN = {
            "James", "John", "Michael", "David", "Robert", "William", "Daniel",
            "Matthew", "Joseph", "Andrew", "Emma", "Olivia", "Sophia", "Isabella",
            "Mia", "Charlotte", "Amelia", "Harper", "Evelyn", "Abigail"
    };

    private static final String[] LAST_EN = {
            "Smith", "Johnson", "Brown", "Taylor", "Anderson", "Thomas",
            "Jackson", "White", "Harris", "Martin", "Thompson", "Garcia",
            "Martinez", "Robinson", "Clark"
    };
    private static final String[] FIRST_FR = {
            "Jean", "Pierre", "Louis", "Henri", "Luc", "Marie", "Sophie",
            "Camille", "Julien", "Antoine", "Claire", "Elise", "Nicolas"
    };

    private static final String[] LAST_FR = {
            "Dubois", "Moreau", "Lefevre", "Garcia", "Roux", "Fournier",
            "Girard", "Andre", "Mercier", "Dupont"
    };
    private static final String[] FIRST_DE = {
            "Lukas", "Jonas", "Leon", "Finn", "Paul", "Anna", "Lena",
            "Laura", "Sophie", "Marie", "Maximilian", "Felix"
    };

    private static final String[] LAST_DE = {
            "Müller", "Schmidt", "Schneider", "Fischer", "Weber",
            "Meyer", "Wagner", "Becker", "Hoffmann", "Schäfer"
    };
    private static final String[] LAST_JP = {
            "Sato", "Suzuki", "Takahashi", "Tanaka", "Watanabe",
            "Ito", "Yamamoto", "Nakamura", "Kobayashi", "Kato"
    };

    private static final String[] FIRST_JP = {
            "Haruto", "Yuki", "Sota", "Yuma", "Ren",
            "Hina", "Yui", "Aoi", "Sakura", "Mei"
    };
    private static final String[] LAST_KR = {
            "Kim", "Lee", "Park", "Choi", "Jung",
            "Kang", "Cho", "Yoon", "Jang", "Lim"
    };

    private static final String[] FIRST_KR = {
            "Min-jun", "Seo-yeon", "Ji-ho", "Ha-eun", "Do-yun",
            "Ji-woo", "Hyun-woo", "Soo-bin", "Ye-jun", "Hae-in"
    };
    private static final String[] LAST_CN = {
            "Wang", "Li", "Zhang", "Liu", "Chen",
            "Yang", "Huang", "Zhao", "Wu", "Zhou"
    };

    private static final String[] FIRST_CN = {
            "Wei", "Fang", "Jie", "Ming", "Lei",
            "Tao", "Yan", "Xin", "Hao", "Ling"
    };

    private String randomAuthorNameByCountry(String country) {
        return switch (country) {
            case "Việt Nam" -> HO_VN[random.nextInt(HO_VN.length)] + " "
                               + TEN_DEM_VN[random.nextInt(TEN_DEM_VN.length)] + " "
                               + TEN_VN[random.nextInt(TEN_VN.length)];

            case "Mỹ", "Anh" -> FIRST_EN[random.nextInt(FIRST_EN.length)] + " "
                                + LAST_EN[random.nextInt(LAST_EN.length)];

            case "Pháp" -> FIRST_FR[random.nextInt(FIRST_FR.length)] + " "
                           + LAST_FR[random.nextInt(LAST_FR.length)];

            case "Đức" -> FIRST_DE[random.nextInt(FIRST_DE.length)] + " "
                          + LAST_DE[random.nextInt(LAST_DE.length)];

            case "Nhật Bản" -> LAST_JP[random.nextInt(LAST_JP.length)] + " "
                               + FIRST_JP[random.nextInt(FIRST_JP.length)];

            case "Hàn Quốc" -> LAST_KR[random.nextInt(LAST_KR.length)] + " "
                               + FIRST_KR[random.nextInt(FIRST_KR.length)];

            case "Trung Quốc" -> LAST_CN[random.nextInt(LAST_CN.length)] + " "
                                 + FIRST_CN[random.nextInt(FIRST_CN.length)];

            default -> "Unknown Author";
        };
    }

    @Transactional
    public void generateAuthors(int count) {

        Set<String> usedNames = new HashSet<>();
        List<Author> authors = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            String country = randomCountry();

            // 👉 tạo tên KHÔNG TRÙNG
            String name = generateUniqueAuthorName(country, usedNames);

            Author author = Author.builder()
                    .name(name)
                    .country(country)
                    .bio(randomBioByCountry(country, name))
                    .ownerId(1L)
                    .build();

            authors.add(author);
            usedNames.add(name);
        }

        authorRepository.saveAll(authors);
    }
    private static final String[] STYLE_VIET = {
            "sở hữu phong cách viết giàu cảm xúc và chiều sâu.",
            "có lối kể chuyện cuốn hút và tinh tế.",
            "được biết đến với văn phong nhẹ nhàng nhưng đầy ám ảnh.",
            "ghi dấu ấn nhờ cách xây dựng nhân vật chân thực.",
            "nổi bật với giọng văn truyền cảm và gần gũi.",
            "chinh phục độc giả bằng những câu chuyện sâu lắng.",
            "mang đến những trang viết giàu hình ảnh và cảm xúc.",
            "có văn phong hiện đại pha chút cổ điển."
    };

    private static final String[] GENRE_VIET = {
            "Các tác phẩm của ông/bà thường xoay quanh đề tài gia đình và xã hội.",
            "Ông/bà nổi tiếng với dòng tiểu thuyết tâm lý – tình cảm.",
            "Ông/bà ghi dấu ấn trong thể loại truyện ngắn giàu tính nhân văn.",
            "Thể loại sở trường của ông/bà là văn học hiện thực.",
            "Ông/bà được yêu thích qua những tác phẩm dành cho tuổi trẻ.",
            "Nhiều tác phẩm của ông/bà thuộc thể loại phiêu lưu và kỳ ảo.",
            "Ông/bà có thế mạnh trong việc khai thác nội tâm nhân vật.",
            "Các sáng tác của ông/bà thường mang màu sắc triết lý nhẹ nhàng."
    };
    private static final String[] ACHIEVEMENT_VIET = {
            "Nhiều tác phẩm của ông/bà đã được tái bản nhiều lần.",
            "Một số tác phẩm của ông/bà đã được chuyển thể thành phim.",
            "Ông/bà từng nhận được nhiều lời khen từ giới phê bình văn học.",
            "Tên tuổi của ông/bà gắn liền với nhiều tác phẩm bán chạy.",
            "Ông/bà được đông đảo độc giả trong và ngoài nước yêu mến.",
            "Tác phẩm của ông/bà thường xuyên xuất hiện trong danh sách đề cử văn học.",
            "Ông/bà là cây bút quen thuộc trên nhiều tạp chí văn học.",
            "Sáng tác của ông/bà để lại dấu ấn sâu đậm trong lòng độc giả."
    };
    private String countryPrefix(String country) {
        return switch (country) {
            case "Việt Nam" -> "là một tác giả Việt Nam";
            case "Mỹ" -> "là một tác giả người Mỹ";
            case "Anh" -> "là một tác giả người Anh";
            case "Pháp" -> "là một tác giả người Pháp";
            case "Đức" -> "là một tác giả người Đức";
            case "Nhật Bản" -> "là một tác giả người Nhật Bản";
            case "Hàn Quốc" -> "là một tác giả người Hàn Quốc";
            case "Trung Quốc" -> "là một tác giả người Trung Quốc";
            default -> "là một tác giả";
        };
    }

    private String randomFrom(String[] arr) {
        return arr[random.nextInt(arr.length)];
    }

    private String randomBioByCountry(String country, String name) {
        return name + " " +
               countryPrefix(country) + ", " +
               randomFrom(STYLE_VIET) + " " +
               randomFrom(GENRE_VIET) + " " +
               randomFrom(ACHIEVEMENT_VIET);
    }

    private String generateUniqueAuthorName(String country, Set<String> usedNames) {
        String baseName;
        String finalName;
        int suffix = 1;

        do {
            baseName = randomAuthorNameByCountry(country);
            finalName = baseName;



            // nếu vẫn trùng thì lặp lại random tên mới
        } while (usedNames.contains(finalName) || authorRepository.existsByName(finalName));

        return finalName;
    }

    private String randomCountry() {
        return QUOC_GIA[random.nextInt(QUOC_GIA.length)];
    }
}

