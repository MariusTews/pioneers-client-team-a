package com.aviumauctores.pioneers.service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.security.Key;
import java.util.Base64;

public class CryptoService {
    private final Cipher cipher;
    private final Key secretKey;

    @Inject
    public CryptoService() {
        try {
            cipher = Cipher.getInstance("AES");
            String key = "fgd5j0kkBar1A4j5"; // 128 bit key
            secretKey = new SecretKeySpec(key.getBytes(), "AES");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String encode(String data) throws Exception {
        byte[] dataByte = data.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encodedByte = cipher.doFinal(dataByte);
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(encodedByte);
    }

    public String decode(String data) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] dataByte = decoder.decode(data);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedByte = cipher.doFinal(dataByte);
        return new String(decodedByte);
    }
}
