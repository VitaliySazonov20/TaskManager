package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.entity.User;
import com.PetProject.Vitaliy.TaskManager.entity.UserCredentials;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service("security")
public class SecurityContextService {

    public User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserCredentials) {
            return ((UserCredentials) auth.getPrincipal()).getUser();
        }
        throw new IllegalStateException("User not authenticated or principal is of wrong type");
    }

    public boolean isAdmin(){
        Authentication auth = SecurityContextHolder.getContext().
                getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isOwner(BigInteger userId){
        return ((UserCredentials) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getUserId().toString().equals(userId.toString());
    }

}
