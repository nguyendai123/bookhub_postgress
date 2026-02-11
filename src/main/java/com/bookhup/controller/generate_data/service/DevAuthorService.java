package com.bookhup.controller.generate_data.service;

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
            String namecountry = randomAuthorNameByCountry(country);
            String name = generateUniqueAuthorName(namecountry, usedNames);

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

    private String randomBioByCountry(String country, String name) {
        String[] bioViet = {
                "là một tác giả nổi bật với nhiều tác phẩm được độc giả yêu thích.",
                "được biết đến với phong cách viết giàu cảm xúc và chiều sâu.",
                "có nhiều tác phẩm để lại dấu ấn mạnh mẽ trong lòng người đọc.",
                "chuyên sáng tác những câu chuyện giàu tính nhân văn.",
                "nổi tiếng với các tác phẩm truyền cảm hứng và ý nghĩa.",
                "có lối kể chuyện cuốn hút và đầy cảm xúc.",
                "được đánh giá cao nhờ những tác phẩm sâu sắc về con người và cuộc sống.",
                "là cây bút quen thuộc với đông đảo độc giả yêu văn học."
        };

        String moTa = bioViet[new Random().nextInt(bioViet.length)];

        return switch (country) {
            case "Việt Nam" -> name + " " + moTa;

            case "Mỹ", "Anh", "Pháp", "Đức", "Nhật Bản", "Hàn Quốc", "Trung Quốc" -> "Tác giả " + name + " " + moTa;

            default -> name + " là một tác giả có nhiều đóng góp cho văn học.";
        };
    }

    private String generateUniqueAuthorName(String name, Set<String> usedNames) {
        int suffix = 1;

        do {
            if (usedNames.contains(name) || authorRepository.existsByName(name)) {
                name = name + " " + suffix;
                suffix++;
            } else {
                break;
            }
        } while (true);

        return name;
    }


    private String randomCountry() {
        return QUOC_GIA[random.nextInt(QUOC_GIA.length)];
    }
}



