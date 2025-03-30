package com.app.aspect;

import com.app.security.AuthenticationMetadata;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @After(value = "@annotation(com.app.annotation.LogSpeciesAction)")
    public void logAfter(JoinPoint joinPoint) {
        AuthenticationMetadata principal = (AuthenticationMetadata) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.warn("User with username {}, and id {} called {} for species with id {}.",
                principal.getUsername(),
                principal.getUserId(),
                joinPoint.toShortString()
        ,joinPoint.getArgs()[0].toString());
    }
}
