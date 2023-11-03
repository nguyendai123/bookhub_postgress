package com.thanhson.bookhup.controller;

import com.thanhson.bookhup.dto.SigninDto;
import com.thanhson.bookhup.dto.SignupDto;
import com.thanhson.bookhup.jwts.JwtUtils;
import com.thanhson.bookhup.model.ERole;
import com.thanhson.bookhup.model.Role;
import com.thanhson.bookhup.model.User;
import com.thanhson.bookhup.repository.RoleRepository;
import com.thanhson.bookhup.repository.UserRepository;
import com.thanhson.bookhup.response.JwtResponse;
import com.thanhson.bookhup.response.MessageResponse;
import com.thanhson.bookhup.service.EmailService;
import com.thanhson.bookhup.service.UserDetailsImpl;
import com.thanhson.bookhup.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")

public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SigninDto signinDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signinDto.getUsername(), signinDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAvatar(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupDto signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;

                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam("usernameOrEmail") String usernameOrEmail) {
        Boolean check = userService.checkUsernameOrEmailExisted(usernameOrEmail, usernameOrEmail);
        MessageResponse messageResponse = new MessageResponse();

        if (check) {
            String newPassword = userService.generateRandomPassword();
            userService.changePassword(usernameOrEmail, newPassword);
            boolean status = emailService.sendMailResetPassword(newPassword, usernameOrEmail);
            if (status) {
                messageResponse.setMessage("Sent mail to reset password successful.");
                return new ResponseEntity<>(messageResponse, HttpStatus.OK);
            } else {
                messageResponse.setMessage("Sent mail to reset password failed.");
                return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
            }
        } else {
            messageResponse.setMessage("Can not find user with username or email: " + usernameOrEmail);
            return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
        }
    }
}
