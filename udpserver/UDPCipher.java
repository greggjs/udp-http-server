package udpserver;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.io.IOException;

public class UDPCipher {
    private String key;
    private String iv;
    private Cipher cipher;
    private SecretKeySpec skeySpec;
    private IvParameterSpec ivspec;

    public UDPCipher(String key) {
        try {
            this.key = key;
            this.iv = key;
            this.skeySpec = new SecretKeySpec(Base64.decode(key), "AES");
            this.ivspec = new IvParameterSpec(Base64.decode(iv));
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (GeneralSecurityException err) {
            err.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public byte[] encrypt(String msg) throws GeneralSecurityException {
        this.cipher.init(Cipher.ENCRYPT_MODE, this.skeySpec, this.ivspec);
        byte[] msg_enc = cipher.doFinal(msg.getBytes());
        return msg_enc;
    }

    public String decrypt(byte[] enc) throws GeneralSecurityException {
        this.cipher.init(Cipher.DECRYPT_MODE, this.skeySpec, this.ivspec);
        byte[] msg = cipher.doFinal(enc);
        return new String(msg);
    }
}
