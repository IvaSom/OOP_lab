package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.CompPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompPointDAO {
    private static final Logger logger = LoggerFactory.getLogger(CompPointDAO.class);
    private final DataSourceProvider dataSourceProvider;

    public CompPointDAO(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public Optional<CompPoint> findByID(Long id){
        String sql = "SELECT * FROM comp_points WHERE id = ?";
        logger.info("Начало поиска точки по id: {}", id);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);

            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    CompPoint compPoint = mapResultSetToCompPoint(rs);
                    logger.info("Точка под id: {} найдена", id);
                    return Optional.of(compPoint);
                }
            }
        }catch (SQLException e){
            logger.error("Ошибка поиска точки под id: {}", id);
        }
        logger.debug("Точка под id: {} не найдена", id);
        return Optional.empty();
    }

    public Optional<CompPoint> findByXAndFubID(double x, Long funid){
        String sql = "SELECT * FROM comp_points WHERE x = ? AND funID = ?";
        logger.info("Начало поиска точки по x: {} и по функции: {}", x, funid);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setDouble(1, x);
            stmt.setLong(2, funid);

            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    CompPoint compPoint = mapResultSetToCompPoint(rs);
                    logger.info("Точка по x: {} и по функции: {} найдена", x, funid);
                    return Optional.of(compPoint);
                }
            }
        }catch (SQLException e){
            logger.error("Ошибка поиска точки по x: {} и по функции: {}", x, funid);
        }
        logger.debug("Точка по x: {} и по функции: {} не найдена", x, funid);
        return Optional.empty();
    }

    public List<CompPoint> findAll() {
        String sql = "SELECT * FROM comp_points";
        logger.info("Выгрузка всех точек");
        List<CompPoint> points = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                points.add(mapResultSetToCompPoint(rs));
            }
            logger.info("Выгруженно {} точек", points.size());
        } catch (SQLException e) {
            logger.error("Ошибка выгрузки точек", e);
        }
        return points;
    }

    public Long create(CompPoint point) {
        String sql = "INSERT INTO comp_points (x, y, derive, funID) VALUES (?, ?, ?, ?)";
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
        String sql = "DELETE FROM comp_points WHERE id = ?";
        logger.info("Удаление точки под id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            if (stmt.executeUpdate() > 0) {
                logger.info("Точка под id: {} удалена", id);
            } else {
                logger.warn("Точка под id: {} не найдена", id);
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления точки под id: {}", id, e);
        }
        return false;
    }

    private CompPoint mapResultSetToCompPoint(ResultSet rs) throws SQLException {
        CompPoint compPoint = new CompPoint();
        compPoint.setId(rs.getLong("id"));
        compPoint.setX(rs.getDouble("x"));
        compPoint.setY(rs.getDouble("y"));
        compPoint.setDerive(rs.getDouble("derive"));
        compPoint.setFunID(rs.getLong("funID"));
        return compPoint;
    }
}
