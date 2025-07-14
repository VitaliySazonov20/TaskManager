package com.PetProject.Vitaliy.TaskManager.Controller.REST;

import com.PetProject.Vitaliy.TaskManager.Exception.UserNotFoundException;
import com.PetProject.Vitaliy.TaskManager.Model.UserModel;
import com.PetProject.Vitaliy.TaskManager.Service.UserService;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    public List<UserModel> getAllUserModels(){
        return userService.getAllUsers().stream()
                .map(user -> new UserModel(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail()))
                .collect(Collectors.toList());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){
        try {
            userService.deleteUserById(BigInteger.valueOf(Long.parseLong(id)));
            return ResponseEntity.ok().body("User deleted successfully");
        } catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error deleting user");
        }

    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> editUser(@PathVariable BigInteger id){
        try{
            User user = userService.getUserById(id);
            UserModel userModel = new UserModel(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail());
            return ResponseEntity.ok(userModel);
        } catch (UserNotFoundException e){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error fetching user");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<String> editUser(@PathVariable BigInteger id,
                                           @RequestBody UserModel userModel){
        try {
            User user = userService.getUserById(id);
            user.setFirstName(userModel.getFirstName());
            user.setLastName(userModel.getLastName());
            user.setEmail(user.getEmail());
            userService.saveUser(user);
            return ResponseEntity.ok().body("User updated successfully");
        } catch(UserNotFoundException e){
            return ResponseEntity.notFound().build();
        }

    }

}
