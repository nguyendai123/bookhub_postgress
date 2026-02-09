
package com.bookhup.controller.service;

import com.bookhup.model.Author;
import com.bookhup.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


import com.bookhup.model.Genre;
import com.bookhup.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DevGenreService {

    private final GenreRepository genreRepository;
    private final Random random = new Random();

    private static final String[] GENRE_NAMES = {
            "Tiểu thuyết", "Ngôn tình", "Trinh thám", "Kinh dị", "Giả tưởng",
            "Khoa học viễn tưởng", "Phiêu lưu", "Hành động", "Tâm lý", "Tình cảm",
            "Lịch sử", "Hồi ký", "Tự truyện", "Văn học cổ điển", "Văn học hiện đại",
            "Thiếu nhi", "Tuổi teen", "Kỹ năng sống", "Kinh doanh", "Marketing",
            "Tài chính", "Đầu tư", "Công nghệ", "Lập trình", "Trí tuệ nhân tạo",
            "Y học", "Sức khỏe", "Ẩm thực", "Du lịch", "Văn hóa",
            "Triết học", "Xã hội học", "Tâm linh", "Nghệ thuật", "Âm nhạc",
            "Nhiếp ảnh", "Thiết kế", "Thời trang", "Thể thao", "Giáo dục",
            "Ngôn ngữ", "Khoa học", "Sinh học", "Vật lý", "Hóa học",
            "Toán học", "Môi trường", "Khởi nghiệp", "Lãnh đạo", "Phát triển bản thân",

            "Chính trị", "Pháp luật", "Tôn giáo", "Thiền", "Yoga",
            "Nuôi dạy con", "Gia đình", "Tình bạn", "Tình yêu", "Giao tiếp",
            "Thuyết trình", "Đàm phán", "Quản lý thời gian", "Tư duy phản biện", "Sáng tạo",
            "Kỹ năng mềm", "Viết lách", "Báo chí", "Truyền thông", "PR",
            "Quảng cáo", "Thương mại điện tử", "Khởi sự doanh nghiệp", "Bán hàng", "Chứng khoán",
            "Bất động sản", "Blockchain", "An ninh mạng", "Khoa học dữ liệu", "Phân tích dữ liệu",
            "Machine Learning", "Deep Learning", "Robotics", "Internet vạn vật", "Lập trình web",
            "Lập trình di động", "Game", "Thiết kế đồ họa", "UI/UX", "Nghệ thuật số",
            "Hội họa", "Điêu khắc", "Kiến trúc", "Nội thất", "Làm vườn",
            "Chăm sóc thú cưng", "Nấu ăn chay", "Làm bánh", "Pha chế", "Dinh dưỡng",
            "Thể hình", "Chạy bộ", "Bóng đá", "Bóng rổ", "Võ thuật",
            "Cờ vua", "Leo núi", "Cắm trại", "Sinh tồn", "Hướng dẫn du lịch",
            "Khám phá thế giới", "Địa lý", "Thiên văn học", "Khảo cổ học", "Nhân học",
            "Tâm lý học tội phạm", "Điều tra", "Gián điệp", "Chiến tranh", "Quân sự"
    };


    @Transactional
    public List<Genre> generateGenres(int count) {

        Set<String> usedNames = new HashSet<>();
        List<Genre> genres = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String name = generateUniqueGenreName(usedNames);

            Genre genre = Genre.builder()
                    .name(name)
                    .ownerId(1L)
                    .build();

            genres.add(genre);
            usedNames.add(name);
        }

        return genreRepository.saveAll(genres);
    }

    /**
     * Tạo tên genre không trùng DB và không trùng trong batch
     */
    private String generateUniqueGenreName(Set<String> usedNames) {
        String baseName;
        String finalName;
        int suffix = 1;

        do {
            baseName = GENRE_NAMES[random.nextInt(GENRE_NAMES.length)];
            finalName = baseName;


        } while (usedNames.contains(finalName)
                 || genreRepository.findByName(finalName).isPresent());

        return finalName;
    }
}
