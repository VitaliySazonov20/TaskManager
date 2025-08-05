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
    public void saveUserCred(UserCredentials userCred) {
        userCredRepo.save(userCred);
    }

    @Transactional
    public List<UserCredentials> getCredentialsByRole(Role role) {
        List<UserCredentials> credentials = userCredRepo.findByRole(role);
        if (credentials.isEmpty()) {
            return Collections.emptyList();
        }
        return credentials;
    }

    public boolean verifyPasswords(String oldPassword, String inputtedPassword) {
        return passwordEncoder.matches(inputtedPassword, oldPassword);
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
