/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package systems.kinau.fishingbot.network.utils;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import systems.kinau.fishingbot.FishingBot;
import systems.kinau.fishingbot.auth.AuthData;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//code borrowed from minecraft's encryptor
public class CryptManager {
    /**
     * ISO_8859_1
     */
    public static final Charset charSet = Charset.forName("ISO_8859_1");

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Generate a new shared secret AES key from a secure random source
     */
    public static SecretKey createNewSharedKey() {
        CipherKeyGenerator var0 = new CipherKeyGenerator();
        var0.init(new KeyGenerationParameters(new SecureRandom(), 128));
        return new SecretKeySpec(var0.generateKey(), "AES");
    }

    public static KeyPair createNewKeyPair() {
        try {
            KeyPairGenerator var0 = KeyPairGenerator.getInstance("RSA");
            var0.initialize(1024);
            return var0.generateKeyPair();
        } catch (NoSuchAlgorithmException var1) {
            var1.printStackTrace();
            System.err.println("Key pair generation failed!");
            return null;
        }
    }

    /**
     * Compute a serverId hash for use by sendSessionRequest()
     */
    public static byte[] getServerIdHash(String par0Str, PublicKey par1PublicKey, SecretKey par2SecretKey) {
        try {
            return digestOperation("SHA-1", par0Str.getBytes("ISO_8859_1"), par2SecretKey.getEncoded(), par1PublicKey.getEncoded());
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    /**
     * Compute a message digest on arbitrary byte[] data
     */
    private static byte[] digestOperation(String par0Str, byte[]... par1ArrayOfByte) {
        try {
            MessageDigest var2 = MessageDigest.getInstance(par0Str);
            byte[][] var3 = par1ArrayOfByte;
            int var4 = par1ArrayOfByte.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                byte[] var6 = var3[var5];
                var2.update(var6);
            }

            return var2.digest();
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
            return null;
        }
    }

    /**
     * Create a new PublicKey from encoded X.509 data
     */
    public static PublicKey decodePublicKey(byte[] par0ArrayOfByte) {
        try {
            X509EncodedKeySpec var1 = new X509EncodedKeySpec(par0ArrayOfByte);
            KeyFactory var2 = KeyFactory.getInstance("RSA");
            return var2.generatePublic(var1);
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
        } catch (InvalidKeySpecException var4) {
            var4.printStackTrace();
        }

        System.err.println("Public key reconstitute failed!");
        return null;
    }

    /**
     * Decrypt shared secret AES key using RSA private key
     */
    public static SecretKey decryptSharedKey(PrivateKey par0PrivateKey, byte[] par1ArrayOfByte) {
        return new SecretKeySpec(decryptData(par0PrivateKey, par1ArrayOfByte), "AES");
    }

    /**
     * Encrypt byte[] data with RSA public key
     */
    public static byte[] encryptData(Key par0Key, byte[] par1ArrayOfByte) {
        return cipherOperation(1, par0Key, par1ArrayOfByte);
    }

    /**
     * Decrypt byte[] data with RSA private key
     */
    public static byte[] decryptData(Key par0Key, byte[] par1ArrayOfByte) {
        return cipherOperation(2, par0Key, par1ArrayOfByte);
    }

    /**
     * Encrypt or decrypt byte[] data using the specified key
     */
    private static byte[] cipherOperation(int par0, Key par1Key, byte[] par2ArrayOfByte) {
        try {
            return createTheCipherInstance(par0, par1Key.getAlgorithm(), par1Key).doFinal(par2ArrayOfByte);
        } catch (IllegalBlockSizeException var4) {
            var4.printStackTrace();
        } catch (BadPaddingException var5) {
            var5.printStackTrace();
        }

        System.err.println("Cipher data failed!");
        return null;
    }

    /**
     * Creates the Cipher Instance.
     */
    private static Cipher createTheCipherInstance(int par0, String par1Str, Key par2Key) {
        try {
            Cipher var3 = Cipher.getInstance(par1Str);
            var3.init(par0, par2Key);
            return var3;
        } catch (InvalidKeyException var4) {
            var4.printStackTrace();
        } catch (NoSuchAlgorithmException var5) {
            var5.printStackTrace();
        } catch (NoSuchPaddingException var6) {
            var6.printStackTrace();
        }

        System.err.println("Cipher creation failed!");
        return null;
    }

    /**
     * Create a new BufferedBlockCipher instance
     */
    private static BufferedBlockCipher createBufferedBlockCipher(boolean par0, Key par1Key) {
        BufferedBlockCipher var2 = new BufferedBlockCipher(new CFBBlockCipher(new AESFastEngine(), 8));
        var2.init(par0, new ParametersWithIV(new KeyParameter(par1Key.getEncoded()), par1Key.getEncoded(), 0, 16));
        return var2;
    }

    public static OutputStream encryptOuputStream(SecretKey par0SecretKey, OutputStream par1OutputStream) {
        return new CipherOutputStream(par1OutputStream, createBufferedBlockCipher(true, par0SecretKey));
    }

    public static InputStream decryptInputStream(SecretKey par0SecretKey, InputStream par1InputStream) {
        return new CipherInputStream(par1InputStream, createBufferedBlockCipher(false, par0SecretKey));
    }

    public static byte[] sign(AuthData.ProfileKeys keys, Consumer<Signature> signatureConsumer) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keys.getPrivateKey());
            Objects.requireNonNull(signature);
            signatureConsumer.accept(signature);
            return signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArgumentSignatures signCommandArguments(AuthData.ProfileKeys keys, UUID signer, List<SignableArgument> arguments) {
        long salt = new Random().nextLong();
        Instant timestamp = Instant.now();

        List<ArgumentSignature> signatures = arguments.stream().map(signableArgument -> {
            MessageSignature signature = signMessage(keys, signer, signableArgument.getArgumentValue(), salt, timestamp);
            return new ArgumentSignature(signableArgument.getArgumentName(), signature.getSignature());
        }).collect(Collectors.toList());

        return new ArgumentSignatures(signatures, salt, timestamp);
    }

