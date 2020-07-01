package com.merkaba.facesecure2.utils;
//package

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendEmailService {

    private static SendEmailService instance = null;
    private String username = "pradhonoaji@merkaba.co.id";
    private String password = "Innova24101981";
    private String mRecipientAddress = "sufi.aji@gmail.com";

    Properties prop;
    Session session;
    public final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

    private SendEmailService(String host,
                             String port,
                             String sender,
                             String receiver,
                             String password) {
        username = sender;
        mRecipientAddress = receiver;
        this.password = password;

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        prop = new Properties();
        prop.put("mail.smtp.host", host); //"mail.merkaba.co.id");
        prop.put("mail.smtp.port", port); //"465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.socketFactory.port", port); //"465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.socketFactory.fallback", "false");

        session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    public static synchronized SendEmailService getInstance(String host,
                                                            String port,
                                                            String sender,
                                                            String receiver,
                                                            String password) {
        if(instance == null) {
            instance = new SendEmailService(host, port, sender, receiver, password);
        }
        return instance;
    }

    public void SendEmail(String mailSubject, String mailContent) {
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(mRecipientAddress));
            message.setSubject(mailSubject);
            //
            Multipart multipart = new MimeMultipart();

            //zip attachment

            // text attachment 1
            MimeBodyPart textBodyPart1 = new MimeBodyPart();
            ByteArrayDataSource tds1 = new ByteArrayDataSource(mailContent.getBytes(Charset.forName("UTF-8")), "text/plain");
            textBodyPart1.setDataHandler(new DataHandler(tds1));
            textBodyPart1.setHeader("Content-ID", "<text>");
            textBodyPart1.setFileName(mailSubject + ".txt");
            multipart.addBodyPart(textBodyPart1);

            message.setContent(multipart);

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public void SendEmail(Bitmap bitmap) {
        Log.d("SEND EMAIL ACTIVITY", "start");
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(mRecipientAddress)
            );
            message.setSubject("Testing Email TLS");
            //message.setText("Welcome to Medium!");

            Multipart multipart = new MimeMultipart();

            //text
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText = "<H1>Welcome to Medium!</H1>";
            messageBodyPart.setContent(htmlText, "text/html");
            multipart.addBodyPart(messageBodyPart);

            //image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageInByte = baos.toByteArray();

            MimeBodyPart imageBodyPart = new MimeBodyPart();
            ByteArrayDataSource bds = new ByteArrayDataSource(imageInByte, "image/png");
            imageBodyPart.setDataHandler(new DataHandler(bds));
            imageBodyPart.setHeader("Content-ID", "<image>");

            imageBodyPart.setFileName("Example.png");
            multipart.addBodyPart(imageBodyPart);

            //attachment
            MimeBodyPart textBodyPart = new MimeBodyPart();
            ByteArrayDataSource tds = new ByteArrayDataSource("text".getBytes(Charset.forName("UTF-8")), "text/plain");
            textBodyPart.setDataHandler(new DataHandler(tds));
            textBodyPart.setHeader("Content-ID", "<text>");
            textBodyPart.setFileName("Example.txt");
            multipart.addBodyPart(textBodyPart);

            message.setContent(multipart);

            Transport.send(message);
            Log.d("SEND EMAIL ACTIVITY", "finish");
        } catch (MessagingException e) {
            e.printStackTrace();
            Log.d("SEND EMAIL ACTIVITY", "error");
        }
    }
}
