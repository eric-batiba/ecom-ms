package com.damlotec.ecommerce.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_RELATED;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(String destinationEmail, Map<String, Object> variables, TemplateType templateType) {
        log.info("Sending {} email to {}", templateType.name(), destinationEmail);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, MULTIPART_MODE_RELATED, UTF_8.name());
            helper.setFrom("contact@btb.com");
            helper.setSubject(templateType.getSubject());
            helper.setTo(destinationEmail);

            // Set up the template context and process the template
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateType.getTemplate(), context);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Sent {} email successfully to {}", templateType.name(), destinationEmail);

        } catch (MessagingException e) {
            log.warn("Failed to send {} email to {}", templateType.name(), destinationEmail, e);
        }
    }
}

