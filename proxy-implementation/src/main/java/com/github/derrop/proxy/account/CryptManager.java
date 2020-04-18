/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.account;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class CryptManager {

    /**
     * Generate a new shared secret AES key from a secure random source
     */
    public static SecretKey createNewSharedKey() {
        try {
            KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
            keygenerator.init(128);
            return keygenerator.generateKey();
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            throw new Error(nosuchalgorithmexception);
        }
    }

    /**
     * Generates RSA KeyPair
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA");
            keypairgenerator.initialize(1024);
            return keypairgenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
            return null;
        }
    }

    /**
     * Compute a serverId hash for use by sendSessionRequest()
     */
    public static byte[] getServerIdHash(String serverId, PublicKey publicKey, SecretKey secretKey) {
        try {
            return digestOperation("SHA-1", serverId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
        } catch (UnsupportedEncodingException unsupportedencodingexception) {
            unsupportedencodingexception.printStackTrace();
            return null;
        }
    }

    /**
     * Compute a message digest on arbitrary byte[] data
     */
    private static byte[] digestOperation(String algorithm, byte[]... data) {
        try {
            MessageDigest messagedigest = MessageDigest.getInstance(algorithm);

            for (byte[] abyte : data) {
                messagedigest.update(abyte);
            }

            return messagedigest.digest();
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
            return null;
        }
    }

    /**
     * Create a new PublicKey from encoded X.509 data
     */
    public static PublicKey decodePublicKey(byte[] encodedKey) {
        try {
            EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(encodedKey);
            KeyFactory keyfactory = KeyFactory.getInstance("RSA");
            return keyfactory.generatePublic(encodedkeyspec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException var3) {
        }

        return null;
    }

    /**
     * Decrypt shared secret AES key using RSA private key
     */
    public static SecretKey decryptSharedKey(PrivateKey key, byte[] secretKeyEncrypted) {
        return new SecretKeySpec(decryptData(key, secretKeyEncrypted), "AES");
    }

    /**
     * Encrypt byte[] data with RSA public key
     */
    public static byte[] encryptData(Key key, byte[] data) {
        return cipherOperation(1, key, data);
    }

    /**
     * Decrypt byte[] data with RSA private key
     */
    public static byte[] decryptData(Key key, byte[] data) {
        return cipherOperation(2, key, data);
    }

    /**
     * Encrypt or decrypt byte[] data using the specified key
     */
    private static byte[] cipherOperation(int opMode, Key key, byte[] data) {
        try {
            return createTheCipherInstance(opMode, key.getAlgorithm(), key).doFinal(data);
        } catch (IllegalBlockSizeException illegalblocksizeexception) {
            illegalblocksizeexception.printStackTrace();
        } catch (BadPaddingException badpaddingexception) {
            badpaddingexception.printStackTrace();
        }

        return null;
    }

    /**
     * Creates the Cipher Instance.
     */
    private static Cipher createTheCipherInstance(int opMode, String transformation, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(opMode, key);
            return cipher;
        } catch (InvalidKeyException | NoSuchAlgorithmException invalidkeyexception) {
            invalidkeyexception.printStackTrace();
        } catch (NoSuchPaddingException nosuchpaddingexception) {
            nosuchpaddingexception.printStackTrace();
        }

        return null;
    }
}
