package com.rayhan.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        try {
            emailService.sendSimple(request);
            return ResponseEntity.ok("Email terkirim ke " + request.getTo());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Gagal kirim ke " + request.getTo() + ": "
                            + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
