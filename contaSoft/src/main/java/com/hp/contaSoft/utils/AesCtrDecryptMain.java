package com.hp.contaSoft.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AesCtrDecryptMain {

    private static final int IV_LENGTH_BYTES = 16;

    private AesCtrDecryptMain() {
    }

    public static String decrypt(String base64Payload, String phrase) throws Exception {
        if (base64Payload == null || base64Payload.trim().isEmpty()) {
            throw new IllegalArgumentException("base64Payload is required");
        }
        if (phrase == null || phrase.isEmpty()) {
            throw new IllegalArgumentException("phrase is required");
        }

        byte[] payload = Base64.getDecoder().decode(base64Payload.trim());
        if (payload.length <= IV_LENGTH_BYTES) {
            throw new IllegalArgumentException("Payload must contain IV (16 bytes) + ciphertext");
        }

        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        byte[] ciphertext = Arrays.copyOfRange(payload, IV_LENGTH_BYTES, payload.length);

        SecretKeySpec keySpec = new SecretKeySpec(phrase.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext, StandardCharsets.UTF_8);
    }

    private static byte[] deriveAes256Key(String phrase) throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        return sha256.digest(phrase.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        String base64Payload = "T4izd0PTNxdbGguhRwV6RDXZNiqudsiS4zgmGxFcTcKBLPFOQVwQF7AfsbApxhMdGOEHXgXuwVOb2IhY+bq7KgxYIsLpt6cjf3rwZnsdhBaFrXsgt1E4dxLCRXISrBozfoRjo66z+qMm9W1t+g==";
        String phrase = "0123456789abcdef0123456789abcdef";

        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            Key key = new SecretKeySpec(phrase.getBytes(), "AES");

            Cipher decCipher = Cipher.getInstance("AES/CTR/NoPadding");
            decCipher.init(Cipher.DECRYPT_MODE, key, ivspec);

            System.out.println(new String(decCipher.doFinal(Base64.getDecoder().decode(base64Payload))));
        } catch (Exception ex) {
            System.err.println("Decrypt error: " + ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(2);
        }
    }
}
