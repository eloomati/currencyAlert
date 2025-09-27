package io.mhetko.dataprovider.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    void shouldSendNotification() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService service = new EmailService(mailSender);

        service.sendNotification("to@example.com", "Subject", "Body");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getTo()).containsExactly("to@example.com");
        assertThat(sent.getSubject()).isEqualTo("Subject");
        assertThat(sent.getText()).isEqualTo("Body");
    }
}
