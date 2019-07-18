package irie.controllers;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

public class TransformEngine {

    // Function to encrypt plain text to cipher text of data type byte
    public byte[] encryptMessage(String secret_key, String plaintext) throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("md5");
        final byte[] digest = messageDigest.digest(secret_key.getBytes("utf-8"));
        final byte[] key_bytes = Arrays.copyOf(digest, 24);

        for(int j=0, k=16; j < 8;){
            key_bytes[k++] = key_bytes[j++];
        }

        final SecretKey key = new SecretKeySpec(key_bytes, "DESede");
        final IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

        final byte[] plain_text_bytes = plaintext.getBytes("utf-8");
        final byte[] cipher_text = cipher.doFinal(plain_text_bytes);

        return cipher_text;
    }

    // Function to decrypt cipher text to original plaintext of data type String

    public String decryptMessage(String secret_key, byte[] ciphered_text) throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("md5");
        final byte[] digest = messageDigest.digest(secret_key.getBytes("utf-8"));
        final byte[] key_bytes = Arrays.copyOf(digest, 24);

        for(int j=0, k=16; j < 8;){
            key_bytes[k++] = key_bytes[j++];
        }

        final SecretKey key = new SecretKeySpec(key_bytes, "DESede");
        final IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[8]);
        final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        decipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

        final byte[] plain_text = decipher.doFinal(ciphered_text);


        return new String(plain_text, "UTF-8");
    }
}
