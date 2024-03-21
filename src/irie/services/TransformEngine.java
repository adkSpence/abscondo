package irie.services;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

public class TransformEngine {

    private Cipher initCipher(int mode, String secret_key) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("md5");
        byte[] digest = messageDigest.digest(secret_key.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = Arrays.copyOf(digest, 24);
        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }
        SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[8]);
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(mode, key, ivParameterSpec);
        return cipher;
    }

    // Function to encrypt plain text to cipher text of data type byte
    public byte[] encryptMessage(String secret_key, String plaintext) throws Exception {
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secret_key);
        final byte[] plain_text_bytes = plaintext.getBytes(StandardCharsets.UTF_8);

        return cipher.doFinal(plain_text_bytes);
    }

    // Function to decrypt cipher text to original plaintext of data type String

    public String decryptMessage(String secret_key, byte[] ciphered_text) throws Exception {
        Cipher decipher = initCipher(Cipher.DECRYPT_MODE, secret_key);
        final byte[] plain_text = decipher.doFinal(ciphered_text);

        return new String(plain_text, StandardCharsets.UTF_8);
    }
}
