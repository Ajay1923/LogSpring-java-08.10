package com.crud.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.crud.demo.model.MailDetails;
import com.crud.demo.repository.MailDetailsRepository;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private MailDetailsRepository mailDetailsRepository;

    public EmailService(JavaMailSender mailSender, MailDetailsRepository mailDetailsRepository) {
        this.mailSender = mailSender;
        this.mailDetailsRepository = mailDetailsRepository;
    }

    // Method to send email with or without an attachment
    public void sendMail(String to, String subject, String text, File attachment) throws Exception {
        MailDetails mailDetails = new MailDetails(to, subject, text, "SENT");

        try {
            // Create a MimeMessage for email with attachments
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // true indicates multipart message

            helper.setFrom("ajaychandru19@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            // Attach the file if provided
            if (attachment != null) {
                helper.addAttachment(attachment.getName(), attachment);
            }

            mailSender.send(mimeMessage);

            // Persist email record with success status
            mailDetailsRepository.save(mailDetails);
        } catch (Exception e) {
            // If there's an error, update the status to FAILED
            mailDetails.setStatus("FAILED: " + e.getMessage());
            mailDetailsRepository.save(mailDetails);
            throw e;
        }
    }
}
