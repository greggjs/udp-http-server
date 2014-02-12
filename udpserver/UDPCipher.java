/**
 * Name: Jake Gregg
 * Instructor: Dr. Scott Campbell
 * Class: CSE 617
 * Date: Feb 11, 2014
 * Filename: UDPCipher.java
 * Description: A class that contains all of our encryption tools
 * needed for encrypting and decrypting with the AES/CBC/PKCS5Padding
 * format. 
 */

package udpserver;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.io.IOException;

public class UDPCipher {
    private Cipher cipher; // Cipher object for ENC/DENC
    private SecretKeySpec skeySpec; // Secret Key
    private IvParameterSpec ivspec; // Initialization Vec

    /**
     *  Constructor that creates a new Cipher with a given key. It uses the 
     *  key also for the initialization vector. Throws all exceptions to the 
     *  caller.
     */
    public UDPCipher(String key) throws GeneralSecurityException, IOException {
        String iv = key; // the initializtion vector is the key
        this.skeySpec = new SecretKeySpec(Base64.decode(key), "AES");
        this.ivspec = new IvParameterSpec(Base64.decode(iv));
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    }

    /**
     *  Encrypts a string into a byte array.
     */
    public byte[] encrypt(String msg) throws GeneralSecurityException {
        // initialize the cipher to encrypt
        this.cipher.init(Cipher.ENCRYPT_MODE, this.skeySpec, this.ivspec);
        byte[] msg_enc = cipher.doFinal(msg.getBytes());
        return msg_enc;
    }

    /**
     *  Decrypts a byte array into a readable string
     */
    public String decrypt(byte[] enc) throws GeneralSecurityException {
        // initialize the cipher to decrypt
        this.cipher.init(Cipher.DECRYPT_MODE, this.skeySpec, this.ivspec);
        byte[] msg = cipher.doFinal(enc);
        return new String(msg);
    }
}
