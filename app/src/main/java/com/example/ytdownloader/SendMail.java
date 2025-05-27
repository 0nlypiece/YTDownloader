package com.example.ytdownloader;

import android.content.Context;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
    Context context;

    public SendMail(Context context) {
        this.context = context;
    }

    public void sendEmail(String recipientEmail,String mesaj) {
        // Gmail bilgileri - BUNLARI KENDİ BİLGİLERİNİZLE DEĞİŞTİRİN
        final String username = "thatstest0001@gmail.com"; // Gönderici Gmail adresi
        final String password = "mazkqbzrjpltzkod"; // Uygulama şifresi veya Gmail şifresi

        // SMTP ayarları
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // TLS kullan



        try {

            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // E-posta mesajını hazırla
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Kodunuz Hazır!");

            String htmlMessage = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 8px;'>" +
                    "<h2 style='color: #3F51B5;'>YT Downloader</h2>" +
                    "<p>Merhaba,</p>" +
                    "<p>İstediğiniz doğrulama kodu aşağıda yer almaktadır:</p>" +
                    "<div style='background-color: #e8eaf6; padding: 16px; border-radius: 6px; text-align: center; font-size: 24px; font-weight: bold; color: #303f9f;'>" +
                    mesaj +
                    "</div>" +
                    "<p>Bu kodu uygulamada ilgili alana girerek işleminizi tamamlayabilirsiniz.</p>" +
                    "<hr>" +
                    "<p style='font-size: 12px; color: #888888;'>Eğer bu isteği siz yapmadıysanız, lütfen dikkate almayın.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            message.setContent(htmlMessage, "text/html; charset=utf-8");
            // E-postayı gönder
            Transport.send(message);
            System.out.println("E-posta başarıyla gönderildi!");

        } catch (AuthenticationFailedException e) {
            System.out.println("Kimlik doğrulama başarısız: Kullanıcı adı/şifre hatalı");
            e.printStackTrace();
        } catch (MessagingException e) {
            System.out.println("E-posta gönderim hatası:");
            Toast.makeText(context, "Hatalı Eposta Girişi", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Beklenmeyen hata:");
            e.printStackTrace();
        }
    }

    public boolean isValidGmail(String email) {
        String gmailPattern = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        return email.matches(gmailPattern);
    }
}