package uz.testplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.testplatform.dto.AuthResponse;
import uz.testplatform.dto.ProfileResponse;
import uz.testplatform.dto.ProfileUpdateRequest;
import uz.testplatform.service.AvatarService;
import uz.testplatform.service.ProfileService;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final AvatarService avatarService;

    @GetMapping
    public ProfileResponse get(Authentication authentication) {
        return profileService.get(authentication.getName());
    }

    @PutMapping
    public AuthResponse update(@Valid @RequestBody ProfileUpdateRequest request,
                               Authentication authentication) {
        return profileService.update(authentication.getName(), request);
    }

    @PostMapping("/avatar")
    public Map<String, String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                            Authentication authentication) {
        String url = avatarService.upload(authentication.getName(), file);
        return Map.of("avatarUrl", url);
    }
}