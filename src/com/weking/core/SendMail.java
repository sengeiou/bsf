package com.weking.core;

import com.wekingframework.core.LibProperties;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class SendMail {

    static Logger log = Logger.getLogger(SendMail.class);

    public static void main(String[] args) {

        String subject = "主播收益详情";//标题
        String body = "这是贵公会昨日的收益详情~!";// 内容
        //sendEmail("1099213876@qq.com", body, "zh_CN");
        //sendFromGMail( "15571169379@163.com", subject, body);
    }

    /**
     * 发送邮箱
     *
     * @param email   邮箱
     * @param content 内容
     */
    public static boolean sendEmail(String email, String content, String langCode) {
        boolean flag = true;
        final Properties props = new Properties();
//		String host = "smtp.exmail.qq.com";  //邮箱SMTP服务器
//		String port = "465";                   //邮箱端口号
//		String user = "no-reply@ezbizy.com";  //邮箱账号
//		String password = "Ezbizy2016";  //邮箱密码
        String host = "smtp.qq.com";  //邮箱SMTP服务器
        String port = "465";
        String user = "541086428@qq.com";  //邮箱账号
        //String password = "rpjmsotzkeyrbbga";  //邮箱密码
        String password = "vlnpwntdccicbegc";
        /*
         * 可用的属性： mail.store.protocol / mail.transport.protocol / mail.host /
         * mail.user / mail.from
         */
        // 表示SMTP发送邮件，需要进行身份验证
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.port", port);
        // 发件人的账号
        props.put("mail.user", user);
        // 访问SMTP服务时需要提供的密码
        props.put("mail.password", password);
        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        // 设置发件人
        try {
            InternetAddress form = new InternetAddress(
                    props.getProperty("mail.user"));
            message.setFrom(form);
            InternetAddress to = new InternetAddress(email);
            message.setRecipient(RecipientType.TO, to);
			/*// 设置抄送
			InternetAddress cc = new InternetAddress("luo_aaaaa@yeah.net");
			message.setRecipient(RecipientType.CC, cc);*/

            // 设置密送，其他的收件人不能看到密送的邮件地址
			/*InternetAddress bcc = new InternetAddress(address);
			message.setRecipient(RecipientType.CC, bcc);*/
            // 设置邮件标题
            String title = LibProperties.getLanguage(langCode, "weking.lang.app.name");
            System.out.println(title);
            message.setSubject(title);
            // 设置邮件的内容体
            message.setContent(content, "text/html;charset=UTF-8");
            // 发送邮件
            System.out.println("发送邮件");
            Transport.send(message);
        } catch (AddressException e) {
            // TODO Auto-generated catch block
            flag = false;
            log.info(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            flag = false;
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return flag;
    }

    /**
     * 邮箱验证码内容
     */
    public String codeContent(String captchat, String langCode) {
        String content = "";
        content += "<center style='margin: 0px; padding: 0px;'>";
        content += "<table cellpadding=0' cellspacing='0' border='0' style='margin: 0px; padding: 0px;'>" +
                "<tbody style='margin: 0px; padding: 0px;'><tr style='margin: 0px; padding: 0px;'>" +
                "<td style='margin: 0px; padding: 0px;'>" +
                "<table style='margin: 0px; padding: 0px;'>" +
                "<tbody style='margin: 0px; padding: 0px;'><tr style='margin: 0px; padding: 0px;'><td valign='middle' width='60' height='100' style='margin: 0px; padding: 0px;'></td>" +
                "<td style='margin: 0px; padding: 0px;'><div style='font-size: 20px; margin: 0px; padding: 0px;'></div>" +
                "<div style='color: rgb(102, 102, 102); margin: 0px; padding: 0px;'></div></td>" +
                "</tr></tbody></table></td></tr>" +
                "<tr bgcolor='#ffffff' style='margin: 0px; padding: 0px;'>" +
                "<td style='padding: 0px; margin: 0px;'>" +
                "<div style='margin: 0px; padding: 0px;'></div>" +
                "<div style='margin: 0px; padding: 0px;'>" + LibProperties.getLanguage(langCode, "weking.lang.app.vcode") + captchat + "。</div><br style='margin: 0px; padding: 0px;'>";
        return content;
    }


    private static String USER_NAME = "541086428@qq.com";  // user name (just the part before "@gmail.com")
    private static String PASSWORD = "vlnpwntdccicbegc"; // password
    private static String RECIPIENT = "15571169379@163.com";


    private static void sendFromGMail(String to, String subject, String content, InputStream is) {
        Properties props = System.getProperties();
        String from = "541086428@qq.com";  // user name (just the part before "@gmail.com")
        String pass = "vlnpwntdccicbegc"; // password
        String host = "smtp.qq.com";//"smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");


        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            /**
             * 4，设置标题
             */
            message.setSubject(subject, "UTF-8");
            message.setText(content);
            /*添加正文内容*/
            Multipart multipart = new MimeMultipart();
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setText(content);

            contentPart.setHeader("Content-Type", "text/html; charset=UTF-8");
            multipart.addBodyPart(contentPart);

            /*添加附件*/
            MimeBodyPart fileBody = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(is, "application/msexcel");
            fileBody.setDataHandler(new DataHandler(source));

            String fileName = "主播收入明細表.xls";
            // 中文乱码问题
            fileBody.setFileName(MimeUtility.encodeText(fileName));
            multipart.addBodyPart(fileBody);

            message.setContent(multipart);
            message.setSentDate(new Date());


            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (AddressException ae) {
            ae.printStackTrace();
        } catch (MessagingException me) {
            me.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
