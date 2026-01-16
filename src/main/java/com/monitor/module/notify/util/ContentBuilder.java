package com.monitor.module.notify.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Content builder utility for notification messages
 *
 * @author monitor
 */
@Slf4j
@Component
public class ContentBuilder {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Build threshold violation content
     *
     * @param violations List of threshold violations
     * @return Formatted content
     */
    public String buildThresholdContent(List<Map<String, Object>> violations) {
        if (CollectionUtils.isEmpty(violations)) {
            return "No threshold violations";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("【阈值告警】\n");
        sb.append("检测到以下阈值违规:\n\n");

        int index = 1;
        for (Map<String, Object> violation : violations) {
            sb.append(index).append(". ");
            sb.append(formatViolation(violation));
            sb.append("\n");
            index++;
        }

        return sb.toString();
    }

    /**
     * Format single violation
     *
     * @param violation Violation map
     * @return Formatted string
     */
    private String formatViolation(Map<String, Object> violation) {
        StringBuilder sb = new StringBuilder();

        // Field name
        Object fieldObj = violation.get("field");
        if (fieldObj != null) {
            sb.append("字段: ").append(fieldObj);
        }

        // Current value
        Object valueObj = violation.get("value");
        if (valueObj != null) {
            sb.append(", 当前值: ").append(valueObj);
        }

        // Threshold
        Object thresholdObj = violation.get("threshold");
        if (thresholdObj != null) {
            sb.append(", 阈值: ").append(thresholdObj);
        }

        // Comparison operator
        Object operatorObj = violation.get("operator");
        if (operatorObj != null) {
            sb.append(" (").append(operatorObj).append(")");
        }

        // Additional info
        Object msgObj = violation.get("message");
        if (msgObj != null) {
            sb.append("\n   说明: ").append(msgObj);
        }

        return sb.toString();
    }

    /**
     * Build task completion content
     *
     * @param reason Complete reason
     * @return Formatted content
     */
    public String buildCompleteContent(String reason) {
        StringBuilder sb = new StringBuilder();
        sb.append("【任务完成】\n");

        if (StringUtils.hasText(reason)) {
            sb.append("完成原因: ").append(reason);
        } else {
            sb.append("任务已完成");
        }

        return sb.toString();
    }

    /**
     * Format message with template and parameters
     *
     * @param template Message template with placeholders like ${name}
     * @param params   Parameters to replace placeholders
     * @return Formatted message
     */
    public String formatMessage(String template, Map<String, Object> params) {
        if (!StringUtils.hasText(template)) {
            return "";
        }

        if (CollectionUtils.isEmpty(params)) {
            return template;
        }

        String result = template;
        Pattern pattern = Pattern.compile("\\$\\{(\\w+)\\}");
        Matcher matcher = pattern.matcher(result);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = params.get(key);
            String replacement = value != null ? Matcher.quoteReplacement(value.toString()) : "";
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * Build data summary content
     *
     * @param dataSummary Data summary map
     * @return Formatted content
     */
    public String buildDataSummary(Map<String, Object> dataSummary) {
        if (CollectionUtils.isEmpty(dataSummary)) {
            return "No data summary available";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("【数据摘要】\n");

        for (Map.Entry<String, Object> entry : dataSummary.entrySet()) {
            sb.append(formatEntry(entry));
        }

        return sb.toString();
    }

    /**
     * Format single entry
     *
     * @param entry Map entry
     * @return Formatted string
     */
    private String formatEntry(Map.Entry<String, Object> entry) {
        String key = entry.getKey();
        Object value = entry.getValue();

        if (value instanceof Map) {
            StringBuilder sb = new StringBuilder();
            sb.append(key).append(":\n");
            for (Object subKey : ((Map<?, ?>) value).keySet()) {
                sb.append("  - ").append(subKey)
                        .append(": ").append(((Map<?, ?>) value).get(subKey)).append("\n");
            }
            return sb.toString();
        } else {
            return key + ": " + value + "\n";
        }
    }

    /**
     * Build complete notification message
     *
     * @param taskName          Task name
     * @param violations        Threshold violations
     * @param completeReason    Complete reason
     * @param dataSummary       Data summary
     * @param notifyTime        Notification time
     * @return Complete message
     */
    public String buildCompleteMessage(
            String taskName,
            List<Map<String, Object>> violations,
            String completeReason,
            Map<String, Object> dataSummary,
            String notifyTime) {

        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("【监控告警通知】\n");
        sb.append("━━━━━━━━━━━━━━\n\n");

        // Task info
        sb.append("任务名称: ").append(taskName).append("\n");
        sb.append("通知时间: ").append(notifyTime != null ? notifyTime : "即时").append("\n\n");

        // Threshold violations
        if (!CollectionUtils.isEmpty(violations)) {
            sb.append(buildThresholdContent(violations));
            sb.append("\n");
        }

        // Complete reason
        if (StringUtils.hasText(completeReason)) {
            sb.append(buildCompleteContent(completeReason));
            sb.append("\n\n");
        }

        // Data summary
        if (!CollectionUtils.isEmpty(dataSummary)) {
            sb.append(buildDataSummary(dataSummary));
        }

        // Footer
        sb.append("\n━━━━━━━━━━━━━━\n");
        sb.append("此消息由监控自动生成");

        return sb.toString();
    }

    /**
     * Build HTML formatted message
     *
     * @param taskName       Task name
     * @param violations     Threshold violations
     * @param completeReason Complete reason
     * @param notifyTime     Notification time
     * @return HTML formatted message
     */
    public String buildHtmlMessage(
            String taskName,
            List<Map<String, Object>> violations,
            String completeReason,
            String notifyTime) {

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html><head>");
        sb.append("<style>");
        sb.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        sb.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        sb.append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }");
        sb.append(".header h1 { margin: 0; font-size: 24px; }");
        sb.append(".content { background: #f9f9f9; padding: 20px; border: 1px solid #ddd; }");
        sb.append(".task-info { background: #fff; padding: 15px; border-radius: 8px; margin-bottom: 15px; }");
        sb.append(".task-info p { margin: 5px 0; }");
        sb.append(".label { font-weight: bold; color: #555; }");
        sb.append(".violation { background: #ffebee; border-left: 4px solid #f44336; padding: 10px 15px; margin: 10px 0; border-radius: 0 4px 4px 0; }");
        sb.append(".violation-title { color: #c62828; font-weight: bold; margin-bottom: 8px; }");
        sb.append(".violation-item { padding: 5px 0; border-bottom: 1px solid #ffcdd2; }");
        sb.append(".violation-item:last-child { border-bottom: none; }");
        sb.append(".complete { background: #e8f5e9; border-left: 4px solid #4caf50; padding: 10px 15px; margin: 10px 0; border-radius: 0 4px 4px 0; }");
        sb.append(".complete-title { color: #2e7d32; font-weight: bold; }");
        sb.append(".footer { background: #eee; padding: 15px; text-align: center; font-size: 12px; color: #777; border-radius: 0 0 8px 8px; }");
        sb.append(".highlight { background-color: #fff3cd; padding: 2px 5px; border-radius: 3px; }");
        sb.append("</style>");
        sb.append("</head><body>");
        sb.append("<div class='container'>");

        // Header
        sb.append("<div class='header'>");
        sb.append("<h1>【监控告警通知】</h1>");
        sb.append("</div>");

        // Content
        sb.append("<div class='content'>");

        // Task info
        sb.append("<div class='task-info'>");
        sb.append("<p><span class='label'>任务名称:</span> ").append(taskName).append("</p>");
        sb.append("<p><span class='label'>通知时间:</span> ").append(notifyTime != null ? notifyTime : "即时").append("</p>");
        sb.append("</div>");

        // Threshold violations
        if (!CollectionUtils.isEmpty(violations)) {
            sb.append("<div class='violation'>");
            sb.append("<div class='violation-title'>阈值告警</div>");
            for (Map<String, Object> violation : violations) {
                sb.append("<div class='violation-item'>");
                sb.append(formatViolationHtml(violation));
                sb.append("</div>");
            }
            sb.append("</div>");
        }

        // Complete reason
        if (StringUtils.hasText(completeReason)) {
            sb.append("<div class='complete'>");
            sb.append("<div class='complete-title'>任务完成</div>");
            sb.append("<p>").append(completeReason).append("</p>");
            sb.append("</div>");
        }

        sb.append("</div>");

        // Footer
        sb.append("<div class='footer'>");
        sb.append("<p>此消息由监控系统自动生成，请勿直接回复</p>");
        sb.append("</div>");

        sb.append("</div></body></html>");

        return sb.toString();
    }

    /**
     * Format violation for HTML output
     */
    private String formatViolationHtml(Map<String, Object> violation) {
        StringBuilder sb = new StringBuilder();

        Object fieldObj = violation.get("field");
        if (fieldObj != null) {
            sb.append("<strong>").append(fieldObj).append("</strong>: ");
        }

        Object valueObj = violation.get("value");
        Object thresholdObj = violation.get("threshold");

        if (valueObj != null) {
            sb.append(valueObj);
        }

        if (thresholdObj != null) {
            sb.append(" (阈值: <span class='highlight'>").append(thresholdObj).append("</span>)");
        }

        Object operatorObj = violation.get("operator");
        if (operatorObj != null) {
            sb.append(" ").append(operatorObj);
        }

        return sb.toString();
    }
}
