package com.bookhup.controller.service;

import com.bookhup.model.Book;
import com.bookhup.model.Post;
import com.bookhup.model.User;
import com.bookhup.repository.*;
import com.bookhup.service.ReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DevPostService1 {
    private final PostRepository postRepository;
    private final BookRepository bookRepository;
    private final HashtagRepository hashtagRepo;
    private final FollowRepository followRepository;
    private final UserFeedWeightsRepository weightRepo;
    private final LikeRepository likeRepository;
    private final ReadingService readingService;
    private UserRepository userRepository;
//
//    public List<Post> generateFakePosts(Integer totalPosts) {
//
//        List<Post> posts = new ArrayList<>();
//        Random random = new Random();
//
//        Map<String, String> contentMap = Map.ofEntries(
//
//                Map.entry("Cuốn sách này thực sự thay đổi tư duy của mình!",
//                        "This book really changed my mindset!"),
//
//                Map.entry("Chương này cực kỳ gay cấn và khó đoán!",
//                        "This chapter is extremely intense and unpredictable!"),
//
//                Map.entry("Cảm giác như mình đang sống trong câu chuyện.",
//                        "It feels like I'm living inside the story."),
//
//                Map.entry("Đoạn này khiến mình suy nghĩ rất nhiều.",
//                        "This part made me reflect deeply."),
//
//                Map.entry("Một trong những cuốn sách đáng đọc nhất năm nay.",
//                        "One of the most worth-reading books this year."),
//
//                Map.entry("Nhân vật chính phát triển rất ấn tượng.",
//                        "The main character develops impressively."),
//
//                Map.entry("Không thể dừng lại ở chương này được!",
//                        "I couldn't stop at this chapter!"),
//
//                Map.entry("Triết lý trong sách rất sâu sắc.",
//                        "The philosophy in this book is profound."),
//
//                Map.entry("Đọc đến đoạn này mà nổi da gà.",
//                        "This part gave me goosebumps."),
//
//                Map.entry("Đúng kiểu sách mình đang tìm kiếm.",
//                        "Exactly the kind of book I was looking for.")
//        );
//
//
//        List<List<String>> sampleHashtags = List.of(
//                List.of("#booklover", "#reading"),
//                List.of("#fantasy", "#novel"),
//                List.of("#motivation", "#selfhelp"),
//                List.of("#dailyreading"),
//                List.of("#mustread", "#bookcommunity")
//        );
//
//        List<String> shareTemplates = List.of(
//
//                "Đọc đến đoạn \"%s\" mà nổi da gà luôn 😭",
//                "Trời ơi đúng khúc \"%s\" là mình cũng dừng lại suy nghĩ mấy phút liền.",
//                "Ai từng đọc tới \"%s\" chắc hiểu cảm giác này lắm luôn.",
//                "Không biết mọi người sao chứ mình đọc tới \"%s\" là bị cuốn hẳn vào luôn.",
//                "Cái đoạn \"%s\" thật sự ám ảnh mình mãi.",
//                "Tới đoạn \"%s\" là mình biết cuốn này chắc chắn hợp gu rồi.",
//                "Đang đọc mà gặp đoạn \"%s\" phải share liền cho mọi người.",
//                "Đọc tới \"%s\" là tim mình chững lại luôn 🥹",
//                "Cảm giác khi đọc đoạn \"%s\" đúng là khó tả thật.",
//                "Ai mê mấy đoạn kiểu \"%s\" chắc sẽ thích cuốn này lắm.",
//                "Mình vừa đọc tới \"%s\" và phải nói là quá xuất sắc.",
//                "Tới khúc \"%s\" là mình bắt đầu không thể rời mắt khỏi sách nữa.",
//                "Đọc mà tới đoạn \"%s\" là chỉ muốn ngồi ngẫm mãi thôi.",
//                "Khúc \"%s\" đúng kiểu đọc xong là nhớ hoài luôn.",
//                "Không ngờ đoạn \"%s\" lại chạm cảm xúc mình như vậy.",
//                "Vừa đọc tới \"%s\" là mình phải quay lại đọc thêm lần nữa.",
//                "Đoạn \"%s\" thật sự làm mình thay đổi cách nhìn về nhân vật.",
//                "Gặp đoạn \"%s\" là mình biết cuốn này không phải dạng vừa rồi.",
//                "Tự nhiên đọc tới \"%s\" mà thấy đồng cảm ghê luôn.",
//                "Đoạn \"%s\" đúng kiểu đọc xong là muốn nói chuyện với ai đó ngay."
//        );
//
//
//        // random ngày trong 30 ngày gần đây
//        LocalDateTime createdAt = LocalDateTime.now()
//                .minusDays(random.nextInt(30))
//                .minusHours(random.nextInt(24));
//        List<String> contents = new ArrayList<>(contentMap.keySet());
//        String content = contents.get(random.nextInt(contents.size()));
//        String translated = contentMap.get(content);
//
//
//        Map<Long, List<Book>> userBooksMap = new HashMap<>();
//
//        for (long userId = 1; userId <= 102; userId++) {
//
//            User user = userRepository.findById(userId).orElse(null);
//            if (user == null) continue;
//
//            int bookCount = 10 + random.nextInt(6); // 10–15
//
//            List<Book> books = new ArrayList<>();
//
//            for (int i = 0; i < bookCount; i++) {
//                long bookId = 1 + random.nextInt(62);
//                bookRepository.findById(bookId).ifPresent(books::add);
//            }
//
//            userBooksMap.put(userId, books);
//        }
//
//        for (Map.Entry<Long, List<Book>> entry : userBooksMap.entrySet()) {
//
//            Long userId = entry.getKey();
//            User user = userRepository.findById(userId).orElse(null);
//            List<Book> books = entry.getValue();
//
//            int postCount = 5 + random.nextInt(6); // 5–10 post
//        }
//
//        for (int i = 0; i < postCount; i++) {
//
//            Book book = books.get(random.nextInt(books.size())); // 100% có book
//
//            ReadingProgress progress = readingRepo
//                    .findByUser_UserIdAndBook_BookId(userId, book.getBookId())
//                    .orElse(null);
//
//            if (progress == null) {
//
//                progress = readingService.addToShelf(
//                        user,
//                        new ReadingAddRequest(
//                                book.getBookId(),
//                                0,
//                                ReadingStatus.READING
//                        )
//                );
//            }
//
//
//            int totalPages = book.getTotalPages();
//            int randomPage = 1 + random.nextInt(totalPages);
//
//            progress.setCurrentPage(randomPage);
//
//            float percent = Math.round(((randomPage * 100f) / totalPages) * 10) / 10f;
//            progress.setPercentDone(percent);
//
//            if (percent >= 100f) {
//                progress.setReadingStatus(ReadingStatus.FINISHED);
//                progress.setFinishedDate(LocalDateTime.now());
//            }
//
//            readingRepo.save(progress);
//
//            String fileName = "post_" + userId + "_" + System.currentTimeMillis() + ".jpg";
//            String imageUrl = "/posts/" + fileName;
//
//
//            boolean isSharePost = random.nextInt(12) == 0;
//
//            Post post;
//
//            if (isSharePost && postRepository.count() > 10) {
//
//                Post originalPost = getRandomExistingPost();
//
//                String newContent = generateShareContent(originalPost);
//
//                post = Post.builder()
//                        .user(user)
//                        .ownerId(user.getUserId())
//                        .content(newContent)
//                        .translatedText(null)
//                        .imageUrl(originalPost.getImageUrl())
//                        .hashtags(originalPost.getHashtags())
//                        .book(originalPost.getBook())
//                        .shareOf(originalPost.getPostId())
//                        .likesCount(random.nextInt(200))
//                        .commentsCount(random.nextInt(50))
//                        .sharesCount(random.nextInt(20))
//                        .views(random.nextInt(1500))
//                        .createdAt(LocalDateTime.now().minusDays(random.nextInt(15)))
//                        .updatedAt(LocalDateTime.now())
//                        .build();
//            } else {
//
//                // ===== POST THƯỜNG =====
//                String content = randomContent();
//                String translated = contentMap.get(content);
//
//                post = Post.builder()
//                        .user(user)
//                        .ownerId(user.getUserId())
//                        .content(content)
//                        .translatedText(translated)
//                        .imageUrl(generateImage(user.getUserId(), book))
//                        .hashtags(generateRandomHashtags())
//                        .book(book)
//                        .shareOf(null)
//                        .likesCount(random.nextInt(300))
//                        .commentsCount(random.nextInt(80))
//                        .sharesCount(random.nextInt(40))
//                        .views(random.nextInt(2000))
//                        .createdAt(LocalDateTime.now().minusDays(random.nextInt(20)))
//                        .updatedAt(LocalDateTime.now())
//                        .build();
//            }
//
//            postRepository.save(post);
//
//
//
//            posts.add(post);
//        }
//
//        return postRepository.saveAll(posts);
//    }
//
//    public void downloadImageFromBing(String keyword, String fileName) {
//
//        try {
//
//            String searchUrl = "https://www.bing.com/images/search?q="
//                               + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
//
//            Document doc = Jsoup.connect(searchUrl)
//                    .userAgent("Mozilla/5.0")
//                    .get();
//
//            Element img = doc.select("img.mimg").first();
//            if (img == null) return;
//
//            String imgUrl = img.attr("src");
//
//            InputStream in = new URL(imgUrl).openStream();
//
//            Path savePath = Paths.get(
//                    "D:\\BookHub_F_KIT2023\\BookHub_F_KIT2023\\bookhub_postgress\\uploads\\posts\\"
//                    + fileName
//            );
//
//            Files.copy(in, savePath, StandardCopyOption.REPLACE_EXISTING);
//
//            in.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String extractMeaningfulPart(String content) {
//        if (content == null || content.length() < 40) return content;
//
//        int start = random.nextInt(content.length() / 2);
//        int end = Math.min(start + 40 + random.nextInt(40), content.length());
//
//        String snippet = content.substring(start, end);
//
//        // Cắt về khoảng trắng gần nhất để không bị đứt chữ
//        int lastSpace = snippet.lastIndexOf(" ");
//        if (lastSpace > 20) {
//            snippet = snippet.substring(0, lastSpace);
//        }
//
//        return snippet.trim();
//    }
//
//    private String generateShareContent(Post originalPost) {
//
//        String snippet = extractMeaningfulPart(originalPost.getContent());
//        String template = shareTemplates.get(random.nextInt(shareTemplates.size()));
//
//        return String.format(template, snippet);
//    }
//
//    private Post getRandomExistingPost() {
//        long count = postRepository.count();
//        int index = random.nextInt((int) count);
//        return postRepository.findAll(PageRequest.of(index, 1))
//                .getContent()
//                .get(0);
//    }


}


