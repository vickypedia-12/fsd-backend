package com.hotel.HotelMgmt.controller;

import com.hotel.HotelMgmt.entity.Admin;
import com.hotel.HotelMgmt.entity.User;
import com.hotel.HotelMgmt.repository.AdminRepository;
import com.hotel.HotelMgmt.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import java.util.*;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Temporary storage for OTPs
    private static final Map<String, String> otpStorage = new HashMap<>();

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody User user) {
        try {
            // Check if email already exists
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists!");
            }

            // Hash the password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(User.Role.ADMIN);
            userRepository.save(user);

            // Create admin entry
            Admin admin = new Admin();
            admin.setUser(user);

            // Generate OTP secret
            admin.setOtpSecret(generateOtpSecret()); // Set the otpSecret value
            adminRepository.save(admin);

            // Send OTP to email for verification
            String otp = generateOtp();
            otpStorage.put(user.getEmail(), otp);
            sendOtpEmail(user.getEmail(), otp);

            return ResponseEntity.ok("Signup successful! Please verify your email with the OTP sent.");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Generate a random OTP secret
    private String generateOtpSecret() {
        // You can use a secure random generator or a library for better security
        return UUID.randomUUID().toString();
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
    public ResponseEntity<?> login(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request) {
        // Find admin by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty() || !userOptional.get().getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Admin not found!");
        }

        User user = userOptional.get();

        // Check if password matches
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password!");
        }

        // Generate OTP for 2FA
        String otp = generateOtp();
        otpStorage.put(email, otp);
        sendOtpEmail(email, otp);

        // Track IP and Location
        String ipAddress = request.getRemoteAddr(); // Get IP address
        String location = getLocationFromIP(ipAddress); // Simulate location retrieval

        // Save IP and location to admin record
        Admin admin = adminRepository.findByUserId(user.getId()).orElseThrow();
        admin.setLastLoginIp(ipAddress);
        admin.setLastLoginLocation(location);
        adminRepository.save(admin);

        // Send Login Alert Email
        sendLoginAlertEmail(email, ipAddress, location);

        return ResponseEntity.ok("OTP sent to email. Please verify to complete login.");
    }

    @PostMapping("/login/verify-otp")
    public ResponseEntity<?> verifyLoginOtp(@RequestParam String email, @RequestParam String otp) {
        // Verify OTP
        if (!otpStorage.containsKey(email) || !otpStorage.get(email).equals(otp)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or email!");
        }

        // Remove OTP after verification
        otpStorage.remove(email);

        return ResponseEntity.ok("Login successful!");
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
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
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your OTP for Admin Verification");
            message.setText("Your OTP is: " + otp + "\n\nThis OTP is valid for 10 minutes.");

            // Send email
            Transport.send(message);
            System.out.println("OTP email sent to " + toEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send OTP email");
        }
    }

    private void sendLoginAlertEmail(String toEmail, String ipAddress, String location) {
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
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Admin Login Alert");
            message.setText("A login attempt was made to your admin account.\n\n"
                    + "IP Address: " + ipAddress + "\n"
                    + "Location: " + location + "\n\n"
                    + "If this wasn't you, please secure your account immediately.");

            // Send email
            Transport.send(message);
            System.out.println("Login alert email sent to " + toEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send login alert email");
        }
    }

    private String getLocationFromIP(String ipAddress) {
        // Simulate location retrieval from IP address
        // In production, use a third-party service like IPStack, MaxMind, etc.
        return "Mumbai, India"; // Example location
    }
}