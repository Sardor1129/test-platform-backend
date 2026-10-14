package uz.testplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.testplatform.dto.AdminSubjectResponse;
import uz.testplatform.dto.SubjectRequest;
import uz.testplatform.service.SubjectService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/subjects")
@RequiredArgsConstructor
public class AdminSubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public List<AdminSubjectResponse> getAll() {
        return subjectService.getAllForAdmin();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminSubjectResponse create(@Valid @RequestBody SubjectRequest request) {
        return subjectService.create(request);
    }

    @PutMapping("/{id}")
    public AdminSubjectResponse update(@PathVariable Long id,
                                       @Valid @RequestBody SubjectRequest request) {
        return subjectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Fan o'chirildi"));
    }
}