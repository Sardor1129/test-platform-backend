package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.testplatform.entity.Avatar;

public interface AvatarRepository extends JpaRepository<Avatar, String> {

    void deleteByUserId(Long userId);
}