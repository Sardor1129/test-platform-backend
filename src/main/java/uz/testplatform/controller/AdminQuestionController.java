package uz.testplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.testplatform.dto.AdminQuestionResponse;
import uz.testplatform.dto.QuestionRequest;
import uz.testplatform.service.QuestionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final QuestionService questionService;

    @GetMapping
    public List<AdminQuestionResponse> getAll(@RequestParam Long subjectId,
                                              @RequestParam String language) {
        return questionService.getAll(subjectId, language);
    }

    @GetMapping("/{id}")
    public AdminQuestionResponse getOne(@PathVariable Long id) {
        return questionService.getOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminQuestionResponse create(@Valid @RequestBody QuestionRequest request) {
        return questionService.create(request);
    }

    @PutMapping("/{id}")
    public AdminQuestionResponse update(@PathVariable Long id,
                                        @Valid @RequestBody QuestionRequest request) {
        return questionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        questionService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Savol o'chirildi"));
    }
}