package uz.testplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uz.testplatform.dto.*;
import uz.testplatform.service.AttemptService;
import uz.testplatform.service.CertificateService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attempts")
@RequiredArgsConstructor
public class AttemptController {

    private final AttemptService attemptService;
    private final CertificateService certificateService;

    @PostMapping
    public AttemptResponse start(@Valid @RequestBody StartAttemptRequest request,
                                 Authentication authentication) {
        return attemptService.start(authentication.getName(), request);
    }

    @GetMapping("/{id}")
    public AttemptResponse getOne(@PathVariable Long id, Authentication authentication) {
        return attemptService.getOne(authentication.getName(), id);
    }
    @PutMapping("/{id}/answers")
    public ResponseEntity<Map<String, String>> saveAnswer(@PathVariable Long id,
                                                          @Valid @RequestBody SaveAnswerRequest request,
                                                          Authentication authentication) {
        attemptService.saveAnswer(authentication.getName(), id, request);
        return ResponseEntity.ok(Map.of("message", "Javob saqlandi"));
    }

    @PostMapping("/{id}/finish")
    public AttemptResultResponse finish(@PathVariable Long id, Authentication authentication) {
        return attemptService.finish(authentication.getName(), id);
    }

    @GetMapping("/{id}/result")
    public AttemptResultResponse getResult(@PathVariable Long id, Authentication authentication) {
        return attemptService.getResult(authentication.getName(), id);
    }

    @GetMapping
    public List<AttemptSummaryResponse> myResults(@RequestParam(defaultValue = "uz") String lang,
                                                  Authentication authentication) {
        return attemptService.getMyResults(authentication.getName(), lang);
    }

    @GetMapping("/{id}/review")
    public ReviewResponse review(@PathVariable Long id, Authentication authentication) {
        return attemptService.getReview(authentication.getName(), id);
    }

    @GetMapping("/{id}/certificate")
    public ResponseEntity<byte[]> certificate(@PathVariable Long id, Authentication authentication) {
        CertificateFile file = certificateService.generate(authentication.getName(), id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.filename() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file.content());
    }
}