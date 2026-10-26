package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.testplatform.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByLogin(String login);

    boolean existsByEmailIgnoreCase(String email);
}