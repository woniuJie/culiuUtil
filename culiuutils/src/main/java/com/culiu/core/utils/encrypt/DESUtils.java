package com.culiu.core.utils.encrypt;


import android.util.Base64;

import com.culiu.core.utils.debug.DebugLog;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class DESUtils {
    private String key = "cuW1L@2U!*7GN0X%A3#E@liu";// 密钥,默认为公钥,验证通过后生成私钥
    private boolean isPrivate = false;// 是否是私钥加密

    public void setPrivateKey(String key) {
        this.key = key;
        isPrivate = true;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     * 加密
     *
     * @param plainByte 明文
     * @return 密文
     */
    public byte[] encrypt(byte[] plainByte) {

        byte[] cipherByte = null;
        try {
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory
                    .getInstance("DESede");
            SecretKey securekey = keyFactory.generateSecret(dks);

            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, securekey);
            cipherByte = cipher.doFinal(plainByte);
        } catch (Exception e) {
            DebugLog.e(e.getMessage());
        }
        return cipherByte;
    }

    /**
     * 解密
     *
     * @param cipherByte 密文
     * @return 明文
     */
    public byte[] decrypt(byte[] cipherByte) {
        byte[] decryptByte = null;
        try {
            // --解密的key
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory
                    .getInstance("DESede");
            SecretKey securekey = keyFactory.generateSecret(dks);

            // --Chipher对象解密
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, securekey);
            decryptByte = cipher.doFinal(cipherByte);
        } catch (Exception e) {
            DebugLog.e(e.getMessage());
        }

        return decryptByte;
    }

    public String encryptString(String sPlainText) throws Exception {
        DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey securekey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, securekey);
        byte[] b = cipher.doFinal(sPlainText.getBytes());
        return Base64.encodeToString(b, Base64.DEFAULT).replaceAll("\r", "").replaceAll("\n", "");
    }

    public String decryptString(String sCipherText) throws Exception {
        // --通过base64,将字符串转成byte数组
        byte[] bytesrc = Base64.decode(sCipherText, Base64.DEFAULT);

        // --解密的key
        DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey securekey = keyFactory.generateSecret(dks);

        // --Chipher对象解密
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, securekey);
        byte[] retByte = cipher.doFinal(bytesrc);

        return new String(retByte);
    }
}