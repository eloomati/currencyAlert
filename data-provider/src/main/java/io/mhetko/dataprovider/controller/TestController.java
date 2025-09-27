package io.mhetko.dataprovider.controller;

import io.mhetko.dataprovider.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final EmailService emailService;

    @GetMapping("/api/test/email")
    public String sendTestEmail() {
        emailService.sendNotification(
                "test@example.com",
                "Test subject",
                "Test body"
        );
        return "Test email sent";
    }
}