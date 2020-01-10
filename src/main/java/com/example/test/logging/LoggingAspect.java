package com.example.test.logging;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static net.logstash.logback.argument.StructuredArguments.value;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    private static String[] formattedStackTrace(StackTraceElement[] stackTrace) {
        if (isNull(stackTrace) || stackTrace.length == 0) {
            return new String[0];
        }

        return Arrays.stream(stackTrace)
                .map(stackFrame -> format("%s.%s (%s:%s)",
                        Optional.ofNullable(stackFrame.getClassName()).orElse(""),
                        Optional.ofNullable(stackFrame.getMethodName()).orElse(""),
                        Optional.ofNullable(stackFrame.getFileName()).orElse(""),
                        Optional.ofNullable(stackFrame.getLineNumber()).orElse(-1)))
                .toArray(String[]::new);
    }

    private static void logException(Exception ex) {
        LOGGER.error("Exception : {}", value("ex", ex.getMessage()),
                value("stacktrace", formattedStackTrace(ex.getStackTrace())));
    }

    @AfterThrowing(value = "@annotation(ServiceLogging)", throwing = "ex")
    public void logExecutionTime(Exception ex) {

        System.out.println("ASPECT ACTIVATED");
        System.out.println("ex = " + ex);

        logException(ex);
    }
}