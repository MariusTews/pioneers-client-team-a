package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.controller.LoginController;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.inject.Inject;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Base64;

public class CryptoService {
    private Cipher cipher;
    private Key secretKey;

    @Inject
    public CryptoService(){

    }

    public String encode(String data) throws Exception {
        byte[] dataByte = data.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(dataByte);
        Base64.Encoder encoder = Base64.getEncoder();;
        String encodedData = encoder.encodeToString(encryptedByte);
        return encodedData;
    }

    public String decode(String data){
        return data;
    }
}
