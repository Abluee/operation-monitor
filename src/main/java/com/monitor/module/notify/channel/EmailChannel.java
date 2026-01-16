package com.monitor.module.notify.channel;

import com.monitor.module.notify.dto.EmailRequest;
import com.monitor.module.notify.dto.NotifyRequest;
import com.monitor.module.notify.dto.NotifyResult;
import com.monitor.module.notify.util.ContentBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Email notification channel
 *
 * @author monitor
 */
@Slf4j
@Component
public class EmailChannel implements NotifyChannel {

    private final JavaMailSender mailSender;
    private final ContentBuilder contentBuilder;

    private static final String CHANNEL_NAME = "email";
    private static final String DEFAULT_FROM = "monitor@company.com";

    public EmailChannel(JavaMailSender mailSender, ContentBuilder contentBuilder) {
        this.mailSender = mailSender;
        this.contentBuilder = contentBuilder;
    }

    @Override
    public String getChannelName() {
        return CHANNEL_NAME;
    }

    @Override
    public NotifyResult send(NotifyRequest request) {
        log.info("Sending email notification for task: {}", request.getTaskId());

        try {
            EmailRequest emailRequest = buildEmailRequest(request);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, emailRequest.getAttachmentPath() != null);

            helper.setFrom(emailRequest.getFrom() != null ? emailRequest.getFrom() : DEFAULT_FROM);
            helper.setTo(emailRequest.getTo().toArray(new String[0]));

            if (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty()) {
                helper.setCc(emailRequest.getCc().toArray(new String[0]));
            }

            helper.setSubject(emailRequest.getSubject());
            helper.setSentDate(new Date());

            if (Boolean.TRUE.equals(emailRequest.getIsHtml())) {
                helper.setText(emailRequest.getHtmlContent(), true);
            } else {
                helper.setText(emailRequest.getContent(), false);
            }

            mailSender.send(mimeMessage);
            log.info("Email notification sent successfully for task: {}", request.getTaskId());
            return NotifyResult.success(CHANNEL_NAME, "sent successfully");

        } catch (MessagingException e) {
            log.error("Email notification failed for task: {}", request.getTaskId(), e);
            return NotifyResult.fail(CHANNEL_NAME, e.getMessage());
        } catch (Exception e) {
            log.error("Email notification exception for task: {}", request.getTaskId(), e);
            return NotifyResult.fail(CHANNEL_NAME, e.getMessage());
        }
    }

    @Override
    public boolean isEnabled() {
        // Check if email is configured
        return mailSender != null;
    }

    /**
     * Build email request from notification request
     */
    private EmailRequest buildEmailRequest(NotifyRequest request) {
        EmailRequest emailRequest = new EmailRequest();

        // Default recipients (should be configured)
        List<String> defaultRecipients = new ArrayList<>();
        defaultRecipients.add("admin@company.com");
        emailRequest.setTo(defaultRecipients);

        // Subject
        String subject = String.format("【监控告警】%s - %s",
                request.getTaskName(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        emailRequest.setSubject(subject);

        // Build content
        StringBuilder plainContent = new StringBuilder();
        plainContent.append("任务名称: ").append(request.getTaskName()).append("\n");
        plainContent.append("通知时间: ").append(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ).append("\n\n");

        if (request.getThresholdViolations() != null && !request.getThresholdViolations().isEmpty()) {
            plainContent.append("阈值违规:\n");
            for (Map<String, Object> violation : request.getThresholdViolations()) {
                plainContent.append("  - ").append(violation.get("field"))
                        .append(": ").append(violation.get("value"))
                        .append(" (阈值: ").append(violation.get("threshold")).append(")\n");
            }
        }

        if (request.getCompleteReason() != null && !request.getCompleteReason().isEmpty()) {
            plainContent.append("\n完成原因: ").append(request.getCompleteReason());
        }

        emailRequest.setContent(plainContent.toString());

        // Build HTML content
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html><html><head><style>");
        htmlContent.append("body { font-family: Arial, sans-serif; }");
        htmlContent.append(".container { padding: 20px; }");
        htmlContent.append(".header { background-color: #f44336; color: white; padding: 10px; }");
        htmlContent.append(".content { margin-top: 20px; }");
        htmlContent.append(".violation { background-color: #ffebee; padding: 10px; margin: 5px 0; }");
        htmlContent.append("</style></head><body>");
        htmlContent.append("<div class='container'>");
        htmlContent.append("<div class='header'><h2>【监控告警】</h2></div>");
        htmlContent.append("<div class='content'>");
        htmlContent.append("<p><strong>任务名称:</strong> ").append(request.getTaskName()).append("</p>");
        htmlContent.append("<p><strong>通知时间:</strong> ").append(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ).append("</p>");

        if (request.getThresholdViolations() != null && !request.getThresholdViolations().isEmpty()) {
            htmlContent.append("<h3>阈值违规:</h3>");
            for (Map<String, Object> violation : request.getThresholdViolations()) {
                htmlContent.append("<div class='violation'>");
                htmlContent.append("<strong>").append(violation.get("field")).append("</strong>: ");
                htmlContent.append(violation.get("value")).append(" (阈值: ").append(violation.get("threshold")).append(")");
                htmlContent.append("</div>");
            }
        }

        if (request.getCompleteReason() != null && !request.getCompleteReason().isEmpty()) {
            htmlContent.append("<p><strong>完成原因:</strong> ").append(request.getCompleteReason()).append("</p>");
        }

        htmlContent.append("</div></div></body></html>");

        emailRequest.setHtmlContent(htmlContent.toString());
        emailRequest.setIsHtml(true);

        return emailRequest;
    }
}
