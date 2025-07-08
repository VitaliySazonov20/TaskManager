package com.PetProject.Vitaliy.TaskManager.Aspect;

import com.PetProject.Vitaliy.TaskManager.Repository.AuditLogRepository;
import com.PetProject.Vitaliy.TaskManager.Service.SecurityContextService;
import com.PetProject.Vitaliy.TaskManager.entity.AuditLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditLogAspect {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private SecurityContextService securityContextService;

//    @AfterReturning(pointcut = "execution(* com.PetProject.Vitaliy.TaskManager.Controller.AuthenticationController.showLoginPage(..))", returning = "result")
//    public void logLogin(JoinPoint joinPoint, Object result){
//        String username = securityContextService.getCurrentUser().getEmail();
//        AuditLog auditLog = new AuditLog(username, "LOGIN", "User" + username + "logged in");
//        auditLogRepository.save(auditLog);
//    }


}
