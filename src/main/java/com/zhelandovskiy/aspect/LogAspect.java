package com.zhelandovskiy.aspect;

import com.zhelandovskiy.config.HttpLogProperties;
import com.zhelandovskiy.config.LogLevel;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Aspect
public class LogAspect {
    private final Logger log = LoggerFactory.getLogger(LogAspect.class);
    private final HttpLogProperties httpLogProperties;

    public LogAspect(HttpLogProperties httpLogProperties) {
        this.httpLogProperties = httpLogProperties;
    }

    @Around("@annotation(com.zhelandovskiy.annotation.TimeMetric)")
    public Object calculateTime(ProceedingJoinPoint joinPoint) {

        if (httpLogProperties.isTimeMetric()) {
            Object proceed;
            try {
                long start = System.currentTimeMillis();

                proceed = joinPoint.proceed();

                long end = System.currentTimeMillis();

                printLog(LogLevel.INFO, "Time {}: {} ms", joinPoint.toShortString(), end - start);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }

            return proceed;
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Before("@annotation(com.zhelandovskiy.annotation.HttpLog)")
    public void logBefore(JoinPoint joinPoint) {
        printLog(LogLevel.DEBUG, "Start method {}", joinPoint.toShortString());
    }

    @After("@annotation(com.zhelandovskiy.annotation.HttpLog)")
    public void logAfter(JoinPoint joinPoint) {
        printLog(LogLevel.DEBUG, "End method {}", joinPoint.toShortString());
    }

    @AfterReturning(
            value = "@annotation(com.zhelandovskiy.annotation.HttpLog)",
            returning = "result")
    public void calculateRecord(List<?> result) {
        if (result.isEmpty())
            printLog(LogLevel.WARN, "Returning result is empty");

    }

    @AfterThrowing(
            value = "@annotation(com.zhelandovskiy.annotation.HttpLog)",
            throwing = "exception")
    public void exceptionsAdvice(Exception exception) {
        if (httpLogProperties.getLevel() == LogLevel.ERROR)
            printLog(LogLevel.ERROR, "Getting exception: {}", exception.toString());
    }

    private void printLog(LogLevel level, String message, Object... args) {
        if (checkLevel(level))
            switch (level) {
                case DEBUG -> log.debug(message, args);
                case INFO -> log.info(message, args);
                case WARN -> log.warn(message, args);
                case ERROR -> log.error(message, args);
            }
    }

    private boolean checkLevel(LogLevel level) {
        return level.ordinal() >= httpLogProperties.getLevel().ordinal();
    }

}