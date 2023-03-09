package com.example.demowithtests.util.mail;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.util.exception.EmailSendingException;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
public final class SmtpMailer implements Mailer {
    private final Properties props;
    // Настройки для подключения к SMTP-серверу Gmail
    private final String host = "smtp.gmail.com";
    private final String username = "hilleljavaee2023@gmail.com";
    private final String password = "qdrjzliussaasliv";
    private final int port = 587;

    public SmtpMailer() {
        // Создаем свойства для подключения
        this.props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
    }

    //    @Override
    //    public void apply(Object o) {
    //        send((String) o);
    //    }

    @Override
    public void send(Employee e) {
// Создаем сессию
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        // Создаем письмо
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(e.getEmail()));
            message.setSubject("Confirmation");
            message.setText(

//                    http://localhost:8087/api/users/1/confirm
                    "    Пожалуйста, нажмите на ссылку ниже, чтобы подтвердить свою регистрацию:\n" +
//                            "<a href=\"https://ваш-сервер.com/activate?userId=12345">Подтвердить регистрацию</a>"
                            "    <a href=\"http://localhost:8087/api/users/" + e.getId() + "/confirmed" +
                            ">Подтвердить регистрацию</a>\n" +
                            "Если вы не регистрировались на нашем сайте, проигнорируйте это сообщение.\n");
            // Отправляем письмо
            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException exception) {
            throw new EmailSendingException("Sending mail from " + username + " to " + e.getEmail() + " issue.");
        }

    }

}
