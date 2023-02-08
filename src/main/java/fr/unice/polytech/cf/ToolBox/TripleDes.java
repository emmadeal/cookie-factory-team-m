package fr.unice.polytech.cf.ToolBox;

import lombok.Getter;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Getter
public class TripleDes {

    public static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private final KeySpec ks;
    private final SecretKeyFactory skf;
    private final Cipher cipher;
    private final byte[] arrayBytes;
    private final String myEncryptionKey;
    private final String myEncryptionScheme;
    SecretKey key;

    public TripleDes() {
        this.myEncryptionKey = "CoOkIe_FaCtOrY_kHaD_eMmA_mOmO_cLaOIrE_jUlEs";
        this.myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        try {
            this.arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
            this.ks = new DESedeKeySpec(arrayBytes);
            this.skf = SecretKeyFactory.getInstance(myEncryptionScheme);
            this.cipher = Cipher.getInstance(myEncryptionScheme);
            this.key = skf.generateSecret(ks);
        } catch (UnsupportedEncodingException | NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeySpecException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    public String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            this.getCipher().init(Cipher.ENCRYPT_MODE, this.getKey());
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = this.getCipher().doFinal(plainText);
            encryptedString = new String(Base64.getEncoder().encode(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }


    public String decrypt(String encryptedString) {
        String decryptedText = null;
        try {
            this.getCipher().init(Cipher.DECRYPT_MODE, this.getKey());
            byte[] encryptedText = Base64.getDecoder().decode(encryptedString);
            byte[] plainText = this.getCipher().doFinal(encryptedText);
            decryptedText = new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

}
