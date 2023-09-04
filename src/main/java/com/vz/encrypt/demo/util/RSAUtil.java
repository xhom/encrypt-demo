package com.vz.encrypt.demo.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author visy.wang
 * @description: RSA加密解密工具
 * @date 2023/7/25 9:52
 */
@Slf4j
public class RSAUtil {
    //算法
    private final static String ALGORITHM = "RSA";
    //密钥长度 于原文长度对应 以及越长速度越慢
    private final static int KEY_SIZE = 1024;

    /**
     * 随机生成密钥对
     */
    private static Map<String, String> genKeyPair() {
        Map<String,String> keyMap = new HashMap<>();

        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen;
        try{
            keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return keyMap;
        }

        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        // 得到私钥字符串
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        // 将公钥和私钥保存到Map
        keyMap.put("pubKey", publicKeyString);
        keyMap.put("priKey", privateKeyString);

        return keyMap;
    }

    /**
     * RSA公钥加密
     * @param data 加密字符串
     * @param publicKey 公钥
     * @return 密文(Base64)
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(byte[] data, String publicKey) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        PublicKey pubKey = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data));
    }

    public static String encryptByPriKey(byte[] data, String privateKey) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        PrivateKey priKey = KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, priKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data));
    }

    /**
     * RSA私钥解密
     * @param data 加密字符串(Base64)
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static byte[] decrypt(byte[] data, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(data);
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return cipher.doFinal(inputByte);
    }


    /*
     * 前端加密说明：
     * 1.安装插件：
     *     npm i jsencrypt --save
     *
     * 2.导入插件：
     *     import JSEncrypt from 'jsencrypt'
     *
     * 3.采用公钥加密：
     *     var pubKey = ""; //公钥
     *
     *     var encryptor = new JSEncrypt();
     *     encryptor.setPublicKey(pubKey);
     *
     *     var cipher = encryptor.encrypt('ip+timestamp');
     *
     *     console.log("加密结果：", cipher);
     */

    public static void main(String[] args) throws Exception{
        String pubKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMBAtneQFX8bqpNqcb4hrjQZET9/HtKVsoWgVy+6ioeaUTfai5WthIvWI5bvJ0gcCwCMdl6/07OvA2geShnUBAcCAwEAAQ==";
        System.out.println(encrypt("{\"name\": \"zhangsan\",\"age\": 39}".getBytes(StandardCharsets.UTF_8), pubKey));
    }
}
