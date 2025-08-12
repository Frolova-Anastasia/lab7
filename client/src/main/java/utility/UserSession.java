package utility;

public class UserSession {
    private static String username;
    private static String passwordHash;
    private static boolean authorized = false;

    public static void authorize(String user, String passHash) {
        username = user;
        passwordHash = passHash;
        authorized = true;

    }

    public static String getUsername() {
        return username;
    }

    public static String getPasswordHash() {
        return passwordHash;
    }

    public static boolean isAuthorized() {
        return username != null && passwordHash != null;
    }

    public static void clear() {
        username = null;
        passwordHash = null;
    }
}
