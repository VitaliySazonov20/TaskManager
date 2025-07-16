package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.Exception.UserNotFoundException;
import com.PetProject.Vitaliy.TaskManager.Model.RegistrationForm;
import com.PetProject.Vitaliy.TaskManager.Repository.UserCredentialsRepository;
import com.PetProject.Vitaliy.TaskManager.Repository.UserRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Role;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import com.PetProject.Vitaliy.TaskManager.entity.UserCredentials;
//import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserCredentialsRepository userCredRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    @Transactional
    public void saveUser(User user){
        try {
            Objects.requireNonNull(user,"User cannot be null");
            userRepo.save(user);
        } catch (DataIntegrityViolationException e){
            throw new ServiceException("Failed to save user due to data constraints", e);
        } catch (DataAccessException e){
            throw new ServiceException("Failed to save user", e);
        }
    }

    @Transactional(readOnly = true)
    public boolean checkIfEmailExists(String email){
        if(StringUtils.isBlank(email)){
            throw new IllegalArgumentException("Email cannot be blank");
        }
        try {
            return userRepo.existsByEmail(email);
        } catch (DataAccessException e){
            throw new ServiceException("Email check service unavailable", e);
        }
    }

    @Transactional
    public void registerUser(RegistrationForm form){
        try{
            Objects.requireNonNull(form, "Registration form cannot be null");

            User user = new User(form.getFirstName(), form.getLastName(), form.getEmail());

            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setRole(Role.USER);
            userCredentials.setPasswordHash(passwordEncoder.encode(form.getPassword()));
            userCredentials.setUser(user);
            user.setUserCredentials(userCredentials);

            userRepo.save(user);
            userCredRepo.save(userCredentials);
        } catch (DataAccessException e){
            throw new ServiceException("Registration failed", e);
        }
    }

    @Transactional
    public List<User> getAllUsers(){
        try{
            List<User> users = userRepo.findAll();
            return users != null ? users:Collections.emptyList();
        } catch (DataAccessException e){
            throw new ServiceException("Failed to load users", e);
        }
    }

    @Transactional
    public User getUserById(BigInteger id){
        Objects.requireNonNull(id, "User ID cannot be null");
        if(!userRepo.existsById(id)){
            throw new UserNotFoundException(id);
        }
        try {
            return userRepo.getById(id);
        } catch (DataAccessException e){
            throw new ServiceException("Failed to retrieve user", e);
        }
    }

    @Transactional
    public void deleteUserById(BigInteger id){
        Objects.requireNonNull(id, "User ID cannot be null");
        if(!userRepo.existsById(id)){
            throw new UserNotFoundException(id);
        }
        try{
            userRepo.deleteById(id);
        }catch (DataAccessException e){
            throw new ServiceException("Failed to delete user", e);
        }
    }

    @Transactional
    public User getUserByEmail(String email){
        Objects.requireNonNull(email, "User email cannot be null");
        if(!userRepo.existsByEmail(email)){
            throw new UserNotFoundException(email);
        }
        try {
            return userRepo.getByEmail(email);
        }catch (DataAccessException e){
            throw new ServiceException("Failed to retrieve user", e);
        }
    }
}
