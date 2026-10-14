package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.testplatform.entity.AttemptAnswer;

import java.util.List;
import java.util.Optional;

public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Long> {

    List<AttemptAnswer> findByAttemptIdOrderByOrderNumberAsc(Long attemptId);

    Optional<AttemptAnswer> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);

    long countByAttemptIdAndSelectedOptionIsNotNull(Long attemptId);

    boolean existsByQuestionId(Long questionId);
}