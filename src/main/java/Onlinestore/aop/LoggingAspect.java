package Onlinestore.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Intercept all controller methods
    @Around("execution(* com.example.loggingdemo.controller..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        logger.info("Incoming request: {}", method);

        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            logger.debug("Arguments: {}", (Object) args);
        }

        Object result = null;
        try {
            result = joinPoint.proceed();
            logger.info("Response from {}: {}", method, result);
        } catch (Exception e) {
            logger.error("Exception in {}: {}", method, e.getMessage(), e);
            throw e;
        }

        return result;
    }
}