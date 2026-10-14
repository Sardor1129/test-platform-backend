package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.testplatform.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}