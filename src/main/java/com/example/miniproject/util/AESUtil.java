package com.example.miniproject.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Component
@Slf4j
public class AESUtil {

    private static SecretKeySpec secretKeySpec = null;

    @Autowired
    public AESUtil(@Value("${AES.secretKey}") String rawKey) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] key = Arrays.copyOf(
                    sha.digest(rawKey.getBytes(StandardCharsets.UTF_8)), 24
            );
            secretKeySpec = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("secretKey 길이가 부족합니다.");
        }
    }

    public static String encrypt(String str) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return encodeBase64(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error("Error while decrypt: " + e);
            return null;
        }
    }

    public static String decrypt(String str) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(decodeBase64(str)));
        } catch (Exception e) {
            log.error("Error while decrypt: " + e);
            return null;
        }
    }

    private static String encodeBase64(byte[] source) {
        return Base64.getEncoder().encodeToString(source);
    }

    private static byte[] decodeBase64(String encodedString) {
        return Base64.getDecoder().decode(encodedString);
    }

}
