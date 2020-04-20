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
package com.github.derrop.proxy.network.encryption;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.protocol.login.client.PacketLoginInEncryptionRequest;
import com.github.derrop.proxy.protocol.login.server.PacketLoginOutEncryptionResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public final class ServerEncryptionUtils {

    private ServerEncryptionUtils() {
        throw new UnsupportedOperationException();
    }

    public static final KeyPair KEY_PAIR;

    static {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KEY_PAIR = keyPairGenerator.generateKeyPair();
        } catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    @NotNull
    public static PacketLoginInEncryptionRequest getLoginEncryptionRequestPacket() {
        String hash = Long.toString(Constants.RANDOM.nextLong(), 16);
        byte[] publicKey = KEY_PAIR.getPublic().getEncoded();

        byte[] verifyBytes = new byte[4];
        Constants.RANDOM.nextBytes(verifyBytes);

        return new PacketLoginInEncryptionRequest(hash, publicKey, verifyBytes);
    }

    @Nullable
    public static SecretKey getSecretKey(
            @NotNull PacketLoginOutEncryptionResponse packetLoginOutEncryptionResponse,
            @NotNull PacketLoginInEncryptionRequest packetLoginInEncryptionRequest
    ) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, KEY_PAIR.getPrivate());

        byte[] decrypted = cipher.doFinal(packetLoginOutEncryptionResponse.getVerifyToken());
        if (!Arrays.equals(packetLoginInEncryptionRequest.getVerifyToken(), decrypted)) {
            return null;
        }

        cipher.init(Cipher.DECRYPT_MODE, KEY_PAIR.getPrivate());
        return new SecretKeySpec(
                cipher.doFinal(packetLoginOutEncryptionResponse.getSharedSecret()),
                "AES"
        );
    }
}
