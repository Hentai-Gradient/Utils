package cn.xma.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtil {
    private static Logger logger = Logger.getLogger(RSAUtil.class);

    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    public static PrivateKey getPrivateKey(String key) throws Exception {
        try {
            byte[] keyBytes = StringUtil.hexStrToBytes(key.trim());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSAUtil");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            String info = "getPrivateKey failed: " + key + " | " + e.getMessage();
            logger.error(info, e);
            throw new Exception(info);
        }
    }

    public static PublicKey getPublicKey(String key) throws Exception {
        try {
            byte[] keyBytes = StringUtil.hexStrToBytes(key.trim());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSAUtil");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            String info = "getPublicKey failed: " + key + " | " + e.getMessage();
            logger.error(info, e);
            throw new Exception(info);
        }
    }

    public static String sign(PrivateKey privateKey, String src, String encode) throws Exception {
        try {
            Signature sigEng = Signature.getInstance(SIGN_ALGORITHMS);
            sigEng.initSign(privateKey);
            sigEng.update(src.getBytes(encode));
            byte[] signature = sigEng.sign();
            return StringUtil.bytesToHexStr(signature);
        } catch (Exception e) {
            String info = "sign failed: " + src + " | " + e.getMessage();
            logger.error(info, e);
            throw new Exception(info);
        }
    }

    public static void verify(PublicKey publicKey, String sign, String src, String encode) throws Exception {
        try {
            if (StringUtils.isBlank(sign) || StringUtils.isBlank(src)) {
                throw new Exception("sign or src isBlank");
            }
            Signature sigEng = Signature.getInstance("SHA1withRSA");
            sigEng.initVerify(publicKey);
            sigEng.update(src.getBytes(encode));
            byte[] sign1 = StringUtil.hexStrToBytes(sign);
            if (!sigEng.verify(sign1)) {
                throw new Exception("VERIFY_SIGNATURE_FAIL");
            }
        } catch (Exception e) {
            String info = "verify failed: " + sign + " | " + src + " | " + e.getMessage();
            logger.error(info, e);
            throw new Exception(info);
        }
    }

    private static String[] genRSAKeyPair() {
        KeyPairGenerator rsaKeyGen = null;
        KeyPair rsaKeyPair = null;
        try {
            logger.error("Generating a pair of RSAUtil key ... ");
            rsaKeyGen = KeyPairGenerator.getInstance("RSAUtil");
            SecureRandom random = new SecureRandom();
            random.setSeed(("" + System.currentTimeMillis() * Math.random() * Math.random()).getBytes(Charset
                    .forName("UTF-8")));
            rsaKeyGen.initialize(1024, random);
            rsaKeyPair = rsaKeyGen.genKeyPair();
            PublicKey rsaPublic = rsaKeyPair.getPublic();
            PrivateKey rsaPrivate = rsaKeyPair.getPrivate();

            String[] privateAndPublic = new String[2];
            privateAndPublic[0] = StringUtil.bytesToHexStr(rsaPrivate.getEncoded());
            privateAndPublic[1] = StringUtil.bytesToHexStr(rsaPublic.getEncoded());
            logger.error("private key:" + privateAndPublic[0]);
            logger.error("public key:" + privateAndPublic[1]);
            logger.error("1024-bit RSAUtil key GENERATED.");

            return privateAndPublic;
        } catch (Exception e) {
            logger.error("genRSAKeyPair error" + e.getMessage(), e);
            return null;
        }
    }
}