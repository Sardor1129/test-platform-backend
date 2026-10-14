package uz.testplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TestplatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestplatformApplication.class, args);
    }

}
