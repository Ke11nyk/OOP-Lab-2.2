package com.lowcost.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lowcost.dto.AuthDTO;
import com.lowcost.dto.LoginDTO;
import com.lowcost.dto.RegistrationDTO;
import com.lowcost.service.AuthService;

import java.util.Optional;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> auth(@RequestBody LoginDTO loginDto) {
        Optional<AuthDTO> response = authService.auth(loginDto);
        if(response.isEmpty()){
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        return ResponseEntity.ok(response.get());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDTO registrationDto) {
        Optional<AuthDTO> response = authService.register(registrationDto);
        if(response.isEmpty()){
            return ResponseEntity.badRequest().body("Registration failed - user may already exist");
        }
        return ResponseEntity.ok(response.get());
    }
}