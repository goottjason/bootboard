package com.miniproj.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Component
@Slf4j
@Service
public class SendEmailService {

  @Value("${spring.mail.username}")
  private String username;

  @Value("${spring.mail.password}")
  private String password;

  public void sendEmail(String emailAddr, String activationCode)
    throws MessagingException, IOException {

    String subject = "😊 legacydiary.com에서 보내는 회원가입 이메일 인증번호입니다.";
    String message = "회원가입을 환영합니다. 인증번호를 입력하시고, 회원가입을 완료하세요. " +
      "인증번호: " + activationCode;

    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.naver.com");
    props.put("mail.smtp.port", "465");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.ssl.enable", "true");
    props.put("mail.smtp.ssl.protocols", "TLSv1.2");
    props.put("mail.smtp.ssl.trust", "smtp.naver.com");

    Session emailSession = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    MimeMessage mime = new MimeMessage(emailSession);
    mime.setFrom(new InternetAddress(username)); // 주입된 username 사용
    mime.addRecipient(RecipientType.TO, new InternetAddress(emailAddr));
    mime.setSubject(subject);
    mime.setText(message);

    Transport.send(mime);
  }
}