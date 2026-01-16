package com.monitor.module.notify.channel;

import com.monitor.module.notify.dto.NotifyRequest;
import com.monitor.module.notify.dto.NotifyResult;

/**
 * Notification channel interface
 *
 * @author monitor
 */
public interface NotifyChannel {

    /**
     * Get channel name
     *
     * @return Channel name
     */
    String getChannelName();

    /**
     * Send notification
     *
     * @param request Notification request
     * @return Send result
     */
    NotifyResult send(NotifyRequest request);

    /**
     * Check if channel is enabled
     *
     * @return true if enabled
     */
    boolean isEnabled();
}
