package com.sy.tool;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * DES是一种对称加密算法，所谓对称加密算法:加密和解密使用相同的秘钥的算法
 * @author llp
 *
 */
public class DESUtil {
    private static final Logger logger = LoggerFactory.getLogger(DESUtil.class);

    private static Key key;
    //设置秘钥key
    private static String KEY_STR="myKey";
    private static String CHARSETNAME="UTF-8";
    private static String ALGORITHM="DES";

    static{
        try{
            //生成DES算法对象
            KeyGenerator generator=KeyGenerator.getInstance(ALGORITHM);
            //运用SHA1安全策略
            SecureRandom secureRandom=SecureRandom.getInstance("SHA1PRNG");
            //设置上密钥种子
            secureRandom.setSeed(KEY_STR.getBytes());
            //初始化基于SHA1的算法对象
            generator.init(secureRandom);
            //生成密钥对象
            key=generator.generateKey();
            generator=null;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取加密的信息
     * @param str
     * @return
     */
    public static String getEncryptString(String str){
//        String originalString = "Hello World";
        return Base64.getEncoder().encodeToString(str.getBytes());
//        String decodedString = new String(Base64.getDecoder().decode(encodedString));
//        System.out.println("Encoded: " + encodedString);
//        System.out.println("Decoded: " + decodedString);
//        //基于BASE64编码，接收byte[]并转换成String
//        BASE64Encoder base64Encoder=new BASE64Encoder();
//        try {
//            // 按UTF8编码
//            byte[] bytes = str.getBytes(CHARSETNAME);
//            // 获取加密对象
//            Cipher cipher = Cipher.getInstance(ALGORITHM);
//            // 初始化密码信息
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//            // 加密
//            byte[] doFinal = cipher.doFinal(bytes);
//            // byte[]to encode好的String并返回
//            return base64Encoder.encode(doFinal);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     * 获取解密之后的信息
     *
     * @param str
     * @return
     */
    public static String getDecryptString(String str) {
//        String originalString = "Hello World";
//        String encodedString = Base64.getEncoder().encodeToString(originalString.getBytes());
        return new String(Base64.getDecoder().decode(str));
//        System.out.println("Encoded: " + encodedString);
//        System.out.println("Decoded: " + decodedString);
        // 基于BASE64编码，接收byte[]并转换成String
//        BASE64Decoder base64decoder = new BASE64Decoder();
//        try {
//            // 将字符串decode成byte[]
//            byte[] bytes = base64decoder.decodeBuffer(str);
//            // 获取解密对象
//            Cipher cipher = Cipher.getInstance(ALGORITHM);
//            // 初始化解密信息
//            cipher.init(Cipher.DECRYPT_MODE, key);
//            // 解密
//            byte[] doFinal = cipher.doFinal(bytes);
//            // 返回解密之后的信息
//            return new String(doFinal, CHARSETNAME);
//        } catch (Exception e) {
//           return null;
//        }finally {
//        }
    }

//    public static void main(String[] args) {
//        //加密
//        logger.info(getEncryptString("root"));//WnplV/ietfQ=
//        logger.info(getEncryptString("123456"));//QAHlVoUc49w=
//        //解密
//        logger.info(getDecryptString(getEncryptString("root")));//root
//        logger.info(getDecryptString(getEncryptString("123456")));//123456
//    }
}
