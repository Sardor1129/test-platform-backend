package uz.testplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.dto.*;
import uz.testplatform.entity.*;
import uz.testplatform.exception.ApiException;
import uz.testplatform.repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttemptService {

    private final TestAttemptRepository attemptRepository;
    private final SubjectRepository subjectRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerOptionRepository optionRepository;
    private final AttemptAnswerRepository answerRepository;
    private final CertificateRepository certificateRepository;


    // ---------- TESTNI BOSHLASH ----------

    @Transactional
    public AttemptResponse start(String login, StartAttemptRequest request) {
        User user = findUser(login);
        LocalDateTime now = LocalDateTime.now();

        // 1) Davom etayotgan test bo'lsa, yangisini yaratmaymiz
        Optional<TestAttempt> active = attemptRepository
                .findFirstByUserIdAndStatusAndDeadlineAfter(user.getId(), AttemptStatus.IN_PROGRESS, now);
        if (active.isPresent()) {
            return toResponse(active.get());
        }

        // 2) Fan va savollar yetarliligini tekshiramiz
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Fan topilmadi"));

        long available = questionRepository
                .countBySubjectIdAndLanguage(subject.getId(), request.language());
        if (available < subject.getQuestionCount()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Bu fan va tilda savollar yetarli emas (kerak: "
                            + subject.getQuestionCount() + ", mavjud: " + available + ")");
        }

        // 3) Tasodifiy savollarni tanlaymiz
        List<Question> questions = questionRepository
                .findRandom(subject.getId(), request.language(), subject.getQuestionCount());

        // 4) Birinchi urinishmi yoki qayta topshirishmi
        AttemptType type = attemptRepository.existsByUserIdAndSubjectId(user.getId(), subject.getId())
                ? AttemptType.RETAKE
                : AttemptType.FIRST;

        // 5) Urinishni yaratamiz
        TestAttempt attempt = TestAttempt.builder()
                .user(user)
                .subject(subject)
                .language(request.language())
                .startedAt(now)
                .deadline(now.plusMinutes(subject.getDurationMinutes()))
                .attemptType(type)
                .totalQuestions(questions.size())
                .build();

        // 6) Har savolga 1, 2, 3... tartib raqami beramiz
        for (int i = 0; i < questions.size(); i++) {
            attempt.getAnswers().add(AttemptAnswer.builder()
                    .attempt(attempt)
                    .question(questions.get(i))
                    .orderNumber(i + 1)
                    .correct(false)
                    .build());
        }

        attemptRepository.save(attempt);
        return toResponse(attempt);
    }

    // ---------- DAVOM ETAYOTGAN TESTNI OLISH ----------

    @Transactional(readOnly = true)
    public AttemptResponse getOne(String login, Long attemptId) {
        User user = findUser(login);

        TestAttempt attempt = attemptRepository.findByIdAndUserId(attemptId, user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Test topilmadi"));

        if (attempt.getStatus() == AttemptStatus.FINISHED) {
            throw new ApiException(HttpStatus.CONFLICT, "Bu test yakunlangan");
        }
        return toResponse(attempt);
    }

    // ---------- JAVOBNI SAQLASH ----------

    @Transactional
    public AnswerFeedbackResponse saveAnswer(String login, Long attemptId, SaveAnswerRequest request) {
        TestAttempt attempt = findOwnAttempt(login, attemptId);

        if (attempt.getStatus() == AttemptStatus.FINISHED) {
            throw new ApiException(HttpStatus.CONFLICT, "Test yakunlangan");
        }
        if (LocalDateTime.now().isAfter(attempt.getDeadline())) {
            throw new ApiException(HttpStatus.CONFLICT, "Test vaqti tugagan");
        }

        AttemptAnswer answer = answerRepository
                .findByAttemptIdAndQuestionId(attemptId, request.questionId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Bu savol testda yo'q"));

        // Javob qulflanadi: bir marta belgilangach o'zgartirib bo'lmaydi
        if (answer.getSelectedOption() != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Bu savolga javob berilgan, o'zgartirib bo'lmaydi");
        }

        AnswerOption option = optionRepository.findById(request.optionId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Variant topilmadi"));
        if (!option.getQuestion().getId().equals(request.questionId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Variant bu savolga tegishli emas");
        }

        answer.setSelectedOption(option);
        answer.setCorrect(option.isCorrect());
        answerRepository.save(answer);

        return new AnswerFeedbackResponse(option.isCorrect(), correctOptionId(answer.getQuestion()));
    }

    // ---------- YAKUNLASH ----------

    @Transactional
    public AttemptResultResponse finish(String login, Long attemptId) {
        TestAttempt attempt = findOwnAttempt(login, attemptId);
        finalizeAttempt(attempt);
        return toResult(attempt);
    }

    @Transactional(readOnly = true)
    public AttemptResultResponse getResult(String login, Long attemptId) {
        TestAttempt attempt = findOwnAttempt(login, attemptId);
        if (attempt.getStatus() != AttemptStatus.FINISHED) {
            throw new ApiException(HttpStatus.CONFLICT, "Test hali yakunlanmagan");
        }
        return toResult(attempt);
    }

    // Vaqti o'tgan testlarni avtomatik yakunlaydi (scheduler chaqiradi)
    @Transactional
    public void finishExpired() {
        List<TestAttempt> expired = attemptRepository
                .findByStatusAndDeadlineBefore(AttemptStatus.IN_PROGRESS, LocalDateTime.now());
        for (TestAttempt attempt : expired) {
            finalizeAttempt(attempt);
        }
    }

    // ---------- YORDAMCHI METODLAR ----------

    private User findUser(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Foydalanuvchi topilmadi"));
    }

    private AttemptResponse toResponse(TestAttempt a) {
        List<AttemptQuestionResponse> questions = a.getAnswers().stream()
                .map(aa -> {
                    Question q = aa.getQuestion();
                    List<AttemptOptionResponse> options = shuffledOptions(a.getId(), q).stream()
                            .map(o -> new AttemptOptionResponse(o.getId(), o.getLabel(), o.getText()))
                            .toList();
                    Long selectedId = aa.getSelectedOption() == null
                            ? null
                            : aa.getSelectedOption().getId();
                    Long correctId = selectedId == null ? null : correctOptionId(q);
                    return new AttemptQuestionResponse(
                            aa.getOrderNumber(), q.getId(), q.getText(), q.getImageUrl(),
                            options, selectedId, correctId);
                })
                .toList();

        long remaining = Math.max(0,
                Duration.between(LocalDateTime.now(), a.getDeadline()).getSeconds());

        return new AttemptResponse(
                a.getId(), a.getSubject().getId(), subjectName(a.getSubject(), a.getLanguage()),
                a.getLanguage(), a.getStartedAt(), a.getDeadline(), remaining, questions);
    }

    private String subjectName(Subject s, String lang) {
        return switch (lang) {
            case "ru" -> s.getNameRu();
            case "en" -> s.getNameEn();
            default -> s.getNameUz();
        };
    }

    private TestAttempt findOwnAttempt(String login, Long attemptId) {
        User user = findUser(login);
        return attemptRepository.findByIdAndUserId(attemptId, user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Test topilmadi"));
    }

    private void finalizeAttempt(TestAttempt attempt) {
        if (attempt.getStatus() == AttemptStatus.FINISHED) {
            return;                         // ikkinchi marta hisoblamaymiz
        }

        int correct = 0;
        for (AttemptAnswer aa : attempt.getAnswers()) {
            boolean ok = aa.getSelectedOption() != null && aa.getSelectedOption().isCorrect();
            aa.setCorrect(ok);
            if (ok) {
                correct++;
            }
        }

        int total = attempt.getAnswers().size();
        double percent = total == 0 ? 0 : Math.round(correct * 10000.0 / total) / 100.0;
        boolean passed = percent >= attempt.getSubject().getPassPercent();

        attempt.setTotalQuestions(total);
        attempt.setCorrectCount(correct);
        attempt.setPercent(percent);
        attempt.setPassed(passed);
        attempt.setStatus(AttemptStatus.FINISHED);
        attempt.setFinishedAt(LocalDateTime.now());
        attemptRepository.save(attempt);

        // Sertifikat FAQAT o'tganda yaratiladi
        if (passed && !certificateRepository.existsByAttemptId(attempt.getId())) {
            certificateRepository.save(Certificate.builder()
                    .attempt(attempt)
                    .number("CERT-" + LocalDateTime.now().getYear()
                            + "-" + String.format("%06d", attempt.getId()))
                    .build());
        }
    }

    // Variantlar tartibi: bir urinish ichida doim bir xil, urinishlar orasida har xil
    private List<AnswerOption> shuffledOptions(Long attemptId, Question q) {
        List<AnswerOption> list = new ArrayList<>(q.getOptions());
        list.sort(Comparator.comparing(AnswerOption::getId));
        Collections.shuffle(list, new Random(attemptId * 1_000_003L + q.getId()));
        return list;
    }

    private AttemptResultResponse toResult(TestAttempt a) {
        int total = a.getTotalQuestions();
        int correct = a.getCorrectCount();
        return new AttemptResultResponse(
                a.getId(), total, correct, total - correct, a.getPercent(),
                Boolean.TRUE.equals(a.getPassed()),
                certificateRepository.existsByAttemptId(a.getId()));
    }

    private Long correctOptionId(Question q) {
        return q.getOptions().stream()
                .filter(AnswerOption::isCorrect)
                .map(AnswerOption::getId)
                .findFirst()
                .orElse(null);
    }

    // ---------- MENING NATIJALARIM ----------

    @Transactional(readOnly = true)
    public List<AttemptSummaryResponse> getMyResults(String login, String lang) {
        User user = findUser(login);
        return attemptRepository
                .findByUserIdAndStatusOrderByStartedAtDesc(user.getId(), AttemptStatus.FINISHED)
                .stream()
                .map(a -> new AttemptSummaryResponse(
                        a.getId(),
                        a.getFinishedAt(),
                        subjectName(a.getSubject(), lang),
                        a.getAttemptType().name(),
                        a.getPercent(),
                        Boolean.TRUE.equals(a.getPassed()),
                        certificateRepository.existsByAttemptId(a.getId())))
                .toList();
    }

    // ---------- JAVOBLARNI KO'RISH ----------

    @Transactional(readOnly = true)
    public ReviewResponse getReview(String login, Long attemptId) {
        TestAttempt attempt = findOwnAttempt(login, attemptId);

        if (attempt.getStatus() != AttemptStatus.FINISHED) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Javoblarni test yakunlangandan keyin ko'rish mumkin");
        }

        List<ReviewQuestionResponse> questions = attempt.getAnswers().stream()
                .map(aa -> {
                    Long selectedId = aa.getSelectedOption() == null
                            ? null : aa.getSelectedOption().getId();

                    List<AnswerOption> ordered = reviewOptions(aa);
                    String letters = "ABCD";
                    List<ReviewOptionResponse> options = new ArrayList<>();
                    for (int i = 0; i < ordered.size(); i++) {
                        AnswerOption o = ordered.get(i);
                        options.add(new ReviewOptionResponse(
                                o.getId(),
                                String.valueOf(letters.charAt(i)),
                                o.getText(),
                                o.isCorrect(),
                                o.getId().equals(selectedId)));
                    }

                    Question q = aa.getQuestion();
                    return new ReviewQuestionResponse(
                            aa.getOrderNumber(), q.getId(), q.getText(), q.getImageUrl(),
                            selectedId != null, aa.isCorrect(), options);
                })
                .toList();

        return new ReviewResponse(
                attempt.getId(),
                subjectName(attempt.getSubject(), attempt.getLanguage()),
                attempt.getPercent(),
                Boolean.TRUE.equals(attempt.getPassed()),
                questions);
    }

    // Variantlarni ko'rsatish tartibi
    private List<AnswerOption> reviewOptions(AttemptAnswer aa) {
        return shuffledOptions(aa.getAttempt().getId(), aa.getQuestion());
    }


}