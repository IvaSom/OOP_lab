package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    private DataSourceProvider dataSourceProvider;

    public UserDAO(DataSourceProvider dataSourceProvider){
        this.dataSourceProvider = dataSourceProvider;
    }

    public Optional<User> findByID(Long id){
        String sql = "SELECT * FROM users WHERE id = ?";
        logger.info("Начало поиска пользователя под id: {}", id);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if(rs.next()) {
                    User user = mapResultSetToUser(rs);
                    logger.debug("Пользователь найден: {}", user.getId());
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска пользователя под id: {}", id, e);
        }
        logger.debug("Ненайден пользователь под id: {}", id);
        return Optional.empty();
    }

    public Optional<User> findByLogin(String login){
        String sql = "SELECT * FROM users WHERE login = ?";
        logger.info("Начало поиска пользователя под login: {}", login);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    User user = mapResultSetToUser(rs);
                    logger.debug("Пользователь найден: {}", user.getLogin());
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска пользователя под login: {}", login, e);
        }
        logger.debug("Ненайден пользователь под login: {}", login);
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email){
        String sql = "SELECT * FROM users WHERE email = ?";
        logger.info("Начало поиска пользователя под email: {}", email);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    User user = mapResultSetToUser(rs);
                    logger.debug("Пользователь найден: {}", user.getEmail());
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска пользователя под email: {}", email, e);
        }
        logger.debug("Ненайден пользователь под email: {}", email);
        return Optional.empty();
    }

    public Optional<User> findByLoginAndPassword(String login, String password){
        String sql = "SELECT * FROM users WHERE login = ? AND password = ?";
        logger.info("Начало поиска пользователя по login: {} и паролю", login);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, login);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    logger.debug("Пользоватль авторизирован: {}", login);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска пользователя по логину и паролю", e);
        }
        logger.debug("Аутентификация по login: {} проваленa", login);
        return Optional.empty();
    }

    public List<User> findAll(){
        String sql = "SELECT * FROM users ORDER BY name";
        logger.info("Выгрузка всех пользователей");
        List<User> users = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            logger.info("Выгруженно {} пользователей", users.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех пользователей", e);
        }
        return users;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setLogin(rs.getString("login"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        return user;
    }

    public Long create(User user){
        String sql = "INSERT INTO users (name, login, email, password) VALUES (?, ?, ?, ?)";
        logger.info("Добавляем нового пользователя под login: {}", user.getLogin());

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             stmt.setString(1, user.getName());
             stmt.setString(2, user.getLogin());
             stmt.setString(3, user.getEmail());
             stmt.setString(4, user.getPassword());

            if (stmt.executeUpdate() > 0){
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        logger.info("Пользователь под id: {} создан", id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при создании пользователя", e);
        }
        logger.error("Пользователь под login: {} не был создан", user.getLogin());
        return null;
    }

    public boolean updatePassword(User user){
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        logger.info("Начало обновления пароля пользователя по id: {}", user.getId());

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getPassword());
            stmt.setLong(2, user.getId());

            if (stmt.executeUpdate() > 0)
                logger.info("Пароль пользователя под id: {} обновлен", user.getId());
            else
                logger.debug("Пользователь под id: {} не найден", user.getId());

            return stmt.executeUpdate() > 0;
        }catch (SQLException e) {
            logger.error("Ошибка обновления пароля пользователя под id: {}", user.getId(), e);
        }

        return false;
    }

    public boolean updateEmail(User user){
        String sql = "UPDATE users SET email = ? WHERE id = ?";
        logger.info("Начало обновления email пользователя по id: {}", user.getId());

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setLong(2, user.getId());

            if (stmt.executeUpdate() > 0)
                logger.info("Email пользователя под id: {} обновлен", user.getId());
            else
                logger.debug("Пользователь под id: {} не найден", user.getId());

            return stmt.executeUpdate() > 0;
        }catch (SQLException e) {
            logger.error("Ошибка обновления email подбзователя под id: {}", user.getId(), e);
        }

        return false;
    }

    public boolean updateLogin(User user){
        String sql = "UPDATE users SET login = ? WHERE id = ?";
        logger.info("Начало логина обновления пользователя по id: {}", user.getId());

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getLogin());
            stmt.setLong(2, user.getId());

            if (stmt.executeUpdate() > 0)
                logger.info("Логин пользователя под id: {} обновлен", user.getId());
            else
                logger.debug("Пользователь под id: {} не найден", user.getId());

            return stmt.executeUpdate() > 0;
        }catch (SQLException e) {
            logger.error("Ошибка обновления логина подбзователя под id: {}", user.getId(), e);
        }

        return false;
    }

    public boolean updateName(User user){
        String sql = "UPDATE users SET name = ? WHERE id = ?";
        logger.info("Начало имени обновления пользователя по id: {}", user.getId());

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setLong(2, user.getId());

            if (stmt.executeUpdate() > 0)
                logger.info("Имя пользователя под id: {} обновлен", user.getId());
            else
                logger.debug("Пользователь под id: {} не найден", user.getId());

            return stmt.executeUpdate() > 0;
        }catch (SQLException e) {
            logger.error("Ошибка обновления имени пользователя под id: {}", user.getId(), e);
        }

        return false;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        logger.info("Deleting users with id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            if (stmt.executeUpdate() > 0)
                logger.info("Пользователь по id: {} удален", id);
            else
                logger.debug("Пользователь под id: {} не найден", id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления пользователя под id: {}", id, e);
        }
        return false;
    }
}