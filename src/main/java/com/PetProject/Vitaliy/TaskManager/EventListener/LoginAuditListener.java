package com.PetProject.Vitaliy.TaskManager.EventListener;

import com.PetProject.Vitaliy.TaskManager.Model.UserModel;
import com.PetProject.Vitaliy.TaskManager.Repository.AuditLogRepository;
import com.PetProject.Vitaliy.TaskManager.Service.AuditLogService;
import com.PetProject.Vitaliy.TaskManager.Service.UserService;
import com.PetProject.Vitaliy.TaskManager.entity.AuditLog;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoginAuditListener {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserService userService;

    @EventListener
    public void onLoginSuccess(InteractiveAuthenticationSuccessEvent event){
        String email = event.getAuthentication().getName();
        User loggedInUser =userService.getUserByEmail(email);

        UserModel user = new UserModel();
        user.setFirstName(loggedInUser.getFirstName());
        user.setLastName(loggedInUser.getLastName());
        user.setEmail(loggedInUser.getEmail());
        user.setId(loggedInUser.getId());
        user.setRole(loggedInUser.getUserCredentials().getRole());

        Map<String, Object> map = new HashMap<>();
        map.put("User",user);

        AuditLog log = new AuditLog(
                email,
                "LOGIN",
                "User "+ email + " logged in successfully",
                map);
        auditLogService.save(log);
    }

    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent event){
        String email = event.getAuthentication() !=null ?
                event.getAuthentication().getName(): "SYSTEM (session expired)";

        User loggedInUser =userService.getUserByEmail(email);

        UserModel user = new UserModel();
        user.setFirstName(loggedInUser.getFirstName());
        user.setLastName(loggedInUser.getLastName());
        user.setEmail(loggedInUser.getEmail());
        user.setId(loggedInUser.getId());
        user.setRole(loggedInUser.getUserCredentials().getRole());

        Map<String, Object> map = new HashMap<>();
        map.put("User",user);

        AuditLog log = new AuditLog(
                email,
                "LOGOUT",
                "User "+ email + " logged out successfully",
                map);
        auditLogService.save(log);

    }
}
