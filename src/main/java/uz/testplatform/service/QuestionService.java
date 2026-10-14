package uz.testplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.dto.AdminOptionResponse;
import uz.testplatform.dto.AdminQuestionResponse;
import uz.testplatform.dto.OptionRequest;
import uz.testplatform.dto.QuestionRequest;
import uz.testplatform.entity.AnswerOption;
import uz.testplatform.entity.Question;
import uz.testplatform.entity.Subject;
import uz.testplatform.exception.ApiException;
import uz.testplatform.repository.AttemptAnswerRepository;
import uz.testplatform.repository.QuestionRepository;
import uz.testplatform.repository.SubjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private static final String LABELS = "ABCD";

    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;

    @Transactional(readOnly = true)
    public List<AdminQuestionResponse> getAll(Long subjectId, String language) {
        return questionRepository.findBySubjectIdAndLanguage(subjectId, language).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminQuestionResponse getOne(Long id) {
        return toResponse(findQuestion(id));
    }

    @Transactional
    public AdminQuestionResponse create(QuestionRequest request) {
        validateOptions(request.options());
        Subject subject = findSubject(request.subjectId());

        Question question = Question.builder()
                .subject(subject)
                .language(request.language())
                .text(request.text())
                .imageUrl(request.imageUrl())
                .build();
        fillOptions(question, request.options());

        return toResponse(questionRepository.save(question));
    }

    @Transactional
    public AdminQuestionResponse update(Long id, QuestionRequest request) {
        Question question = findQuestion(id);
        ensureNotUsed(id, "o'zgartirib");
        validateOptions(request.options());

        question.setSubject(findSubject(request.subjectId()));
        question.setLanguage(request.language());
        question.setText(request.text());
        question.setImageUrl(request.imageUrl());

        question.getOptions().clear();      // eski variantlar o'chadi (orphanRemoval)
        fillOptions(question, request.options());

        return toResponse(questionRepository.save(question));
    }

    @Transactional
    public void delete(Long id) {
        Question question = findQuestion(id);
        ensureNotUsed(id, "o'chirib");
        questionRepository.delete(question);
    }

    // ---------- YORDAMCHI METODLAR ----------

    private Question findQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Savol topilmadi"));
    }

    private Subject findSubject(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Fan topilmadi"));
    }

    private void ensureNotUsed(Long questionId, String action) {
        if (attemptAnswerRepository.existsByQuestionId(questionId)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Bu savol testlarda ishlatilgan, " + action + " bo'lmaydi");
        }
    }

    private void validateOptions(List<OptionRequest> options) {
        long correctCount = options.stream().filter(OptionRequest::correct).count();
        if (correctCount != 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Aynan bitta variant to'g'ri bo'lishi kerak");
        }
    }

    private void fillOptions(Question question, List<OptionRequest> options) {
        for (int i = 0; i < options.size(); i++) {
            OptionRequest o = options.get(i);
            AnswerOption option = AnswerOption.builder()
                    .question(question)
                    .label(String.valueOf(LABELS.charAt(i)))
                    .text(o.text())
                    .correct(o.correct())
                    .build();
            question.getOptions().add(option);
        }
    }

    private AdminQuestionResponse toResponse(Question q) {
        List<AdminOptionResponse> options = q.getOptions().stream()
                .map(o -> new AdminOptionResponse(o.getId(), o.getLabel(), o.getText(), o.isCorrect()))
                .toList();
        return new AdminQuestionResponse(
                q.getId(), q.getSubject().getId(), q.getLanguage(),
                q.getText(), q.getImageUrl(), options);
    }
}