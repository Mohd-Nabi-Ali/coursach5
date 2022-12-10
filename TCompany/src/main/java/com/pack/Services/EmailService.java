package com.pack.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmailService {
    @Autowired
    private JavaMailSender jms;

    @Async
    public void send(String email,String message){
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom("vaxyzv@gmail.com");
        smm.setTo(email);
        smm.setSubject("\"Voyage\"");
        smm.setText(message);
        jms.send(smm);
    }
}