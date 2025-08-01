package aifu.project.libraryweb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;


    public void sendConfirmationCode(String toEmail, String code){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Password Change Confirmation Code");
        message.setText("Hello,\n\nYour confirmation code to change your password is: " + code +
                "\n\nThis code will expire in 10 minutes." +
                "\n\nIf you did not request this, please ignore this email.");
        mailSender.send(message);

    }



    public void sendSimpleMessage(String toEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }


}
