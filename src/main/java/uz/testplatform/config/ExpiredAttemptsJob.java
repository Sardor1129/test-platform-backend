package uz.testplatform.config;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.testplatform.service.AttemptService;

@Component
@RequiredArgsConstructor
public class ExpiredAttemptsJob {

    private final AttemptService attemptService;

    @Scheduled(fixedDelay = 60_000)     // har 60 soniyada
    public void run() {
        attemptService.finishExpired();
    }
}