package com.culiu.core.utils.encrypt;

public class EncryptDecryptUtils {

    private static DESUtils desEncrypt = new DESUtils();

    /**
     * 加密String字符串
     *
     * @param encryptString
     */
    public static String encryptString(String encryptString) {
        String encryptResult = null;
        try {
            encryptResult = desEncrypt.encryptString(encryptString);
        } catch (Exception e) {
//            DebugLog.e("encrypt-->" + encryptString + ", failed.\n" + e.getMessage());
            encryptResult = encryptString;
        }
        return encryptResult;
    }

    /**
     * 解密String字符串
     *
     * @param decrypttString
     */
    public static String decryptString(String decrypttString) {
        String decryptResult = null;
        try {
            decryptResult = desEncrypt.decryptString(decrypttString);
        } catch (Exception e) {
//            DebugLog.e("decrypt-->" + decrypttString + ", failed.\n" + e.getMessage());
            decryptResult = decrypttString;
        }
        return decryptResult;
    }

}