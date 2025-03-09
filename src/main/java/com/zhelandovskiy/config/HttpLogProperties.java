package com.zhelandovskiy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "http-log")
public class HttpLogProperties {
    private boolean enabled = false;
    private boolean timeMetric = false;
    private LogLevel level = LogLevel.INFO;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isTimeMetric() {
        return timeMetric;
    }

    public void setTimeMetric(boolean timeMetric) {
        this.timeMetric = timeMetric;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }
}