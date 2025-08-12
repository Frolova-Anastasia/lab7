package db;

import java.sql.SQLException;

public class AuthManager {
    private final UserDAO userDAO;

    public AuthManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean login(String username, String passwordHash) throws SQLException {
        return userDAO.checkUser(username, passwordHash);
    }

    public boolean register(String username, String passwordHash) throws SQLException {
        return userDAO.registerUser(username, passwordHash);
    }

    public int getUserId(String username) throws SQLException {
        return userDAO.getUserId(username);
    }

}
