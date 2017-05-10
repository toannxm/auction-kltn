package hvcntt.org.shoppingweb.service;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.mail.MailSender;import org.springframework.mail.SimpleMailMessage;import org.springframework.mail.javamail.JavaMailSender;import org.springframework.stereotype.Service;/** * Created by Nguyen on 09/05/2017. */@Servicepublic class SendMailServiceImpl implements SendMailService {    @Autowired    private JavaMailSender mailSender;    public static final String SUBJECT = "Contact of website Auction - KLTN";    @Override    public void sendMail(String to, String message) {        SimpleMailMessage mailMessage = new SimpleMailMessage();        mailMessage.setTo(to);        mailMessage.setSubject(SUBJECT);        mailMessage.setText(message);        mailSender.send(mailMessage);    }}