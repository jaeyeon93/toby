package toby.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class DummySender implements MailSender {
    public void send(SimpleMailMessage mailMessage) throws MailException {
    }

    public void send(SimpleMailMessage[] mailMessage) throws MailException {
    }
}
