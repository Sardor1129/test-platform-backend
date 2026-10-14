package uz.testplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.testplatform.dto.*;
import uz.testplatform.entity.CodePurpose;
import uz.testplatform.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }

    @PostMapping("/register/send-code")
    public ResponseEntity<Map<String, String>> sendRegisterCode(@Valid @RequestBody RegisterRequest  request){
        authService.sendRegisterCode(request);
        return ResponseEntity.ok(Map.of("message","Kod yuborildi"));
    }

    @PostMapping("/register/resend-code")
    public ResponseEntity<Map<String, String>> resendRegisterCode(@Valid @RequestBody PhoneRequest request) {
        authService.resendRegisterCode(request.phone());
        return ResponseEntity.ok(Map.of("message", "Kod qayta yuborildi"));
    }

    @PostMapping("/register/confirm")
    public AuthResponse confirmRegister(@Valid @RequestBody VerifyCodeRequest request) {
        return authService.confirmRegister(request);
    }

    @PostMapping("/password/send-code")
    public ResponseEntity<Map<String, String>> sendResetCode(@Valid @RequestBody PhoneRequest request) {
        authService.sendResetCode(request.phone());
        return ResponseEntity.ok(Map.of("message", "Kod yuborildi"));
    }

    @PostMapping("/password/verify-code")
    public ResponseEntity<Map<String, String>> verifyResetCode(@Valid @RequestBody VerifyCodeRequest request) {
        authService.checkResetCode(request);
        return ResponseEntity.ok(Map.of("message", "Kod to'g'ri"));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Parol muvaffaqiyatli o'zgartirildi"));
    }
}
