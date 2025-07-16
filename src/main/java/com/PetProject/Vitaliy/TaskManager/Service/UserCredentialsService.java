package com.PetProject.Vitaliy.TaskManager.Service;


import com.PetProject.Vitaliy.TaskManager.Repository.UserCredentialsRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Role;
import com.PetProject.Vitaliy.TaskManager.entity.UserCredentials;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class UserCredentialsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserCredentialsRepository userCredRepo;

    @Transactional
    public void saveUserCred(UserCredentials userCred){
        Objects.requireNonNull(userCred, "User credentials cannot be null");
        try {
            Objects.requireNonNull(userCred.getPassword(), "Password cannot be empty");
            Objects.requireNonNull(userCred.getRole(), "Role must be specified");

            if (!isPasswordEncoded(userCred.getPassword())) {
                userCred.setPasswordHash(encodePassword(userCred.getPassword()));
            }
            userCredRepo.save(userCred);
        } catch (DataAccessException e){
            throw new ServiceException("Failed to save credentials", e);
        }
    }

    @Transactional
    public List<UserCredentials> getCredentialsByRole(Role role){
        Objects.requireNonNull(role, "Role cannot be null");
        try {
            List<UserCredentials> credentials = userCredRepo.findByRole(role);
            if(credentials.isEmpty() || credentials == null){
                return Collections.emptyList();
            }
            return credentials;
        } catch (DataAccessException e){
            throw new ServiceException("Failed to load credentials", e);
        }
    }

    public boolean verifyPasswords(String oldPassword,String inputtedPassword){
        if(StringUtils.isBlank(oldPassword)){
            throw new IllegalArgumentException("Old password cannot be blank");
        }
        if(StringUtils.isBlank(inputtedPassword)){
            throw new IllegalArgumentException("New password cannot be blank");
        }
        try {
            return passwordEncoder.matches(inputtedPassword,oldPassword);
        } catch (Exception e){
            return false;
        }
    }
    public String encodePassword(String password){
        if(StringUtils.isBlank(password)){
            throw new IllegalArgumentException("Password cannot be blank");
        }
        try {
            String encoded = passwordEncoder.encode(password);
            if (encoded == null){
                throw new ServiceException("Password encoding failed");
            }
            return encoded;
        } catch (Exception e){
            throw new ServiceException("Failed to encode password",e);
        }
    }

    private boolean isPasswordEncoded(String password) {
        return password.startsWith("$2a$"); // BCrypt pattern check
    }
}
