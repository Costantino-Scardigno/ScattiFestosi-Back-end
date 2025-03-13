package com.gestioneFoto.controller;

import com.gestioneFoto.model.User;
import com.gestioneFoto.payload.request.LoginRequest;
import com.gestioneFoto.payload.request.SignupRequest;
import com.gestioneFoto.payload.response.JwtResponse;
import com.gestioneFoto.payload.response.MessageResponse;
import com.gestioneFoto.security.JwtTokenUtil;
import com.gestioneFoto.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenUtil.generateToken(authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User
                ? (org.springframework.security.core.userdetails.User) authentication.getPrincipal()
                : null);
        return ResponseEntity.ok(new JwtResponse(jwt, loginRequest.getUsername()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if(userService.findByUsername(signupRequest.getUsername()).isPresent()){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        // Codifica la password
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        // Assegna il ruolo di default (assicurati di avere già inserito questo ruolo nel database)
        // user.setRoles(...);
        userService.saveUser(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
