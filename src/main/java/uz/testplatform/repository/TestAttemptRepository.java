package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.testplatform.entity.AttemptStatus;
import uz.testplatform.entity.TestAttempt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {

    List<TestAttempt> findByUserIdOrderByStartedAtDesc(Long userId);

    Optional<TestAttempt> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndSubjectId(Long userId, Long subjectId);

    Optional<TestAttempt> findFirstByUserIdAndStatus(Long userId, AttemptStatus status);

    List<TestAttempt> findByStatusOrderByStartedAtDesc(AttemptStatus status);

    boolean existsBySubjectId(Long subjectId);

    Optional<TestAttempt> findFirstByUserIdAndStatusAndDeadlineAfter(
            Long userId, AttemptStatus status, LocalDateTime now);

    List<TestAttempt> findByStatusAndDeadlineBefore(AttemptStatus status, LocalDateTime now);

    List<TestAttempt> findByUserIdAndStatusOrderByStartedAtDesc(Long userId, AttemptStatus status);

    List<TestAttempt> findByStatusOrderByFinishedAtDesc(AttemptStatus status);
}