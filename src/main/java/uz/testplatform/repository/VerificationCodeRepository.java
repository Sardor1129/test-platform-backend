package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.testplatform.entity.CodePurpose;
import uz.testplatform.entity.VerificationCode;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findFirstByEmailAndPurposeAndUsedFalseOrderByIdDesc(
            String email, CodePurpose purpose);
}