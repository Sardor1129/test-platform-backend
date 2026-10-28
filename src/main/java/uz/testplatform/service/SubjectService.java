package uz.testplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.dto.AdminSubjectResponse;
import uz.testplatform.dto.SubjectRequest;
import uz.testplatform.dto.SubjectResponse;
import uz.testplatform.entity.Category;
import uz.testplatform.entity.Subject;
import uz.testplatform.exception.ApiException;
import uz.testplatform.repository.CategoryRepository;
import uz.testplatform.repository.QuestionRepository;
import uz.testplatform.repository.SubjectRepository;
import uz.testplatform.repository.TestAttemptRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final TestAttemptRepository attemptRepository;

    // ---------- FOYDALANUVCHI ----------

    @Transactional(readOnly = true)
    public List<SubjectResponse> getAll(String lang) {
        return subjectRepository.findAll().stream()
                .map(s -> new SubjectResponse(
                        s.getId(),
                        nameByLang(s, lang),
                        s.getDurationMinutes(),
                        s.getQuestionCount(),
                        s.getCategory() == null ? null : s.getCategory().getId()))
                .toList();
    }

    // ---------- ADMIN ----------

    @Transactional(readOnly = true)
    public List<AdminSubjectResponse> getAllForAdmin() {
        return subjectRepository.findAll().stream()
                .map(this::toAdminResponse)
                .toList();
    }

    @Transactional
    public AdminSubjectResponse create(SubjectRequest request) {
        Subject subject = Subject.builder()
                .nameUz(request.nameUz())
                .nameRu(request.nameRu())
                .nameEn(request.nameEn())
                .durationMinutes(request.durationMinutes())
                .questionCount(request.questionCount())
                .passPercent(request.passPercent())
                .category(findCategory(request.categoryId()))
                .build();
        return toAdminResponse(subjectRepository.save(subject));
    }

    @Transactional
    public AdminSubjectResponse update(Long id, SubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Fan topilmadi"));

        subject.setNameUz(request.nameUz());
        subject.setNameRu(request.nameRu());
        subject.setNameEn(request.nameEn());
        subject.setDurationMinutes(request.durationMinutes());
        subject.setQuestionCount(request.questionCount());
        subject.setPassPercent(request.passPercent());
        subject.setCategory(findCategory(request.categoryId()));

        return toAdminResponse(subjectRepository.save(subject));
    }

    @Transactional
    public void delete(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Fan topilmadi");
        }
        if (questionRepository.existsBySubjectId(id)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Bu fanda savollar bor, avval savollarni o'chiring");
        }
        if (attemptRepository.existsBySubjectId(id)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Bu fandan test topshirilgan, o'chirib bo'lmaydi");
        }
        subjectRepository.deleteById(id);
    }

    // ---------- YORDAMCHI METODLAR ----------

    private Category findCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Kategoriya topilmadi"));
    }

    private String nameByLang(Subject s, String lang) {
        return switch (lang) {
            case "ru" -> s.getNameRu();
            case "en" -> s.getNameEn();
            default -> s.getNameUz();
        };
    }

    private AdminSubjectResponse toAdminResponse(Subject s) {
        Category c = s.getCategory();
        return new AdminSubjectResponse(
                s.getId(), s.getNameUz(), s.getNameRu(), s.getNameEn(),
                s.getDurationMinutes(), s.getQuestionCount(), s.getPassPercent(),
                c == null ? null : c.getId(),
                c == null ? null : c.getNameUz());
    }
}