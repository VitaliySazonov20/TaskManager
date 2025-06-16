package com.PetProject.Vitaliy.TaskManager.Repository;

import com.PetProject.Vitaliy.TaskManager.entity.Enum.Role;
import com.PetProject.Vitaliy.TaskManager.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long > {
    List<UserCredentials> findByRole(Role role);
}
