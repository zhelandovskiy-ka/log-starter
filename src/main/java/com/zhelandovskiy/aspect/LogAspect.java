package com.zhelandovskiy.aspect;

import com.zhelandovskiy.config.HttpLogProperties;
import com.zhelandovskiy.config.LogLevel;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class LogAspect {
    private final Logger log = LoggerFactory.getLogger(LogAspect.class);
    @Autowired
    private HttpLogProperties httpLogProperties;

    @Around("@annotation(com.zhelandovskiy.annotation.HttpLog)")
    public Object calculateTime(ProceedingJoinPoint joinPoint) throws Throwable {

        if (httpLogProperties.isTimeMetric() && httpLogProperties.getLevel() == LogLevel.DEBUG) {
            Object proceed;

            long start = System.currentTimeMillis();
            proceed = joinPoint.proceed();
            long end = System.currentTimeMillis();

            log.info("Time {}: {} ms", joinPoint.toShortString(), end - start);

            return proceed;
        }

        return joinPoint.proceed();
    }

    @Before("@annotation(com.zhelandovskiy.annotation.HttpLog)")
    public void logBefore(JoinPoint joinPoint) {
        if (httpLogProperties.getLevel() == LogLevel.INFO || httpLogProperties.getLevel() == LogLevel.DEBUG)
            log.info("Start method {}", joinPoint.toShortString());
    }

    @After("@annotation(com.zhelandovskiy.annotation.HttpLog)")
    public void logAfter(JoinPoint joinPoint) {
        if (httpLogProperties.getLevel() == LogLevel.INFO || httpLogProperties.getLevel() == LogLevel.DEBUG)
            log.info("End method {}", joinPoint.toShortString());
    }

    @AfterReturning(
            value = "@annotation(com.zhelandovskiy.annotation.HttpLog)",
            returning = "result")
    public void calculateRecord(List<?> result) {
        if (httpLogProperties.getLevel() == LogLevel.WARN && result.isEmpty())
            log.info("Returning result is empty");
    }

    @AfterThrowing(
            value = "@annotation(com.zhelandovskiy.annotation.HttpLog)",
            throwing = "exception")
    public void exceptionsAdvice(Exception exception) {
        if (httpLogProperties.getLevel() == LogLevel.ERROR)
            log.error("Getting exception: {}", exception.toString());
    }

}