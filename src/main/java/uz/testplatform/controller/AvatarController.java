package uz.testplatform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.testplatform.repository.AvatarRepository;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/avatars")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarRepository avatarRepository;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> get(@PathVariable String id) {
        return avatarRepository.findById(id)
                .map(a -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(a.getContentType()))
                        .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                        .body(a.getData()))
                .orElse(ResponseEntity.notFound().build());
    }
}