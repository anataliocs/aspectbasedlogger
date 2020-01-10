package com.example.test.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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
                .filter(Objects::nonNull)
                .map(stackFrame -> format("%s.%s (%s:%s)",
                        Optional.ofNullable(stackFrame.getClassName()).orElse(""),
                        Optional.ofNullable(stackFrame.getMethodName()).orElse(""),
                        Optional.ofNullable(stackFrame.getFileName()).orElse(""),
                        Optional.ofNullable(stackFrame.getLineNumber()).orElse(-1)))
                .toArray(String[]::new);
    }

    private static void logException(Exception ex, Logger logger, String message) {
        logger.error("Exception({}) : {}",
                value("ex", message),
                value("ex", ex.getMessage()),
                value("stacktrace", formattedStackTrace(ex.getStackTrace())));
    }

    private static Optional<ServiceLogging> getServiceLoggingAnnotation(JoinPoint joinPoint) {
        if (isNull(joinPoint) || isNull(joinPoint.getSignature())) {
            return Optional.empty();
        }

        final ServiceLogging serviceLogging;
        try {
            final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            final Method method = signature.getMethod();

            serviceLogging = method.getAnnotation(ServiceLogging.class);
        } catch (Exception e) {
            return Optional.empty();
        }

        return Optional.of(serviceLogging);
    }

    private static Logger getLoggerFromJoinPoint(JoinPoint joinPoint) {

        return nonNull(joinPoint) && nonNull(joinPoint.getTarget())
                ? LoggerFactory.getLogger(joinPoint.getTarget().getClass()) : LOGGER;
    }

    private static String getErrorMsg(Optional<ServiceLogging> serviceLogging) {
        final StringBuilder s = new StringBuilder();
        serviceLogging.ifPresent(annotation -> s.append(annotation.message()));

        return s.toString();
    }

    @AfterThrowing(pointcut = "@annotation(ServiceLogging)",
            throwing = "ex",
            argNames = "joinPoint,ex")
    public void logExecutionTime(JoinPoint joinPoint, Exception ex) {

        System.out.println("ASPECT ACTIVATED");
        System.out.println("ex = " + ex);
        System.out.println("serviceLogging = ");
        System.out.println("joinPoint = " + joinPoint);
        System.out.println("joinPoint = " + joinPoint.getClass());
        System.out.println("joinPoint kind = " + joinPoint.getKind());
        System.out.println("joinPoint args = " + joinPoint.getArgs());
        System.out.println("joinPoint toString = " + joinPoint.toLongString());
        System.out.println("joinPoint sig = " + joinPoint.getSignature());
        System.out.println("joinPoint source location = " + joinPoint.getSourceLocation());
        System.out.println("joinPoint target = " + joinPoint.getTarget());
        System.out.println("joinPoint = " + joinPoint.getStaticPart());

        System.out.println("joinPoint = " + joinPoint.getArgs().length);

        final Optional<ServiceLogging> serviceLogging = getServiceLoggingAnnotation(joinPoint);

        logException(ex, getLoggerFromJoinPoint(joinPoint), getErrorMsg(serviceLogging));
    }
}