package irie.services;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class TransformEngine {

    private SecretKey deriveKey(byte[] secret_key) throws NoSuchAlgorithmException {
        // Use a more secure hash function like SHA-256 for key derivation
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] hash = sha.digest(secret_key);

        // 3DES requires a 192-bit (24-byte) key. SHA-256 gives us 32 bytes.
        byte[] keyBytes = Arrays.copyOf(hash, 24);

        // Ensure parity bits are set correctly for 3DES (if necessary)
        adjustParityBits(keyBytes);

        return new SecretKeySpec(keyBytes, "DESede");
    }

    private void adjustParityBits(byte[] keyBytes) {
        for (int i = 0; i < keyBytes.length; i++) {
            byte b = keyBytes[i];
            keyBytes[i] = (byte) ((b & 0xFE) | ((Integer.bitCount(b & 0xFF) & 1) == 0 ? 1 : 0));
        }
    }

    private Cipher initCipher(int mode, byte[] secret_key) throws Exception {
        SecretKey key = deriveKey(secret_key);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[8]);
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(mode, key, ivParameterSpec);
        return cipher;
    }

    // Function to encrypt plain text to cipher text of data type byte
    public byte[] encryptMessage(byte[] secret_key, String plaintext) throws Exception {
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secret_key);
        final byte[] plain_text_bytes = plaintext.getBytes(StandardCharsets.UTF_8);

        return cipher.doFinal(plain_text_bytes);
    }

    // Function to decrypt cipher text to original plaintext of data type String

    public String decryptMessage(byte[] secret_key, byte[] ciphered_text) throws Exception {
        Cipher decipher = initCipher(Cipher.DECRYPT_MODE, secret_key);
        final byte[] plain_text = decipher.doFinal(ciphered_text);

        return new String(plain_text, StandardCharsets.UTF_8);
    }
}
