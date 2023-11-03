package com.thanhson.bookhup.controller;

import com.thanhson.bookhup.model.User;
import com.thanhson.bookhup.response.ResponseSuccess;
import com.thanhson.bookhup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")

@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
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
