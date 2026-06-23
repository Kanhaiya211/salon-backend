package com.salon.salon_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.salon.salon_backend.config.JwtProvider;
import com.salon.salon_backend.dto.ForgotPasswordRequest;
import com.salon.salon_backend.dto.LoginRequest;
import com.salon.salon_backend.dto.SignupRequest;
import com.salon.salon_backend.entity.User;
import com.salon.salon_backend.repository.UserRepository;
import com.salon.salon_backend.service.EmailService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;
    
    private final Map<String, SignupRequest>
    signupCache = new HashMap<>();

private final Map<String, String>
    otpCache = new HashMap<>();

   // SIGNUP API
   // @PostMapping("/signup")
   // public String signup(
   //         @RequestBody User user
   // ) {

   //     // EMAIL EXISTS

   //     if (
   //             userRepository
   //                     .findByEmail(
   //                             user.getEmail()
   //                     )
   //                     .isPresent()
   //     ) {

   //         return "Email already exists";
   //     }

   //     // PASSWORD ENCRYPT

   //     user.setPassword(

   //             passwordEncoder.encode(
   //                     user.getPassword()
   //             )
   //     );

   //     // GENERATE OTP

   //     String otp =
   //             generateOtp();

   //     user.setOtp(otp);

   //     user.setVerified(false);

   //     user.setOtpExpiry(

   //             java.time.LocalDateTime.now()
   //                     .plusMinutes(5)
   //     );

   //     // SAVE USER

   //     userRepository.save(user);

   //     // SEND EMAIL

   //     emailService.sendOtpEmail(
   //             user.getEmail(),
   //             otp
   //     );

   //     return "OTP Sent To Email";
   // }

//     @PostMapping("/signup")
// public String signup(@RequestBody String body) {

//     System.out.println("BODY = " + body);

//     return "OK";
// }

   @PostMapping("/signup")
public String signup(@RequestBody SignupRequest request) {

    // Check if email already exists
    if (userRepository.existsByEmail(request.getEmail())) {
        return "Email already registered";
    }

    String otp = generateOtp();

    User user = new User();

    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());

    user.setVerified(false);
    user.setOtp(otp);
    user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

    userRepository.save(user);

    emailService.sendOtpEmail(
            user.getEmail(),
            otp
    );

    return "OTP Sent";
}
   //  // LOGIN API
@PostMapping("/login")
public Map<String, String> login(
        @RequestBody LoginRequest request
) {

    authenticationManager.authenticate(

            new UsernamePasswordAuthenticationToken(

                    request.getEmail(),

                    request.getPassword()
            )
    );

    User loggedInUser =
            userRepository
                    .findByEmail(
                            request.getEmail()
                    )
                    .orElseThrow(() ->

                            new RuntimeException(
                                    "User Not Found"
                            )
                    );

    // EMAIL VERIFICATION CHECK

    if (!loggedInUser.isVerified()) {

        throw new RuntimeException(
                "Email Not Verified"
        );
    }

    String token =
            JwtProvider.generateToken(
                    request.getEmail()
            );

    Map<String, String> response =
            new HashMap<>();

    response.put(
            "token",
            token
    );

    response.put(
            "role",
            loggedInUser
                    .getRole()
                    .name()
    );

    return response;
}
    @GetMapping("/test-email")
    public String testEmail() {

        emailService.sendOtpEmail(

                "kanhaiyadhongade14@gmail.com",

                "123456"
        );

        return "Email Sent";
    }
    private String generateOtp() {

        return String.valueOf(

                (int)(
                        100000
                                +
                                Math.random() * 900000
                )
        );
    }
    
    // @PostMapping("/send-signup-otp")
    // public String sendSignupOtp(

    //         @RequestBody SignupRequest request
    // ) {

    //     // EMAIL EXISTS

    //     if (
    //             userRepository
    //                     .findByEmail(
    //                             request.getEmail()
    //                     )
    //                     .isPresent()
    //     ) {

    //         return "Email already exists";
    //     }

    //     // GENERATE OTP

    //     String otp =
    //             generateOtp();

    //     // STORE TEMP DATA

    //     signupCache.put(
    //             request.getEmail(),
    //             request
    //     );

    //     otpCache.put(
    //             request.getEmail(),
    //             otp
    //     );
    //     otpExpiryCache.put(

    //             request.getEmail(),

    //             LocalDateTime.now()
    //                     .plusMinutes(5)
    //     );

    //     // SEND EMAIL

    //     emailService.sendOtpEmail(
    //             request.getEmail(),
    //             otp
    //     );

    //     return "OTP Sent";
    // }
    
   @PostMapping("/verify-signup-otp")
public String verifySignupOtp(
        @RequestBody SignupRequest request
) {

    User user = userRepository
            .findByEmail(request.getEmail())
            .orElse(null);

    if (user == null) {
        return "User Not Found";
    }

    if (user.isVerified()) {
        return "User Already Verified";
    }

    if (user.getOtp() == null) {
        return "OTP Expired";
    }

    if (user.getOtpExpiry() == null ||
            LocalDateTime.now().isAfter(user.getOtpExpiry())) {

        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return "OTP Expired";
    }

    if (!user.getOtp().equals(request.getOtp())) {
        return "Invalid OTP";
    }

    user.setVerified(true);
    user.setOtp(null);
    user.setOtpExpiry(null);

    userRepository.save(user);

    return "Email Verified Successfully";
}
    
    @PostMapping("/forgot-password/send-otp")
    public String sendForgotPasswordOtp(

            @RequestBody
            ForgotPasswordRequest request
    ) {

        User user =
                userRepository
                        .findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(() ->

                                new RuntimeException(
                                        "User Not Found"
                                )
                        );

        String otp =
                generateOtp();

        forgotPasswordOtpCache.put(
                request.getEmail(),
                otp
        );

        emailService.sendOtpEmail(

                request.getEmail(),

                otp
        );

        return "OTP Sent";
    }
    
    @PostMapping("/forgot-password/reset")
    public String resetPassword(

            @RequestBody
            ForgotPasswordRequest request
    ) {

        String storedOtp =
                forgotPasswordOtpCache.get(
                        request.getEmail()
                );

        if (
                storedOtp == null
        ) {

            return "OTP Expired";
        }

        if (
                !storedOtp.equals(
                        request.getOtp()
                )
        ) {

            return "Invalid OTP";
        }

        User user =
                userRepository
                        .findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(() ->

                                new RuntimeException(
                                        "User Not Found"
                                )
                        );

        user.setPassword(

                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        forgotPasswordOtpCache.remove(
                request.getEmail()
        );

        return "Password Reset Successful";
    }
    private final Map<String, LocalDateTime>
    otpExpiryCache =
    new HashMap<>();
    
    @PostMapping("/resend-signup-otp")
public String resendSignupOtp(
        @RequestBody SignupRequest request
) {

    User user = userRepository
            .findByEmail(request.getEmail())
            .orElse(null);

    if (user == null) {
        return "User Not Found";
    }

    if (user.isVerified()) {
        return "User Already Verified";
    }

    String otp = generateOtp();

    user.setOtp(otp);
    user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

    userRepository.save(user);

    emailService.sendOtpEmail(
            user.getEmail(),
            otp
    );

    return "OTP Resent Successfully";
}
}
