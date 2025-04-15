package com.lowcost.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.lowcost.dto.AuthDTO;
import com.lowcost.dto.LoginDTO;
import com.lowcost.service.AuthService;
import com.lowcost.service.JsonParser;

import java.util.Optional;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class AuthController {
    private final AuthService authService;
    @PostMapping
    private String auth(@RequestBody LoginDTO loginDto) throws Exception {
        Optional<AuthDTO> response = authService.auth(loginDto);
        if(response.isEmpty()){
            return "[]";
        }
        return JsonParser.toJsonObject(response.get());
    }

}