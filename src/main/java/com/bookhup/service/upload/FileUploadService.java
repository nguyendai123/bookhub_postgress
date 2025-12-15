package com.bookhup.service.upload;

import com.bookhup.model.UploadType;
import com.bookhup.model.User;
import com.bookhup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final UserRepository userRepository;

    @Value("${upload.avatars-dir}")
    private String avatarsDir;

    @Value("${upload.posts-dir}")
    private String postsDir;

    @Value("${upload.book-covers-dir}")
    private String bookCoversDir;

    @Value("${upload.reviews-dir}")
    private String reviewsDir;

    /**
     * Upload file & trả về URL để lưu DB
     */
    public String upload(MultipartFile file, UploadType type, Long ownerId) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        try {
            String extension = getExtension(file.getOriginalFilename());

            String fileName = type.name().toLowerCase()
                              + "_" + ownerId
                              + "_" + System.currentTimeMillis()
                              + extension;

            UploadPath uploadPath = resolvePath(type);

            Path dir = Paths.get(uploadPath.dir());
            Files.createDirectories(dir);

            Path target = dir.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // URL lưu DB (KHÔNG có domain)
            return uploadPath.urlPrefix() + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

    /**
     * Upload & xử lý hậu logic (ví dụ avatar)
     */
    public String uploadFile(MultipartFile file, UploadType type, Long userId) {
        String url = upload(file, type, userId);

        if (type == UploadType.AVATAR) {
            User u = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            u.setAvatarUrl(url);
            userRepository.save(u);
        }

        return url;
    }

    // ===================== PRIVATE =====================

    private UploadPath resolvePath(UploadType type) {
        return switch (type) {
            case AVATAR -> new UploadPath(avatarsDir, "/avatars/");
            case POST -> new UploadPath(postsDir, "/posts/");
            case BOOKCOVERS -> new UploadPath(bookCoversDir, "/books/covers/");
            case REVIEW -> new UploadPath(reviewsDir, "/reviews/");
        };
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * Record map dir vật lý ↔ URL
     */
    private record UploadPath(String dir, String urlPrefix) {
    }
}


