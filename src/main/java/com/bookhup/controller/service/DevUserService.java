package com.bookhup.controller.service;
import com.bookhup.event.UserRegisteredEvent;
import com.bookhup.model.Role;
import com.bookhup.model.RoleType;
import com.bookhup.model.User;
import com.bookhup.model.UserStatus;
import com.bookhup.repository.RoleRepository;
import com.bookhup.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.text.Normalizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DevUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final ExecutorService imageExecutor =
            Executors.newFixedThreadPool(8); // giới hạn 5 luồng


    private final Random random = new Random();

    private static final String[] HO = {
            "Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ",
            "Đặng", "Bùi", "Đỗ", "Hồ", "Ngô", "Dương", "Lý", "Đinh", "Mai",
            "Trương", "Tạ", "Châu", "Cao", "Hà", "Tô", "Lương", "La", "Lưu",
            "Thái", "Chung", "Phùng", "Vương", "Quách", "Tăng", "Hứa", "Kiều"
    };
    private static final String[] TEN = {
            "An", "Anh", "Ân", "Ánh", "Bảo", "Bình", "Châu", "Chi", "Công",
            "Cường", "Dũng", "Duy", "Đạt", "Đức", "Giang", "Hà", "Hải", "Hân",
            "Hiếu", "Hiền", "Hoài", "Hòa", "Hồng", "Hùng", "Hương", "Huy",
            "Khánh", "Khoa", "Kiên", "Lan", "Linh", "Loan", "Long", "Mai",
            "Minh", "My", "Nam", "Nga", "Ngân", "Ngọc", "Nhật", "Nhung",
            "Phát", "Phong", "Phúc", "Phương", "Quân", "Quang", "Quỳnh",
            "Sơn", "Tâm", "Tân", "Thảo", "Thi", "Thiện", "Thu", "Thủy",
            "Tiến", "Trang", "Trí", "Trinh", "Trung", "Tú", "Tuấn", "Uyên",
            "Vân", "Vi", "Việt", "Vinh", "Vy", "Xuân", "Yến"
    };

    private static final String[] THE_LOAI = {
            "Tiểu thuyết", "Trinh thám", "Khoa học viễn tưởng", "Ngôn tình",
            "Kỹ năng sống", "Lịch sử", "Thiếu nhi", "Kinh doanh", "Tâm lý học"
    };

    private static final String[] MO_TA_BIO = {
            "Mình rất thích đọc sách vào buổi tối.",
            "Đọc sách là cách mình thư giãn sau giờ làm.",
            "Nghiện truyện trinh thám và tiểu thuyết.",
            "Thích khám phá những cuốn sách truyền cảm hứng.",
            "Mỗi ngày không đọc sách là thấy thiếu thiếu.",

            "Cuối tuần mà có sách và cà phê là đủ vui rồi.",
            "Đọc sách giúp mình mở rộng góc nhìn về cuộc sống.",
            "Mình thích những câu chuyện nhẹ nhàng và sâu lắng.",
            "Sách là người bạn đồng hành không thể thiếu.",
            "Đang cố gắng đọc nhiều hơn mỗi ngày.",
            "Thích đọc sách giấy hơn là đọc trên màn hình.",
            "Mê nhất mấy cuốn sách về tâm lý và kỹ năng sống.",
            "Đọc sách để hiểu mình và hiểu người hơn.",
            "Mỗi cuốn sách là một chuyến đi mới.",
            "Thích cảm giác đắm chìm trong một câu chuyện hay.",
            "Sách hay là phải đọc chậm để ngẫm.",
            "Mình thường đọc sách trước khi đi ngủ.",
            "Đọc sách giúp mình bớt stress hơn.",
            "Thích những cuốn sách khiến mình phải suy nghĩ nhiều.",
            "Vừa đọc sách vừa nghe nhạc nhẹ là hết nước chấm.",
            "Sưu tầm sách cũng là một niềm vui nho nhỏ.",
            "Có thể không online, nhưng nhất định phải có sách.",
            "Thích những câu chuyện đời thường nhưng ý nghĩa.",
            "Đọc sách là thói quen mình đang cố duy trì mỗi ngày.",
            "Một ngày đẹp là một ngày có thời gian đọc sách.",
            "Thích đọc lại những cuốn sách mình từng yêu thích.",
            "Đọc sách giúp mình học được nhiều điều mới.",
            "Sách là cách mình trốn khỏi thế giới ồn ào.",
            "Mình thích những cuốn sách có nhân vật sâu sắc.",
            "Đọc sách xong là chỉ muốn giới thiệu cho mọi người cùng đọc."
    };


    private static final String[] KIEU_DOC = {
            "Đọc buổi tối", "Đọc cuối tuần", "Đọc mỗi ngày", "Đọc khi rảnh"
    };

    @Transactional
    public void generateUsers(int count, String rawPassword) {

        Role roleUser = roleRepository.findByRoleName(RoleType.USER)
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        String encodedPassword = passwordEncoder.encode(rawPassword);
        List<User> users = new ArrayList<>();

        Set<String> usedUsernames = new HashSet<>();
        Set<String> usedEmails = new HashSet<>();

        for (int i = 0; i < count; i++) {

            String username = generateUniqueUsername(usedUsernames);
            String email = generateUniqueEmail(username, usedEmails);

            String avatarFileName = "avatar_" + username + ".jpg";

            // 🔥 Tạo keyword avatar random
            String keyword = randomAvatarKeyword();

            imageExecutor.submit(() -> {
                try {
                    downloadImageFromBing(keyword, avatarFileName, "avatars");
                } catch (Exception e) {
                    System.err.println("Download image failed: " + e.getMessage());
                }
            });

            User user = User.builder()
                    .username(username)
                    .email(email)
                    .passwordHash(encodedPassword)
                    .createdAt(LocalDateTime.now())
                    .status(UserStatus.ACTIVE)
                    .isAdmin(false)
                    .xpPoints(random.nextInt(5000))
                    .level(random.nextInt(20) + 1)
                    .bio(randomBio())
                    .avatarUrl("/avatars/" + avatarFileName)
                    .favoriteGenres(randomGenresJson())
                    .readingPattern(randomKieuDoc())
                    .preferredLanguage("vi")
                    .avgReadTimePerDay(10f + random.nextFloat() * 120f)
                    .aiClusterSegment("DOC_GIA_PHOTHONG")
                    .roles(new HashSet<>(Set.of(roleUser)))
                    .build();

            users.add(user);

            usedUsernames.add(username);
            usedEmails.add(email);
        }


        userRepository.saveAll(users);
        users.forEach(u -> eventPublisher.publishEvent(new UserRegisteredEvent(u)));
    }
    private final Set<String> usedAvatarKeywords = new HashSet<>();

    private String randomAvatarKeyword() {

        String[] genders = {
                "man", "woman",
                "male", "female",
                "guy", "lady",
                "person"
        };

        String[] ages = {
                "teen", "young", "young adult",
                "adult", "middle aged",
                "mature", "senior"
        };

        String[] styles = {
                "profile photo",
                "profile picture",
                "professional headshot",
                "studio portrait",
                "linkedin profile photo",
                "facebook profile picture",
                "instagram profile photo",
                "passport style photo",
                "corporate headshot",
                "natural light portrait"
        };

        String[] ethnicities = {
                "asian", "east asian", "southeast asian",
                "european", "white",
                "african", "black",
                "latino", "hispanic",
                "middle eastern",
                "indian", "korean", "japanese", "chinese"
        };

        String[] moods = {
                "smiling",
                "serious",
                "neutral expression",
                "natural look",
                "friendly face",
                "confident look",
                "casual look",
                "relaxed expression"
        };
        String[] qualities = {
                "high resolution",
                "4k",
                "HD",
                "sharp focus",
                "detailed face",
                "professional photography",
                "DSLR photo"
        };


        String keyword;

        do {
            keyword =
                    ages[random.nextInt(ages.length)] + " " +
                    ethnicities[random.nextInt(ethnicities.length)] + " " +
                    genders[random.nextInt(genders.length)] + " " +
                    moods[random.nextInt(moods.length)] + " " +
                    styles[random.nextInt(styles.length)] + " " +
                    qualities[random.nextInt(qualities.length)] + " portrait photo " +
                    random.nextInt(10000);
        }
        while (!usedAvatarKeywords.add(keyword)); // tránh trùng hoàn toàn

        return keyword;
    }



    public void downloadImageFromBing(String keyword,
                                      String fileName,
                                      String folder) {
        try {
            String searchUrl = "https://www.bing.com/images/search?q=" +
                               URLEncoder.encode(keyword, StandardCharsets.UTF_8);

            Document doc = Jsoup.connect(searchUrl).userAgent("Mozilla/5.0").get();
            Element img = doc.select("img.mimg").first();
            if (img == null) return;

            String imgUrl = img.attr("src");

            InputStream in = new URL(imgUrl).openStream();

            Path uploadDir = Paths.get("uploads", folder);
            Files.createDirectories(uploadDir);

            Path savePath = uploadDir.resolve(fileName);
            Files.copy(in, savePath, StandardCopyOption.REPLACE_EXISTING);

            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String taoUsernameTuTen(String fullName) {
        String khongDau = boDau(fullName).toLowerCase();
        return khongDau.replaceAll("\\s+", "");
    }


    private String boDau(String input) {
        String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    private String generateUniqueEmail(String username, Set<String> usedEmails) {
        String email = username + "@bookhub.vn";
        int suffix = 1;

        while (usedEmails.contains(email) || userRepository.existsByEmail(email)) {
            email = username + suffix + "@bookhub.vn";
            suffix++;
        }
        return email;
    }

    private String generateUniqueUsername(Set<String> usedUsernames) {
        String username;
        int maxAttempts = 20; // tránh loop vô hạn
        int attempts = 0;

        do {
            username = randomUsernameRealistic(); // hàm tạo username mới
            attempts++;

            if (attempts > maxAttempts) {
                // fallback nếu hiếm khi bị trùng liên tục
                username = username + random.nextInt(10000);
                break;
            }

        } while (usedUsernames.contains(username) || userRepository.existsByUsername(username));

        return username;
    }
    private String randomUsernameRealistic() {
        String ho = HO[random.nextInt(HO.length)];
        String ten = TEN[random.nextInt(TEN.length)];

        // bỏ dấu + viết thường + bỏ khoảng trắng
        String base = (ho + ten).toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll("[áàạảãăắằặẳẵâấầậẩẫ]", "a")
                .replaceAll("[éèẹẻẽêếềệểễ]", "e")
                .replaceAll("[íìịỉĩ]", "i")
                .replaceAll("[óòọỏõôốồộổỗơớờợởỡ]", "o")
                .replaceAll("[úùụủũưứừựửữ]", "u")
                .replaceAll("[ýỳỵỷỹ]", "y");

        // 50% thêm số phía sau cho tự nhiên
        if (random.nextBoolean()) {
            base += random.nextInt(100);
        }

        return base;
    }



    private String randomHoTen() {
        return HO[random.nextInt(HO.length)] + " " + TEN[random.nextInt(TEN.length)];
    }

    private String randomBio() {
        return MO_TA_BIO[random.nextInt(MO_TA_BIO.length)];
    }

    private String randomGenresJson() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(THE_LOAI[random.nextInt(THE_LOAI.length)]);
        }
        return new ObjectMapper().valueToTree(list).toString();
    }

    private String randomKieuDoc() {
        return KIEU_DOC[random.nextInt(KIEU_DOC.length)];
    }
}

