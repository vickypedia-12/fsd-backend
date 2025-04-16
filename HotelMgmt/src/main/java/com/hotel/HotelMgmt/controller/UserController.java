package com.hotel.HotelMgmt.controller;

import com.hotel.HotelMgmt.entity.User;
import com.hotel.HotelMgmt.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

// import javax.mail.*;
// import javax.mail.internet.InternetAddress;
// import javax.mail.internet.MimeMessage;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Secret key for JWT (you should store this in a secure location)
    private static final String SECRET_KEY = "your-secret-key-ayush";

    // Temporary storage for OTPs (in production, use a persistent store or cache
    // like Redis)
    private static final Map<String, String> otpStorage = new HashMap<>();

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody User user) {
        System.out.println("Hello, world!");

        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists!");
        }

        // Hash the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);
        userRepository.save(user);

        // Generate OTP and send to user's email
        String otp = generateOtp();
        otpStorage.put(user.getEmail(), otp);
        sendOtpEmail(user.getEmail(), otp);

        return ResponseEntity.ok("Signup successful! Please verify your email with the OTP sent.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        // Check if OTP matches
        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            otpStorage.remove(email); // Remove OTP after successful verification
            return ResponseEntity.ok("Email verified successfully!");
        }

        return ResponseEntity.badRequest().body("Invalid OTP or email!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        // Find user by email
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found!");
        }

        User user = optionalUser.get();

        // Check if password matches
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password!");
        }
        Key key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());

        // Generate JWT token
        String token = Jwts.builder()
                .setSubject("User Authentication")
                .claim("user_id", user.getId())
                .claim("email", user.getEmail())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(30 * 24 * 60 * 60))) // Token expires in 30 days
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Set token as HTTP-only cookie
        ResponseCookie cookie = ResponseCookie.from("auth_token", token)
                .httpOnly(true)
                .secure(true) // Set this to true in production for HTTPS
                .path("/")
                .maxAge(30 * 24 * 60 * 60) // 30 days
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body("Login successful!");
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    // Forgot Password - Send OTP
    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> sendForgotPasswordOtp(@RequestParam String email) {
        // Validate if the email exists
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Email not found!");
        }

        // Generate OTP and send to user's email
        String otp = generateOtp();
        otpStorage.put(email, otp);
        sendOtpEmail(email, otp);

        return ResponseEntity.ok("OTP sent to the email.");
    }

    // Forgot Password - Verify OTP
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> verifyForgotPasswordOtp(@RequestParam String email, @RequestParam String otp) {
        // Check if OTP matches
        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            return ResponseEntity.ok("OTP verified successfully!");
        }

        return ResponseEntity.badRequest().body("Invalid OTP or email!");
    }

    // Forgot Password - Reset Password
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) {
        // Verify OTP
        if (!otpStorage.containsKey(email) || !otpStorage.get(email).equals(otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP or email!");
        }

        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Email not found!");
        }

        // Reset password
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Remove OTP after successful password reset
        otpStorage.remove(email);

        return ResponseEntity.ok("Password reset successful!");
    }

    private void sendOtpEmail(String toEmail, String otp) {
        // Email credentials
        final String username = "ayushmore8652@gmail.com";
        final String password = "oazn kvgf kpvj nbhu";

        // Email configuration
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toEmail));
            message.setSubject("Your OTP for Email Verification");
            message.setText("Your OTP is: " + otp + "\n\nThis OTP is valid for 10 minutes.");

            // Send email
            Transport.send(message);
            System.out.println("OTP email sent to " + toEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send OTP email");
        }
    }
}