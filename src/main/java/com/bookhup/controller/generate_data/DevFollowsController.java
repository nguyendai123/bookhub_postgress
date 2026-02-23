package com.bookhup.controller.generate_data;

import com.bookhup.controller.service.DevFollowService;
import com.bookhup.model.User;
import com.bookhup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevFollowsController {

    private final DevFollowService devFollowService;
    private final UserRepository userRepository;

    @PostMapping("/generate-follows")
    public String generateFollows() {
        List<User> users = userRepository.findAll();
        devFollowService.generateFollows(users);
        return "Generate follow test data thành công!";
    }

}