    public static MessageSignature signChatMessage(AuthData.ProfileKeys keys, UUID signer, String message) {
        long salt = new Random().nextLong();
        Instant timestamp = Instant.now();
        return signMessage(keys, signer, message, salt, timestamp);
    }

    private static MessageSignature signMessage(AuthData.ProfileKeys keys, UUID signer, String message, long salt, Instant timestamp) {
        String component = "{\"text\":\"" + message + "\"}";
        if (FishingBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_19_1) {
            byte[] messageHeader = new byte[16];
            ByteBuffer.wrap(messageHeader).order(ByteOrder.BIG_ENDIAN).putLong(signer.getMostSignificantBits()).putLong(signer.getLeastSignificantBits());

            HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha256(), new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    // empty
                }
            });

            try {
                DataOutputStream dataOutputStream = new DataOutputStream(hashingOutputStream);
                dataOutputStream.writeLong(salt);
                dataOutputStream.writeLong(timestamp.getEpochSecond());
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(dataOutputStream, StandardCharsets.UTF_8);
                outputStreamWriter.write(message);
                outputStreamWriter.flush();
                dataOutputStream.write(70);
            } catch (IOException var4) {
            }

            byte[] messageBody = hashingOutputStream.hash().asBytes();
            return new MessageSignature(sign(keys, signature -> {
                try {
                    Optional<MessageSignature> prevSignature = FishingBot.getInstance().getCurrentBot().getPlayer().getLastUsedSignature();
                    if (prevSignature.isPresent())
                        signature.update(prevSignature.get().getSignature());
                    signature.update(messageHeader);
                    signature.update(messageBody);
                } catch (SignatureException e) {
                    e.printStackTrace();
                }
            }), salt, timestamp);
        } else {
            byte[] bs = new byte[32];
            ByteBuffer byteBuffer = ByteBuffer.wrap(bs).order(ByteOrder.BIG_ENDIAN);
            byteBuffer.putLong(salt);
            byteBuffer.putLong(signer.getMostSignificantBits()).putLong(signer.getLeastSignificantBits());
            byteBuffer.putLong(timestamp.getEpochSecond());
            return new MessageSignature(sign(keys, signature -> {
                try {
                    signature.update(bs);
                    signature.update(component.getBytes(StandardCharsets.UTF_8));
                } catch (SignatureException e) {
                    e.printStackTrace();
                }
            }), salt, timestamp);
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class MessageSignature {
        private final byte[] signature;
        private final long salt;
        private final Instant timestamp;
    }

    @RequiredArgsConstructor
    @Getter
    public static class ArgumentSignature {
        private final String name;
        private final byte[] signature;
    }

    @RequiredArgsConstructor
    @Getter
    public static class ArgumentSignatures {
        private final List<ArgumentSignature> argumentSignatures;
        private final long salt;
        private final Instant timestamp;
    }

    @RequiredArgsConstructor
    @Getter
    public static class SignableArgument {
        private final String argumentName;
        private final String argumentValue;
    }
}