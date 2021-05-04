package com.plus.bysj.account.utils;

import com.sun.mail.smtp.SMTPTransport;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

/**
 * @ProjectName: account
 * @Package: com.plus.bysj.account.utils
 * @ClassName: MailUtils
 * @Author: rh
 * @Description:
 * @Date: 2021/4/23 18:03
 */
@Log4j2
public class MailUtils {
    //邮件服务器主机地址
    private static String HOST="121.126.97.2";
    //帐号
    private static String ACCOUNT = "debbie202011@gmail.com";
    //密码
    private static String PASSWORD = "";
    //授权码
    private static String AUTH = "GNKQACDEQUEJAGKU";



    /**
     * @param toUser  发送邮件给谁
     * @param title   邮件的标题
     * @param emailMsg  邮件信息
     */
    public static void sendMail1(String toUser,String title, String emailMsg)throws  MessagingException {
        // 1.创建一个程序与邮件服务器会话对象 Session
        Properties props = new Properties();
        //设置发送的协议
        props.setProperty("mail.transport.protocol", "SMTP");

        //设置发送邮件的服务器
        props.setProperty("mail.host", HOST);
        props.setProperty("mail.smtp.auth", "true");// 指定验证为true

//        props.setProperty("mail.smtp.socketFactory.port", "465");
//        props.setProperty("mail.smtp.port", "465");

        // 创建验证器
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                //设置发送人的帐号和密码      帐号		   授权码
                return new PasswordAuthentication(ACCOUNT, AUTH);
            }
        };
        //会话
        Session session = Session.getInstance(props, auth);

        // 2.创建一个Message，它相当于是邮件内容
        Message message = new MimeMessage(session);

        //设置发送者
        message.setFrom(new InternetAddress(ACCOUNT));

        //设置发送方式与接收者
        message.setRecipient(RecipientType.TO, new InternetAddress(toUser));

        //设置邮件主题
        message.setSubject(title);
        // message.setText("这是一封激活邮件，请<a href='#'>点击</a>");

        //设置邮件内容
        message.setContent(emailMsg, "text/html;charset=utf-8");

        // 3.创建 Transport用于将邮件发送
        Transport.send(message);
        //log.info("邮件发送成功");
    }


    public static void main(String[] args) throws AddressException, MessagingException {
		//MailUtils.sendMail("6357@foxmail.com", "祝福邮件2", "哈哈哈");
        MailUtils.sendMail("debbie202011@gmail.com", "测试", "这是正文11111");
        System.out.println("成功");
    }

    /**
     * @param toUser  发送邮件给谁
     * @param title   邮件的标题
     * @param emailMsg  邮件信息
     */
    public static void sendMail(String toUser,String title, String emailMsg)throws  MessagingException {
        String qq="hnwiyoqrcftebfdg";
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.qq.com");
        javaMailSender.setPort(465);
        javaMailSender.setUsername("635760163@qq.com");
        javaMailSender.setPassword(qq);
        Properties properties = new Properties();
        properties.setProperty("mail.host", "smtp.qq.com");
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.socketFactory.port", "465");

        javaMailSender.setJavaMailProperties(properties);
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true, "utf-8");
        helper.setFrom("635760163@qq.com");// 设置发件人
        helper.setTo(toUser);// 设置收件人
//            helper.setCc(cc);// 设置抄送
        helper.setSubject(title);// 设置主题
        helper.setText(emailMsg);// 邮件体
        javaMailSender.send(mailMessage);// 发送邮件
        //System.out.println("邮件发送成功...");
       log.info("邮件发送成功...");
    }

    public static void sendMail2(String ccEmail, String title, String message) throws AddressException, MessagingException {
        String username ="debbie202011@gmail.com";
        String password = "binghongcha";
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
//        props.setProperty("mail.smtps.host", "smtp.gmail.com");
//        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
//        props.setProperty("mail.smtp.socketFactory.fallback", "false");
//        props.setProperty("mail.smtp.port", "465");
//        props.setProperty("mail.smtp.socketFactory.port", "465");
//        props.setProperty("mail.smtps.auth", "true");
        props = System.getProperties();
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(ccEmail, false));

//        if (ccEmail.length() > 0) {
//            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
//        }

        msg.setSubject(title);
        msg.setText(message, "utf-8");
        msg.setSentDate(new Date());

        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

        t.connect("smtp.gmail.com", username, password);
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }
}
