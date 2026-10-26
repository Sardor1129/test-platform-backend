package uz.testplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.dto.AdminResultResponse;
import uz.testplatform.entity.AttemptStatus;
import uz.testplatform.entity.User;
import uz.testplatform.repository.TestAttemptRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminResultService {

    private final TestAttemptRepository attemptRepository;

    @Transactional(readOnly = true)
    public List<AdminResultResponse> getResults(Long subjectId, Boolean passed) {
        return attemptRepository.findByStatusOrderByFinishedAtDesc(AttemptStatus.FINISHED)
                .stream()
                .filter(a -> subjectId == null || a.getSubject().getId().equals(subjectId))
                .filter(a -> passed == null || passed.equals(a.getPassed()))
                .map(a -> {
                    User u = a.getUser();
                    String fullName = u.getFirstName();
                    return new AdminResultResponse(
                            a.getId(), fullName, u.getEmail(),
                            a.getSubject().getNameUz(), a.getLanguage(),
                            a.getAttemptType().name(),
                            a.getCorrectCount(), a.getTotalQuestions(),
                            a.getPercent(), Boolean.TRUE.equals(a.getPassed()),
                            a.getFinishedAt());
                })
                .toList();
    }
}