package utility;

import java.security.MessageDigest;

public class PasswordHasher {
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при хэшировании пароля", e);
        }
    }
}
