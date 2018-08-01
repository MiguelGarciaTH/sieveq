/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.crypto;

import core.management.CoreConfiguration;
import core.management.CoreProperties;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.Arrays;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author miguel
 */
public class KeyStorage {

    private String MESSAGE_DIGEST;
    private String SECRET_KEY_SPEC;
    private String path;
    private CoreProperties prop;

    private PrivateKey priKey;

    private void setup() {
        switch (prop.algorithm) {
            case "SHA-1":
                MESSAGE_DIGEST = "SHA1";
                SECRET_KEY_SPEC = "HmacSHA1";
                break;
            case "SHA-256":
                MESSAGE_DIGEST = "SHA-256";
                SECRET_KEY_SPEC = "HmacSHA256";
                break;
            default:
                System.out.println(prop.algorithm + " not implemented yet");
                MESSAGE_DIGEST = "SHA-256";
                SECRET_KEY_SPEC = "HmacSHA256";
                System.out.println("Setup SHA256 by default");
        }
    }

    public KeyStorage(String path, String algorithm) {
        this.path = path;
        setup();
    }

    public KeyStorage() {
        setup();
    }

    public SecretKeySpec generateSecretKeyFromFile(String path) {
        SecretKeySpec secretKeySpec = null;
        try {
            byte[] key = loadSecretKey(path);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, prop.hmac_key_size); // use only first 128 bit (16)
            secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        } catch (Exception ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "generateSecretKeyFromFile()", ex.getMessage());
        }
        return secretKeySpec;
    }

    private byte[] loadSecretKey(String path) {
        FileInputStream fis = null;
        try {
            File filePublicKey = new File(path);
            fis = new FileInputStream(path);
            byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
            fis.read(encodedPublicKey);
            fis.close();
            return encodedPublicKey;
        } catch (Exception ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "loadSecretKey()", ex.getMessage());
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "loadSecretKey()2", ex.getMessage());
            }
        }
        return null;
    }

    public PublicKey loadPublicKey(String publickey_file) {
        FileInputStream fis = null;
        try {
            File filePublicKey = new File(publickey_file);
            fis = new FileInputStream(publickey_file);
            DataInputStream dis = new DataInputStream(fis);
            byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
            dis.readFully(encodedPublicKey);
            dis.close();
            fis.close();
            
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec ks = new X509EncodedKeySpec(encodedPublicKey);
            
            PublicKey pubKey = (PublicKey) keyFactory.generatePublic(ks);
            return pubKey;
        } catch (Exception ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "loadPublicKey()", ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "loadPublicKey()2", ex.getMessage());
            }
        }
        return null;
    }

    public PrivateKey loadPrivateKey(String privatekey_file) {
        FileInputStream fis = null;
        try {
            File filePrivateKey = new File(privatekey_file);
            fis = new FileInputStream(privatekey_file);
            byte[] encodedPublicKey;
            try (DataInputStream dis = new DataInputStream(fis)) {
                encodedPublicKey = new byte[(int) filePrivateKey.length()];
                dis.readFully(encodedPublicKey);
            }
            fis.close();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(encodedPublicKey);
            PrivateKey privKey = (PrivateKey) keyFactory.generatePrivate(ks);
            return privKey;
        } catch (Exception ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "loadPrivateKey()", ex.getMessage());
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "loadPrivateKey()2", ex.getMessage());
            }
        }
        return null;
    }
       public PublicKey readPublicKeyFromFile(String fileName) throws IOException{  
        FileInputStream fis = null;  
        ObjectInputStream ois = null;  
        try {  
            fis = new FileInputStream(new File(fileName));  
            ois = new ObjectInputStream(fis);  
              
            BigInteger modulus = (BigInteger) ois.readObject();  
            BigInteger exponent = (BigInteger) ois.readObject();  
              
            //Get Public Key  
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);  
            KeyFactory fact = KeyFactory.getInstance("RSA");  
            PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);  
                          
            return publicKey;  
              
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        finally{  
            if(ois != null){  
                ois.close();  
                if(fis != null){  
                    fis.close();  
                }  
            }  
        }  
        return null;  
    }  
      
    /** 
     * read Public Key From File 
     * @param fileName 
     * @return 
     * @throws IOException 
     */  
    public PrivateKey readPrivateKeyFromFile(String fileName) throws IOException{  
        FileInputStream fis = null;  
        ObjectInputStream ois = null;  
        try {  
            fis = new FileInputStream(new File(fileName));  
            ois = new ObjectInputStream(fis);  
              
            BigInteger modulus = (BigInteger) ois.readObject();  
            BigInteger exponent = (BigInteger) ois.readObject();  
              
            //Get Private Key  
            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, exponent);  
            KeyFactory fact = KeyFactory.getInstance("RSA");  
            PrivateKey privateKey = fact.generatePrivate(rsaPrivateKeySpec);  
                          
            return privateKey;  
              
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        finally{  
            if(ois != null){  
                ois.close();  
                if(fis != null){  
                    fis.close();  
                }  
            }  
        }  
        return null;  
    }  
}
