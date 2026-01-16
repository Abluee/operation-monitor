package com.monitor.module.notify.controller;

import com.monitor.common.result.Result;
import com.monitor.module.notify.dto.*;
import com.monitor.module.notify.entity.BizNotifyRecord;
import com.monitor.module.notify.service.NotifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Notification controller
 *
 * @author monitor
 */
@Slf4j
@RestController
@RequestMapping("/api/notify")
@Api(tags = "Notification Management")
@Validated
public class NotifyController {

    private final NotifyService notifyService;

    public NotifyController(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    /**
     * Send notification manually
     *
     * @param request Notification request
     * @return Send results
     */
    @PostMapping("/send")
    @ApiOperation("Send notification manually")
    public Result<List<NotifyResult>> sendNotify(@RequestBody @ApiParam("Notification request") NotifyRequest request) {
        log.info("Received manual notification request for task: {}", request.getTaskId());

        if (request.getTaskId() == null) {
            return Result.fail("Task ID is required");
        }

        List<NotifyResult> results = notifyService.sendNotify(request);
        return Result.success(results);
    }

    /**
     * Send DingTalk notification
     *
     * @param request DingTalk request
     * @return Send result
     */
    @PostMapping("/dingtalk")
    @ApiOperation("Send DingTalk notification")
    public Result<NotifyResult> sendDingTalk(@RequestBody @ApiParam("DingTalk request") DingTalkRequest request) {
        log.info("Received DingTalk notification request");

        if (!StringUtils.hasText(request.getAccessToken())) {
            return Result.fail("Access token is required");
        }

        NotifyResult result = notifyService.sendDingTalk(request);
        if (result.getSuccess()) {
            return Result.success(result);
        } else {
            return Result.fail(result.getErrorMsg());
        }
    }

    /**
     * Send email notification
     *
     * @param request Email request
     * @return Send result
     */
    @PostMapping("/email")
    @ApiOperation("Send email notification")
    public Result<NotifyResult> sendEmail(@RequestBody @ApiParam("Email request") EmailRequest request) {
        log.info("Received email notification request");

        if (request.getTo() == null || request.getTo().isEmpty()) {
            return Result.fail("Recipient list is required");
        }

        NotifyResult result = notifyService.sendEmail(request);
        if (result.getSuccess()) {
            return Result.success(result);
        } else {
            return Result.fail(result.getErrorMsg());
        }
    }

    /**
     * Get notification records by task ID
     *
     * @param taskId Task ID
     * @param pageNum Page number
     * @param pageSize Page size
     * @return Notification records
     */
    @GetMapping("/records/{taskId}")
    @ApiOperation("Get notification records by task ID")
    public Result<Map<String, Object>> getNotifyRecords(
            @PathVariable @ApiParam("Task ID") Long taskId,
            @RequestParam(required = false) @ApiParam("Page number") Integer pageNum,
            @RequestParam(required = false) @ApiParam("Page size") Integer pageSize) {
        log.info("Getting notification records for task: {}", taskId);

        if (pageNum != null || pageSize != null) {
            return Result.success(notifyService.getNotifyRecords(taskId, pageNum, pageSize));
        } else {
            List<BizNotifyRecord> records = notifyService.getNotifyRecords(taskId);
            Map<String, Object> result = new HashMap<>();
            result.put("list", records);
            return Result.success(result);
        }
    }

    /**
     * Retry failed notifications for a task
     *
     * @param taskId Task ID
     * @return Retry results
     */
    @PostMapping("/retry/{taskId}")
    @ApiOperation("Retry failed notifications")
    public Result<List<NotifyResult>> retryFailedNotifies(@PathVariable @ApiParam("Task ID") Long taskId) {
        log.info("Retrying failed notifications for task: {}", taskId);

        List<NotifyResult> results = notifyService.retryFailedNotifies(taskId);
        return Result.success(results);
    }

    /**
     * Get available notification channels
     *
     * @return List of channel names
     */
    @GetMapping("/channels")
    @ApiOperation("Get available notification channels")
    public Result<List<String>> getAvailableChannels() {
        List<String> channels = notifyService.getAvailableChannels();
        return Result.success(channels);
    }

    /**
     * Get single notification record by ID
     *
     * @param id Record ID
     * @return Notification record
     */
    @GetMapping("/record/{id}")
    @ApiOperation("Get notification record by ID")
    public Result<BizNotifyRecord> getRecordById(@PathVariable @ApiParam("Record ID") Long id) {
        log.info("Getting notification record: {}", id);

        List<BizNotifyRecord> records = notifyService.getNotifyRecords(id);
        if (records.isEmpty()) {
            return Result.fail("Record not found");
        }
        return Result.success(records.get(0));
    }
}
