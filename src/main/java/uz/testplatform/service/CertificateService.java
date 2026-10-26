package uz.testplatform.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.testplatform.dto.CertificateFile;
import uz.testplatform.entity.Certificate;
import uz.testplatform.entity.TestAttempt;
import uz.testplatform.entity.User;
import uz.testplatform.exception.ApiException;
import uz.testplatform.repository.CertificateRepository;
import uz.testplatform.repository.TestAttemptRepository;
import uz.testplatform.repository.UserRepository;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final UserRepository userRepository;
    private final TestAttemptRepository attemptRepository;
    private final CertificateRepository certificateRepository;

    @Transactional(readOnly = true)
    public CertificateFile generate(String login, Long attemptId) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Foydalanuvchi topilmadi"));

        // Faqat o'z urinishi
        TestAttempt attempt = attemptRepository.findByIdAndUserId(attemptId, user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Test topilmadi"));

        // Sertifikat faqat o'tgan urinishda mavjud
        Certificate certificate = certificateRepository.findByAttemptId(attemptId)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN,
                        "Bu test uchun sertifikat berilmagan"));

        byte[] pdf = buildPdf(user, attempt, certificate);
        return new CertificateFile(certificate.getNumber() + ".pdf", pdf);
    }

    private byte[] buildPdf(User user, TestAttempt attempt, Certificate certificate) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BaseFont base = loadFont();
            Font title = new Font(base, 32, Font.BOLD, new Color(0, 90, 200));
            Font normal = new Font(base, 16, Font.NORMAL, Color.DARK_GRAY);
            Font name = new Font(base, 26, Font.BOLD, Color.BLACK);
            Font small = new Font(base, 12, Font.NORMAL, Color.GRAY);

            Document doc = new Document(PageSize.A4.rotate(), 60, 60, 60, 60);
            PdfWriter.getInstance(doc, out);
            doc.open();

            doc.add(centered("SERTIFIKAT", title, 40));
            doc.add(centered("Ushbu sertifikat quyidagi shaxsga beriladi", normal, 20));

            String fullName = user.getFirstName();
            doc.add(centered(fullName, name, 20));

            String subject = switch (attempt.getLanguage()) {
                case "ru" -> attempt.getSubject().getNameRu();
                case "en" -> attempt.getSubject().getNameEn();
                default -> attempt.getSubject().getNameUz();
            };
            doc.add(centered("\"" + subject + "\" fanidan test sinovini muvaffaqiyatli topshirgani uchun",
                    normal, 10));
            doc.add(centered("Natija: " + attempt.getPercent() + "% ("
                    + attempt.getCorrectCount() + " / " + attempt.getTotalQuestions() + ")", normal, 40));

            doc.add(centered("Sertifikat raqami: " + certificate.getNumber(), small, 5));
            doc.add(centered("Berilgan sana: " + certificate.getIssuedAt().format(DATE), small, 0));

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Sertifikat yaratib bo'lmadi");
        }
    }

    private Paragraph centered(String text, Font font, float spacingAfter) {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(spacingAfter);
        return p;
    }

    private BaseFont loadFont() throws Exception {
        try (InputStream in = new ClassPathResource("fonts/DejaVuSans.ttf").getInputStream()) {
            byte[] bytes = in.readAllBytes();
            return BaseFont.createFont("DejaVuSans.ttf", BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED, true, bytes, null);
        }
    }
}