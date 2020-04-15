package net.md_5.bungee;

import com.github.derrop.proxy.protocol.login.client.PacketLoginInEncryptionRequest;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutEncryptionResponse;
import lombok.Getter;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

/**
 * Class containing all encryption related methods for the proxy.
 */
public class EncryptionUtil {

    private static final Random random = new Random();
    public static final KeyPair keys;
    @Getter
    private static final SecretKey secret = new SecretKeySpec(new byte[16], "AES");

    static {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            keys = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static PacketLoginInEncryptionRequest encryptRequest() {
        String hash = Long.toString(random.nextLong(), 16);
        byte[] pubKey = keys.getPublic().getEncoded();
        byte[] verify = new byte[4];
        random.nextBytes(verify);
        return new PacketLoginInEncryptionRequest(hash, pubKey, verify);
    }

    public static SecretKey getSecret(PacketLoginOutEncryptionResponse resp, PacketLoginInEncryptionRequest request) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, keys.getPrivate());
        byte[] decrypted = cipher.doFinal(resp.getVerifyToken());

        if (!Arrays.equals(request.getVerifyToken(), decrypted)) {
            throw new IllegalStateException("Key pairs do not match!");
        }

        cipher.init(Cipher.DECRYPT_MODE, keys.getPrivate());
        return new SecretKeySpec(cipher.doFinal(resp.getSharedSecret()), "AES");
    }
}
