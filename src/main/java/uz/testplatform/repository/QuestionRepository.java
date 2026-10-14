package uz.testplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.testplatform.entity.Question;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findBySubjectIdAndLanguage(Long subjectId, String language);

    long countBySubjectIdAndLanguage(Long subjectId, String language);

    @Query(value = """
            SELECT * FROM questions
            WHERE subject_id = :subjectId AND language = :language
            ORDER BY RANDOM()
            LIMIT :limit
            """, nativeQuery = true)
    List<Question> findRandom(@Param("subjectId") Long subjectId,
                              @Param("language") String language,
                              @Param("limit") int limit);

    boolean existsBySubjectId(Long subjectId);
}