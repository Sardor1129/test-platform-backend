package uz.testplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import uz.testplatform.exception.ApiException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MailService {

    @Value("${app.mail.enabled}")
    private boolean enabled;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.from-name:Test platforma}")
    private String fromName;

    @Value("${app.mail.brevo-api-key:}")
    private String apiKey;

    private final RestClient http;

    public MailService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(10_000);
        this.http = RestClient.builder()
                .baseUrl("https://api.brevo.com")
                .requestFactory(factory)
                .build();
    }

    public void sendCode(String to, String code) {
        if (!enabled) {
            // Ishlab chiqish rejimi: kod konsolga chiqadi
            log.info("EMAIL (test) -> {} : kod {}", to, code);
            return;
        }
        Map<String, Object> body = Map.of(
                "sender", Map.of("name", fromName, "email", from),
                "to", List.of(Map.of("email", to)),
                "subject", "Tasdiqlash kodi",
                "textContent", "Sizning tasdiqlash kodingiz: " + code
                        + "\n\nKod 5 daqiqa amal qiladi. Agar bu so'rovni siz yubormagan bo'lsangiz, "
                        + "bu xatni e'tiborsiz qoldiring."
        );
        try {
            http.post()
                    .uri("/v3/smtp/email")
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Xat yuborib bo'lmadi ({})", to, e);
            throw new ApiException(HttpStatus.BAD_GATEWAY,
                    "Xat yuborib bo'lmadi, keyinroq urinib ko'ring");
        }
    }
}