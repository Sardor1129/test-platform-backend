package uz.testplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {

    public void send(String phone, String message) {
        log.info("SMS -> {} : {}", phone, message);
    }
}