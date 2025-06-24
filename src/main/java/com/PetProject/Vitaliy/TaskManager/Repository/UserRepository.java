package com.PetProject.Vitaliy.TaskManager.Repository;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface UserRepository extends JpaRepository<User, Long >{

    boolean existsByEmail(String email);

    User getById(BigInteger id);

}
