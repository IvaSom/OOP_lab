package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.TabPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TabPointDAO {
    private static final Logger logger = LoggerFactory.getLogger(TabPointDAO.class);
    private final DataSourceProvider dataSourceProvider;

    public TabPointDAO(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public Optional<TabPoint> findByID(Long id){
        String sql = "SELECT * FROM tab_points WHERE id = ?";
        logger.info("Начало поиска точки по id: {}", id);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);

            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    TabPoint tabPoint = mapResultSetToTabPoint(rs);
                    logger.info("Точка под id: {} найдена", id);
                    return Optional.of(tabPoint);
                }
            }
        }catch (SQLException e){
            logger.error("Ошибка поиска точки под id: {}", id);
        }
        logger.debug("Точка под id: {} не найдена", id);
        return Optional.empty();
    }

    public Optional<TabPoint> findByXAndFunID(double x, Long funid){
        String sql = "SELECT * FROM tab_points WHERE x = ? AND funID = ?";
        logger.info("Начало поиска точки по x: {} и по функции: {}", x, funid);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setDouble(1, x);
            stmt.setLong(2, funid);

            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    TabPoint tabPoint = mapResultSetToTabPoint(rs);
                    logger.info("Точка по x: {} и по функции: {} найдена", x, funid);
                    return Optional.of(tabPoint);
                }
            }
        }catch (SQLException e){
            logger.error("Ошибка поиска точки по x: {} и по функции: {}", x, funid);
        }
        logger.debug("Точка по x: {} и по функции: {} не найдена", x, funid);
        return Optional.empty();
    }

    public List<TabPoint> findAll() {
        String sql = "SELECT * FROM tab_points";
        logger.info("Выгрузка всех точек");
        List<TabPoint> points = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                points.add(mapResultSetToTabPoint(rs));
            }
            logger.info("Выгруженно {} точек", points.size());
        } catch (SQLException e) {
            logger.error("Ошибка выгрузки точек", e);
        }
        return points;
    }

    public Long create(TabPoint point) {
        String sql = "INSERT INTO tab_points (x, y, derive, funID) VALUES (?, ?, ?, ?)";
        logger.info("Создание новой точки по x: {} и по функции: {}", point.getX(), point.getFunID());

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, point.getX());
            stmt.setDouble(2, point.getY());
            stmt.setDouble(3, point.getDerive());
            stmt.setLong(4, point.getFunID());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        logger.info("Точка создана под id: {}", id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания точки", e);
        }
        logger.error("Ошибка создания точки");
        return null;
    }
    public boolean delete(Long id) {
        String sql = "DELETE FROM tab_points WHERE id = ?";
        logger.info("Удаление точки под id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Точка под id: {} удалена", id);
            } else {
                logger.warn("Точка под id: {} не найдена", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления точки под id: {}", id, e);
        }
        return false;
    }

    public List<TabPoint> sortedByX(String order) {
        String sortOrder = "DESC".equalsIgnoreCase(order) ? "DESC" : "ASC";
        String sql = "SELECT * FROM tab_points ORDER BY x " + sortOrder;
        logger.info("Сортировка точек по X: {}", sortOrder);
        List<TabPoint> points = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                points.add(mapResultSetToTabPoint(rs));
            }
            logger.info("Найдено {} точек", points.size());
        } catch (SQLException e) {
            logger.error("Ошибка при сортировке по X: {}", e.getMessage());
        }
        return points;
    }

    public List<TabPoint> sortedByFunID(String order) {
        String sortOrder = "DESC".equalsIgnoreCase(order) ? "DESC" : "ASC";
        String sql = "SELECT * FROM tab_points ORDER BY funID " + sortOrder;
        logger.info("Сортировка точек по funID: {}", sortOrder);
        List<TabPoint> points = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                points.add(mapResultSetToTabPoint(rs));
            }
            logger.info("Найдено {} точек", points.size());
        } catch (SQLException e) {
            logger.error("Ошибка при сортировке по funID: {}", e.getMessage());
        }
        return points;
    }

    public List<TabPoint> sortedByID(String order) {
        String sortOrder = "DESC".equalsIgnoreCase(order) ? "DESC" : "ASC";
        String sql = "SELECT * FROM tab_points ORDER BY id " + sortOrder;
        logger.info("Сортировка точек по ID: {}", sortOrder);
        List<TabPoint> points = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                points.add(mapResultSetToTabPoint(rs));
            }
            logger.info("Найдено {} точек", points.size());
        } catch (SQLException e) {
            logger.error("Ошибка при сортировке по ID: {}", e.getMessage());
        }
        return points;
    }

    private TabPoint mapResultSetToTabPoint(ResultSet rs) throws SQLException {
        TabPoint tabPoint = new TabPoint();
        tabPoint.setId(rs.getLong("id"));
        tabPoint.setX(rs.getDouble("x"));
        tabPoint.setY(rs.getDouble("y"));
        tabPoint.setDerive(rs.getDouble("derive"));
        tabPoint.setFunID(rs.getLong("funID"));
        return tabPoint;
    }
}