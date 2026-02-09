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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.text.Normalizer;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DevUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

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

            String fullName = randomHoTen();
            String baseUsername = taoUsernameTuTen(fullName);

            String username = generateUniqueUsername(usedUsernames);
            String email = generateUniqueEmail(username, usedEmails);

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
                    .avatarUrl("avatar_" + (i + 1))
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

