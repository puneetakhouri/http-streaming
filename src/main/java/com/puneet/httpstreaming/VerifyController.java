package com.puneet.httpstreaming;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerifyController {


    @PostMapping("/verify")
    public ResponseEntity<?> verify(HttpServletRequest request, HttpServletResponse response) {
        if(request.getHeader("Authorization") != null){
            String authorization = request.getHeader("Authorization");
            if("Xyz".equals(authorization))
                return ResponseEntity.ok("Valid Authorization");
        }
        return ResponseEntity.status(401).body("Invalid Authorization");
    }
}
