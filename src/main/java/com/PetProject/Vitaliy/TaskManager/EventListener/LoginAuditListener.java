package com.PetProject.Vitaliy.TaskManager.EventListener;

import com.PetProject.Vitaliy.TaskManager.Repository.AuditLogRepository;
import com.PetProject.Vitaliy.TaskManager.entity.AuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginAuditListener {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @EventListener
    public void onLoginSuccess(InteractiveAuthenticationSuccessEvent event){
        String email = event.getAuthentication().getName();
        AuditLog log = new AuditLog(email,"LOGIN","User "+ email + " logged in successfully");
        auditLogRepository.save(log);
    }

    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent event){
        String email = event.getAuthentication() !=null ?
                event.getAuthentication().getName(): "SYSTEM (session expired)";
        AuditLog log = new AuditLog(email, "LOGOUT","User "+ email + " logged out successfully");
        auditLogRepository.save(log);

    }
}
