package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.testplatform.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}