package uz.testplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.dto.AdminCategoryResponse;
import uz.testplatform.dto.CategoryRequest;
import uz.testplatform.dto.CategoryResponse;
import uz.testplatform.entity.Category;
import uz.testplatform.exception.ApiException;
import uz.testplatform.repository.CategoryRepository;
import uz.testplatform.repository.SubjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubjectRepository subjectRepository;

    // ---------- FOYDALANUVCHI ----------

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll(String lang) {
        return categoryRepository.findAll(Sort.by("id")).stream()
                .map(c -> new CategoryResponse(c.getId(), nameByLang(c, lang)))
                .toList();
    }

    // ---------- ADMIN ----------

    @Transactional(readOnly = true)
    public List<AdminCategoryResponse> getAllForAdmin() {
        return categoryRepository.findAll(Sort.by("id")).stream()
                .map(this::toAdminResponse)
                .toList();
    }

    @Transactional
    public AdminCategoryResponse create(CategoryRequest request) {
        Category category = Category.builder()
                .nameUz(request.nameUz().trim())
                .nameRu(request.nameRu().trim())
                .nameEn(request.nameEn().trim())
                .build();
        return toAdminResponse(categoryRepository.save(category));
    }

    @Transactional
    public AdminCategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Kategoriya topilmadi"));
        category.setNameUz(request.nameUz().trim());
        category.setNameRu(request.nameRu().trim());
        category.setNameEn(request.nameEn().trim());
        return toAdminResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Kategoriya topilmadi");
        }
        if (subjectRepository.existsByCategoryId(id)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Bu kategoriyada testlar bor, avval testlarni boshqa kategoriyaga o'tkazing");
        }
        categoryRepository.deleteById(id);
    }

    // ---------- YORDAMCHI ----------

    private String nameByLang(Category c, String lang) {
        return switch (lang) {
            case "ru" -> c.getNameRu();
            case "en" -> c.getNameEn();
            default -> c.getNameUz();
        };
    }

    private AdminCategoryResponse toAdminResponse(Category c) {
        return new AdminCategoryResponse(
                c.getId(), c.getNameUz(), c.getNameRu(), c.getNameEn(),
                subjectRepository.countByCategoryId(c.getId()));
    }
}