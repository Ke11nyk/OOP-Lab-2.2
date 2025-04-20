package com.lowcost.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.lowcost.dto.RegistrationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import com.lowcost.dto.AuthDTO;
import com.lowcost.dto.LoginDTO;
import com.lowcost.entity.Password;
import com.lowcost.entity.User;
import com.lowcost.repository.UserRepo;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepo userRepository;

    public Optional<AuthDTO> register(RegistrationDTO registrationDTO) {
        // Перевірка на наявність користувача
        if (userRepository.findUserByEmail(registrationDTO.getEmail()).isPresent()) {
            return Optional.empty();
        }

        // Генерація солі та хешування пароля
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(registrationDTO.getPassword(), salt);

        // Створення нового користувача
        User newUser = new User();
        newUser.setLogin(registrationDTO.getLogin()); // Встановлюємо логін
        newUser.setEmail(registrationDTO.getEmail());
        newUser.setRole(registrationDTO.getRole());
        newUser.setPassword(hashedPassword);
        newUser.setSalt(salt);

        // Збереження користувача
        User savedUser = userRepository.save(newUser);

        // Генерація JWT токена
        Algorithm algorithm = Algorithm.HMAC256("baeldung");
        String jwt = JWT.create()
                .withIssuer("Baeldung")
                .withClaim("id", savedUser.getId())
                .withClaim("login", savedUser.getLogin())
                .withClaim("email", savedUser.getEmail())
                .withClaim("role", savedUser.getRole())
                .sign(algorithm);

        return Optional.of(new AuthDTO(jwt, savedUser.getLogin(), savedUser.getId()));
    }

    public Optional<AuthDTO> auth(LoginDTO loginDTO){
        Optional<User> user = userRepository.findUserByEmail(loginDTO.getEmail());
        if(user.isEmpty()){
            return Optional.empty();
        }
        User confirmedUser = user.get();
        Password pwd = Password.builder()
                .hash(confirmedUser.getPassword())
                .salt(confirmedUser.getSalt())
                .build();
        String hashedPwd = BCrypt.hashpw(loginDTO.getPassword(), pwd.getSalt());
        if(!hashedPwd.equals(pwd.getHash())){
            return Optional.empty();
        }
        Algorithm algorithm = Algorithm.HMAC256("baeldung");
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("Baeldung")
                .build();
        String jwt = JWT.create()
                .withIssuer("Baeldung")
                .withClaim("id", confirmedUser.getId())
                .withClaim("login", confirmedUser.getLogin())
                .withClaim("email", confirmedUser.getEmail())
                .withClaim("role", confirmedUser.getRole())
                .sign(algorithm);
        return Optional.of(new AuthDTO(jwt, confirmedUser.getLogin(), confirmedUser.getId()));
    }
}