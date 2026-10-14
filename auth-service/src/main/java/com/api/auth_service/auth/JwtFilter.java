package com.api.auth_service.auth;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final CustomUserDetailsService users;

    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h == null || !h.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }
        try {
            String token = h.substring(7);
            String email = jwt.getEmailFromToken(token);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails d = users.loadUserByUsername(email);
                if (jwt.isTokenValid(token, d)) {
                    var a = new UsernamePasswordAuthenticationToken(d, null, d.getAuthorities());
                    a.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(a);
                }
            }
        } catch (JwtException | IllegalArgumentException |
                 org.springframework.security.core.userdetails.UsernameNotFoundException ignored) {
        }
        chain.doFilter(req, res);
    }
}
