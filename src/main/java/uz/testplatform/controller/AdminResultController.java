package uz.testplatform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.testplatform.dto.AdminResultResponse;
import uz.testplatform.service.AdminResultService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/results")
@RequiredArgsConstructor
public class AdminResultController {

    private final AdminResultService adminResultService;

    @GetMapping
    public List<AdminResultResponse> getResults(@RequestParam(required = false) Long subjectId,
                                                @RequestParam(required = false) Boolean passed) {
        return adminResultService.getResults(subjectId, passed);
    }
}