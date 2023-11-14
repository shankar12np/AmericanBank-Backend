package com.example.americanbank.Controller;

import com.example.americanbank.Config.JwtUtil;
import com.example.americanbank.DTO.AuthenticationResponse;
import com.example.americanbank.Entity.User;
import com.example.americanbank.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/login")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User newUser = userService.registerUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }


    @PostMapping("/auth")
    public ResponseEntity<?> loginUser(@RequestBody User user){
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            boolean passwordMatches = bCryptPasswordEncoder.matches(user.getPassword(), userDetails.getPassword());

            if (passwordMatches){
                String jwt = jwtUtil.generateTokenForUser(userDetails);
                return ResponseEntity.ok(new AuthenticationResponse(jwt, "Login Successful!"));
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .header("WWW-Authenticate", "Basic realm=\"Access to the staging site\"")
                        .body("Milenaaaaa username or password");
            }
        }
        catch (UsernameNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"Access to the staging site\"")
                    .body("Username not found");
        } catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Geda Jasto: !!!" + e.getMessage());
        }
    }

}
