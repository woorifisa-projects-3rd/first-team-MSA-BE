package com.example.User.service;


import com.example.User.error.CustomException;
import com.example.User.error.ErrorCode;
import com.example.User.util.CryptoUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.smtp.SMTPAddressFailedException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final CryptoUtil cryptoUtil;
    public String sendPinNumberToEmail(String email){
        String title ="[집계사장]Email 확인 용 PinNumber";
        String pinNumber=cryptoUtil.makeRandomInteger();
        String content =
                "집계사장을 사용해주셔서 감사합니다. 🦀🍔🍟" +
                        "<br><br> " +
                        "인증 번호는 " + pinNumber + "입니다." +
                        "<br> "; // 이메일 내용
        mailSend(email, title, content);
        return pinNumber;
    }

    public String sendURLToEmail(String email, Integer storeId,String encryptedEmail) {  // 리턴 타입을 void로 변경
//        String ip="localhost";
        String deployIp= "https://jg-sajang.vercel.app";
        String url = String.format("%s/employee/%d/commute/%s",deployIp, storeId, encryptedEmail);
        String title ="[집계사장]직원 URL";
        mailSend(email, title, createHTML(url));
        return url;
    }

    // mail 양식 설정
    public String temporaryPasswordEmail(String email) {
        String authPassword = cryptoUtil.makeRandomPassword();
        String title = "[집계사장] 임시 비밀번호를 보내드립니다."; // 이메일 제목
        String content =
                "집계사장을 사용해주셔서 감사합니다. 🦀🍔🍟" +
                        "<br><br> " +
                        "임시 비밀번호는 " + authPassword + "입니다." +
                        "<br> " +
                        "보안을 위해 로그인 후에는 꼭 비밀번호를 변경해주세요!"; // 이메일 내용
        mailSend(email, title, content);
        return authPassword;
    }

    private void mailSend(String toMail, String title, String content) {
        MimeMessage message = javaMailSender.createMimeMessage(); // MimeMessage 객체 생성
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setTo(toMail); // 이메일 수신자 주소 설정
            helper.setSubject(title); // 이메일 주소 설정
            helper.setText(content, true); // 이메일의 내용
            javaMailSender.send(message);
        } catch (MailSendException sme)
        {
            log.error("존재하지 않는 이메일 : {}" ,toMail);
            throw new CustomException(ErrorCode.NOT_EXISTS_EMAIL);
        } catch (SendFailedException se)
        {
            log.error("메일 전송 실패: {}", se.getMessage());
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        } catch (MessagingException e) {
            log.error("이메일 전송 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private String createHTML(String url) {
        return """
            <div style='max-width: 600px; margin: 0 auto; padding: 20px; font-family: Arial, sans-serif;'>
                <div style='background-color: #0068FF; padding: 20px; text-align: center;'>
                    <h1 style='color: white; margin: 0;'>집계사장</h1>
                </div>
                <div style='background-color: #ffffff; padding: 40px 20px; text-align: center; border: 1px solid #e9e9e9;'>
                    <h2 style='color: #333333; margin-bottom: 30px;'>직원 등록 링크</h2>
                    <p style='color: #666666; font-size: 16px; line-height: 24px;'>
                        안녕하세요.<br>출퇴근을 위한 링크를 보내드립니다.
                    </p>
                    <div style='background-color: #f8f9fa; padding: 15px; margin: 30px auto; max-width: 500px; border-radius: 4px;'>
                        <p style='word-break: break-all; color: #666666;'>%s</p>
                    </div>
                    <a href='%s' style='display: inline-block; background-color: #0068FF; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; margin-top: 20px; font-weight: bold;'>
                        출퇴근 링크
                    </a>
                </div>
                <div style='text-align: center; padding: 20px; color: #999999; font-size: 12px;'>
                    <p>본 메일은 발신전용 메일입니다.</p>
                    <p>&copy; 2024 CoreBank. All rights reserved.</p>
                </div>
            </div>
            """.formatted(url, url);  // url을 두 곳에 삽입
    }
}