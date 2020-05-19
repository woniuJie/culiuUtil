package com.culiu.core.utils.encrypt;


import com.culiu.core.utils.debug.DebugLog;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * 新的3DES加密方法（兼容IOS）
 */
public class ThreeDES {

    private static final String TAG = "IM_CHAT";

    private String key = "";// 密钥,默认为公钥,验证通过后生成私钥

    private boolean isPrivate = false;// 是否是私钥加密

    /**
     * 设置私钥
     *
     * @param key
     */
    public void setPrivateKey(String key) {
        this.key = key;
        isPrivate = true;
    }

    /**
     * 设置公钥
     *
     * @param publicKey
     */
    public void setPublicKey(String publicKey) {
        this.key = publicKey;
        isPrivate = false;
    }

    /**
     * 当前加密方式
     *
     * @return
     */
    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     * 加密
     *
     * @param plainByte 明文
     *
     * @return 密文
     */
    public byte[] encrypt(byte[] plainByte) {
        DebugLog.d(TAG, "three des encrypt key:" + key);
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
     *
     * @return 明文
     */
    public byte[] decrypt(byte[] cipherByte) {
        DebugLog.d(TAG, "three des decrypt key:" + key);
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

        // BASE64Encoder encoder = new BASE64Encoder();
        // return encoder.encode(b).replaceAll("\r", "").replaceAll("\n", "");

        return Base64.encodeBytes(b).replaceAll("\r", "").replaceAll("\n", "");
    }

    public String decryptString(String sCipherText) throws Exception {
        // --通过base64,将字符串转成byte数组
        // BASE64Decoder decoder = new BASE64Decoder();
        // byte[] bytesrc = decoder.decodeBuffer(src);
        byte[] bytesrc = Base64.decode(sCipherText);

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

    public String getKey() {
        return key;
    }

}
