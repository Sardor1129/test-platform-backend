package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.testplatform.entity.AnswerOption;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
}