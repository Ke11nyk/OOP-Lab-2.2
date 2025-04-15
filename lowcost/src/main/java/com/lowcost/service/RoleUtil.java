package com.lowcost.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Arrays;
import java.util.List;

public class RoleUtil {
    public static final String ADMIN = "admin";
    public static final String DISPATCH = "dispatch";
    public static final String USER = "user";

    public static List<String> getAllowedRoles(String[] roles){
        return Arrays.asList(roles);
    }

    public static boolean validateAccess(String role, List<String> allowedRoles){
        return allowedRoles.contains(role);
    }

    public static String getRole(String token){
        Algorithm algorithm = Algorithm.HMAC256("baeldung");
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("Baeldung")
                .build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getClaim("role").asString();
    }
}