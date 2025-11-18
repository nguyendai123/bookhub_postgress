package com.bookhup.controller;

import com.bookhup.model.User;
import com.bookhup.response.ResponseSuccess;
import com.bookhup.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseSuccess<User>> updateUser(@PathVariable("id") Long id,
                                                            @RequestBody User userInfoRequest) {
        User userResponse = userService.updateUser(id, userInfoRequest);
        ResponseSuccess<User> responseSuccess = new ResponseSuccess<>();
        responseSuccess.setMessage("Updated user successfully.");
        responseSuccess.setData(userResponse);
        return ResponseEntity.ok(responseSuccess);
    }
}
