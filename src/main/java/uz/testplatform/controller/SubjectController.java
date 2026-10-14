package uz.testplatform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.testplatform.dto.SubjectResponse;
import uz.testplatform.service.SubjectService;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public List<SubjectResponse> getAll(@RequestParam(defaultValue = "uz") String lang) {
        return subjectService.getAll(lang);
    }
}