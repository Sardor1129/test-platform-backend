package uz.testplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uz.testplatform.exception.ApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled}")
    private boolean enabled;

    @Value("${app.mail.from}")
    private String from;

    public void sendCode(String to, String code) {
        if (!enabled) {
            log.info("EMAIL (test) -> {} : kod {}", to, code);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("Tasdiqlash kodi");
            message.setText("Sizning tasdiqlash kodingiz: " + code
                    + "\n\nKod 5 daqiqa amal qiladi. Agar bu so'rovni siz yubormagan bo'lsangiz, "
                    + "bu xatni e'tiborsiz qoldiring.");
            mailSender.send(message);
        } catch (MailException e) {
            log.error("Xat yuborib bo'lmadi ({})", to, e);
            throw new ApiException(HttpStatus.BAD_GATEWAY,
                    "Xat yuborib bo'lmadi, keyinroq urinib ko'ring");
        }
    }
}