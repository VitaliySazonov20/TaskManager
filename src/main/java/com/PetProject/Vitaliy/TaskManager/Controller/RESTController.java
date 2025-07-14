package com.PetProject.Vitaliy.TaskManager.Controller;


import com.PetProject.Vitaliy.TaskManager.Exception.UserNotFoundException;
import com.PetProject.Vitaliy.TaskManager.Model.CommentModel;
import com.PetProject.Vitaliy.TaskManager.Model.RegistrationForm;
import com.PetProject.Vitaliy.TaskManager.Model.UserModel;
import com.PetProject.Vitaliy.TaskManager.Service.*;
import com.PetProject.Vitaliy.TaskManager.entity.*;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Priority;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RESTController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityContextService securityContextService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LogExportService logExportService;

}
