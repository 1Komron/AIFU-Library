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



    /**
     * Yangi foydalanuvchiga akkauntini faollashtirish uchun maxsus xat yuboradi.
     * @param toEmail - Yangi adminning emaili.
     * @param code - Noyob faollashtirish kodi (UUID).
     */
 /*   public void sendActivationEmail(String toEmail, String code) {
        // Kelajakda bu yerga front-end manzili qo'yiladi, masalan: "http://my-library-app.com/activate?code="
        String activationLink = "http://localhost:8080/api/auth/activate?code=" + code;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Kutubxona Tizimida Akkauntni Faollashtirish");
        message.setText("Salom!\n\nSiz uchun yangi admin akkaunti yaratildi. " +
                "Akkauntni faollashtirish va tizimga kirishni boshlash uchun quyidagi havolani bosing:\n\n" +
                activationLink +
                "\n\nBu havola 24 soat davomida amal qiladi." +
                "\n\nAgar bu so'rovni siz yubormagan bo'lsangiz, bu xatni e'tiborsiz qoldiring.");

        mailSender.send(message);
    }
*/

}
