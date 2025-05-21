package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;

import model.javaBean.AppUser;
import utils.ConnectionFactory;

public class AppUserDAO {

    // add user
    public void insert(AppUser user) throws SQLException {
        String sql = "INSERT INTO AppUser (user_name, email, password) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
    		 // 自動保留 DB 所產生的 key 通常是 Primary Key
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.username());
            ps.setString(2, user.email());
            ps.setString(3, user.password());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                // auto create user_id and createdAt
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        user = new AppUser(id, user.username(), user.email(), user.password(), LocalDateTime.now());
                    }
                }
            }
        } 
    }

    // search user by user_name,Optional can avoid no user
    public Optional<AppUser> findByUsername(String userName) throws SQLException {
        String sql = "SELECT user_id, user_name, email, password, createdAt FROM AppUser WHERE user_name = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AppUser user = new AppUser(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                    );
                    return Optional.of(user);
                }
            }
        } 
        return Optional.empty();
    }

    // search user by user_id
    public Optional<AppUser> findById(int userId) throws SQLException {
        String sql = "SELECT user_id, user_name, email, password, createdAt FROM AppUser WHERE user_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AppUser user = new AppUser(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                    );
                    return Optional.of(user);
                }
            }
        } 
        return Optional.empty();
    }

    // update email
    public void updateEmail(int userId, String newEmail) throws SQLException {
        String sql = "UPDATE AppUser SET email = ? WHERE user_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newEmail);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } 
    }

    // update password
    public void updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE AppUser SET password = ? WHERE user_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } 
    }

    // delete user
    public void delete(int userId) throws SQLException {
        String sql = "DELETE FROM AppUser WHERE user_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
        } 
    }
}
