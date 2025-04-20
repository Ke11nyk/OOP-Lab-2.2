package com.lowcost.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final String SECRET = "baeldung";
    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HEADER);

        if (header == null || !header.startsWith(PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = header.replace(PREFIX, "");
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            DecodedJWT jwt = JWT.require(algorithm)
                    .withIssuer("Baeldung")
                    .build()
                    .verify(token);

            String role = jwt.getClaim("role").asString();
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    jwt.getSubject(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(request, response);

        } catch (JWTVerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT token");
        }
    }
}