package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Класс, отвечающий только за таблицу users.
 * Создаёт новых пользователей, проверяет логин и пароль, получает id пользователя по имени.
 */
public class UserDAO {
    private Connection connection;

    public UserDAO(DBManager dbManager) throws SQLException {
        this.connection = dbManager.getConnection();
    }

    public boolean registerUser(String username, String passwordHash) throws SQLException {
        String query = "INSERT INTO users (username, password_hash) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            return stmt.executeUpdate() > 0; // true — если добавлен
        }
    }

    public boolean checkUser(String username, String passwordHash) throws SQLException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");



                return storedHash.equals(passwordHash);
            } else {
                return false;
            }
        }
    }

    public int getUserId(String username) throws SQLException {
        String query = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
                else throw new SQLException("Пользователь не найден: " + username);
            }
        }
    }

}
