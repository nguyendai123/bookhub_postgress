package com.bookhup.config;

import com.bookhup.model.Permission;
import com.bookhup.model.Role;
import com.bookhup.model.RoleType;
import com.bookhup.model.User;
import com.bookhup.repository.PermissionRepository;
import com.bookhup.repository.RoleRepository;
import com.bookhup.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bookhup.model.UserStatus.ACTIVE;

@Configuration
public class BookHubInitConfig {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BookHubInitConfig(RoleRepository roleRepository,
                             PermissionRepository permissionRepository,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {

            // ===============================
            // 1) TẠO FULL PERMISSION LIST
            // ===============================
            Set<Permission> allPermissions = new HashSet<>();

            // AUTH
            allPermissions.add(create("AUTH_REGISTER"));
            allPermissions.add(create("AUTH_LOGIN"));
            allPermissions.add(create("AUTH_LOGOUT"));
            allPermissions.add(create("AUTH_RESET_PASSWORD"));

            // USER
            allPermissions.add(create("USER_VIEW"));
            allPermissions.add(create("USER_UPDATE"));
            allPermissions.add(create("USER_DELETE"));
            allPermissions.add(create("USER_FOLLOW"));
            allPermissions.add(create("USER_UNFOLLOW"));

            // POST
            allPermissions.add(create("POST_CREATE"));
            allPermissions.add(create("POST_UPDATE"));
            allPermissions.add(create("POST_DELETE"));
            allPermissions.add(create("POST_VIEW"));
            allPermissions.add(create("POST_SHARE"));

            // COMMENT
            allPermissions.add(create("COMMENT_CREATE"));
            allPermissions.add(create("COMMENT_REPLY"));
            allPermissions.add(create("COMMENT_DELETE"));
            allPermissions.add(create("COMMENT_LIKE"));

            // HASHTAG & STATS
            allPermissions.add(create("HASHTAG_CREATE"));
            allPermissions.add(create("POST_STATS_VIEW"));

            // BOOK
            allPermissions.add(create("BOOK_SEARCH"));
            allPermissions.add(create("BOOK_VIEW"));
            allPermissions.add(create("BOOK_VIEW_TRENDING"));

            // ADMIN – BOOK MANAGEMENT
            allPermissions.add(create("ADMIN_BOOK_CREATE"));
            allPermissions.add(create("ADMIN_BOOK_UPDATE"));
            allPermissions.add(create("ADMIN_BOOK_DELETE"));

            // REVIEW
            allPermissions.add(create("REVIEW_CREATE"));
            allPermissions.add(create("REVIEW_UPDATE"));
            allPermissions.add(create("REVIEW_DELETE"));
            allPermissions.add(create("REVIEW_LIKE"));
            allPermissions.add(create("REVIEW_SHARE"));
            allPermissions.add(create("REVIEW_VIEW"));

            // REVIEW MEDIA
            allPermissions.add(create("REVIEW_MEDIA_UPLOAD"));
            allPermissions.add(create("REVIEW_MEDIA_DELETE"));

            // READING
            allPermissions.add(create("READING_ADD"));
            allPermissions.add(create("READING_UPDATE"));
            allPermissions.add(create("READING_VIEW"));

            // NOTIFICATION
            allPermissions.add(create("NOTIFICATION_VIEW"));

            // SOCIAL
            allPermissions.add(create("FOLLOW_CREATE"));
            allPermissions.add(create("FOLLOW_DELETE"));

            // AI ENGINE
            allPermissions.add(create("AI_RECOMMENDATION_VIEW"));
            allPermissions.add(create("AI_SUMMARY_GENERATE"));
            allPermissions.add(create("AI_HIGHLIGHT_GENERATE"));
            allPermissions.add(create("AI_INTERACTION_QUERY"));


            // ===============================
            // 2) ROLE SETUP
            // ===============================

            // ---- USER ROLE ----
            Role userRole = createRole(RoleType.USER,
                    allPermissions.stream()
                            .filter(p -> !p.getPermissionName().startsWith("ADMIN_"))
                            .collect(Collectors.toSet())
            );

            // ---- ADMIN ROLE ----
            Role adminRole = createRole(RoleType.ADMIN, allPermissions);


            // ===============================
            // 3) ADMIN ACCOUNT
            // ===============================
            if (!userRepository.existsByEmail("admin@bookhub.com")) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@bookhub.com")
                        .passwordHash(passwordEncoder.encode("1232001"))
                        .createdAt(LocalDateTime.now())
                        .isAdmin(true)
                        .status(ACTIVE)
                        .roles(new HashSet<>())
                        .build();

                admin.setCreatedAt(LocalDateTime.now());
                admin.getRoles().add(adminRole);
                userRepository.save(admin);

                User normalUser = User.builder()
                        .username("dainv")
                        .email("nguyenvandai1232001@gmail.com")
                        .passwordHash(passwordEncoder.encode("1232001"))
                        .createdAt(LocalDateTime.now())
                        .isAdmin(false)
                        .status(ACTIVE)
                        .roles(new HashSet<>())
                        .build();

                normalUser.getRoles().add(userRole);
                userRepository.save(normalUser);

            }
        };
    }

    // Create permission safely
    private Permission create(String name) {
        return permissionRepository.findByPermissionName(name)
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setPermissionName(name);
                    p.setCreatedAt(LocalDateTime.now());
                    return permissionRepository.save(p);
                });
    }

    // Create role safely
    private Role createRole(RoleType roleName, Set<Permission> permissions) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setRoleName(roleName);
                    r.setCreatedAt(LocalDateTime.now());
                    r.setPermissions(permissions);
                    return roleRepository.save(r);
                });
    }
}


