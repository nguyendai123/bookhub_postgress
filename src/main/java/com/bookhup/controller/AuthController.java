package com.bookhup.controller;//package com.bookhup.controller;
//
//import com.bookhup.repository.RoleRepository;
//import com.bookhup.repository.UserRepository;
//import com.bookhup.model.RoleType;
//import com.bookhup.model.Role;
//import com.bookhup.model.User;
//import com.bookhup.response.MessageResponse;
//import com.bookhup.service.auth.UserService;
//import com.bookhup.service.impl.EmailServiceImpl;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@CrossOrigin(origins = "*", maxAge = 3600)
//@RestController
//@RequestMapping("/api/auth")
//
//public class AuthController {
//    @Autowired
//    AuthenticationManager authenticationManager;
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    UserService userService;
//
//    @Autowired
//    EmailServiceImpl emailService;
//
//    @Autowired
//    RoleRepository roleRepository;
//
//    @Autowired
//    PasswordEncoder encoder;
//
//    @Autowired
//    JwtUtils jwtUtils;
//
////    @PostMapping("/login")
////    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SigninDto signinDto) {
////
////        Authentication authentication = authenticationManager.authenticate(
////                new UsernamePasswordAuthenticationToken(signinDto.getUsername(), signinDto.getPassword()));
////
////        SecurityContextHolder.getContext().setAuthentication(authentication);
////        String jwt = jwtUtils.generateJwtToken(authentication);
////
////        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
////        List<String> roles = userDetails.getAuthorities().stream()
////                .map(item -> item.getAuthority())
////                .collect(Collectors.toList());
////
////        return ResponseEntity.ok(new JwtResponse(jwt,
////                userDetails.getId(),
////                userDetails.getUsername(),
////                userDetails.getEmail(),
////                userDetails.getAvatar(),
////                roles));
////    }
//
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupDto signUpRequest) {
//        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Error: Username is already taken!"));
//        }
//
//        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Error: Email is already in use!"));
//        }
//
//        // Create new user's account
//        User user = new User(signUpRequest.getUsername(),
//                signUpRequest.getEmail(),
//                encoder.encode(signUpRequest.getPassword()));
//
//        Set<String> strRoles = signUpRequest.getRole();
//        Set<Role> roles = new HashSet<>();
//
//        if (strRoles == null) {
//            Role userRole = roleRepository.findByName(RoleType.USER)
//                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//            roles.add(userRole);
//        } else {
//            strRoles.forEach(role -> {
//                switch (role) {
//                    case "admin":
//                        Role adminRole = roleRepository.findByName(RoleType.ADMIN)
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(adminRole);
//                        break;
//
//                    default:
//                        Role userRole = roleRepository.findByName(RoleType.USER)
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(userRole);
//                }
//            });
//        }
//
//        user.setRoles(roles);
//        userRepository.save(user);
//
//        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
//    }
//
//    @GetMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@RequestParam("usernameOrEmail") String usernameOrEmail) {
//        Boolean check = userService.checkUsernameOrEmailExisted(usernameOrEmail, usernameOrEmail);
//        MessageResponse messageResponse = new MessageResponse();
//
//        if (check) {
//            String newPassword = userService.generateRandomPassword();
//            userService.changePassword(usernameOrEmail, newPassword);
//            boolean status = emailService.sendMailResetPassword(newPassword, usernameOrEmail);
//            if (status) {
//                messageResponse.setMessage("Sent mail to reset password successful.");
//                return new ResponseEntity<>(messageResponse, HttpStatus.OK);
//            } else {
//                messageResponse.setMessage("Sent mail to reset password failed.");
//                return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
//            }
//        } else {
//            messageResponse.setMessage("Can not find user with username or email: " + usernameOrEmail);
//            return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
//        }
//    }
//}


import com.bookhup.request.auth.LoginRequest;
import com.bookhup.request.auth.RegisterRequest;
import com.bookhup.request.auth.ResetPasswordRequest;
import com.bookhup.response.MessageResponse;
import com.bookhup.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(authService.logout());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        MessageResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
}

