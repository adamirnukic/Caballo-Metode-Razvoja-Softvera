package main.caballo.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class PasswordUtil {
    private static final SecureRandom RNG = new SecureRandom();

    private PasswordUtil() {}

    public static String generateSalt(int bytes) {
        byte[] salt = new byte[bytes];
        RNG.nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    public static String hashSha256(String password, String hexSalt) {
        try {
            byte[] salt = HexFormat.of().parseHex(hexSalt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash password", e);
        }
    }

    public static boolean verify(String raw, String hexSalt, String expectedHexHash) {
        return hashSha256(raw, hexSalt).equalsIgnoreCase(expectedHexHash);
    }
}
