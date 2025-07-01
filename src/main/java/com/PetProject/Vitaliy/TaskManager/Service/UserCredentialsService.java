package com.PetProject.Vitaliy.TaskManager.Service;


import com.PetProject.Vitaliy.TaskManager.Repository.UserCredentialsRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Role;
import com.PetProject.Vitaliy.TaskManager.entity.UserCredentials;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCredentialsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserCredentialsRepository userCredRepo;

    @Transactional
    public void saveUserCred(UserCredentials userCred){
        userCredRepo.save(userCred);
    }

    @Transactional
    public List<UserCredentials> getCredentialsByRole(Role role){
        return userCredRepo.findByRole(role);
    }

    public boolean verifyPasswords(String oldPassword,String inputtedPassword){
        return passwordEncoder.matches(inputtedPassword,oldPassword);
    }
    public String encodePassword(String password){
        return passwordEncoder.encode(password);
    }
}
