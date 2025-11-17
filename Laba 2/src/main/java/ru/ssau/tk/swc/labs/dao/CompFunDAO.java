package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.CompFun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompFunDAO {
    private static final Logger logger = LoggerFactory.getLogger(CompFunDAO.class);
    private DataSourceProvider dataSourceProvider;

    public CompFunDAO(DataSourceProvider dataSourceProvider){
        this.dataSourceProvider = dataSourceProvider;
    }

    public Optional<CompFun> findByID(Long id){
        String sql = "SELECT * FROM compFun WHERE id = ?";
        logger.info("Начало поиска композитной функции по id: {}", id);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setLong(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    CompFun compFun = mapResultSetToCompFun(rs);
                    logger.info("Композитнпя функция по id: {} найдена", id);
                    return Optional.of(compFun);
                }
            }
        }catch (SQLException e){
            logger.error("Ошибка поиска композитной функции под id: {}", id, e);
        }
        logger.debug("Композитнпя функция по id: {} не найдена", id);
        return Optional.empty();
    }

    public Optional<CompFun> findByName(String name){
        String sql = "SELECT * FROM compFun WHERE name = ?";
        logger.info("Начало поиска композитной функции по имени: {}", name);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, name);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    CompFun compFun = mapResultSetToCompFun(rs);
                    logger.info("Композитная функция: {} найдена", name);
                    return Optional.of(compFun);
                }
            }
        }catch (SQLException e){
            logger.error("Ошибка поиска композитной функции: {}", name, e);
        }
        logger.debug("Композитнпя функция: {} не найдена", name);
        return Optional.empty();
    }

    public List<CompFun> findAll(){
        String sql = "SELECT * FROM compFun ORDER BY name";
        logger.info("Выгрузка всех композитных функций");
        List<CompFun> compFuns = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                compFuns.add(mapResultSetToCompFun(rs));
            }
            logger.info("Выгруженно {} композитной функций", compFuns.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех композитной функций", e);
        }
        return compFuns;
    }

    public Long create(CompFun compFun){
        String sql = "INSERT INTO compFun (name) VALUES (?)";
        logger.info("Создание композитной функции: {}", compFun.getName());

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, compFun.getName());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        logger.info("Композитная функция: {} под id: {} создана", compFun.getName(),id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания композитной функции", e);
        }
        logger.error("Ошибка создания композитной функции: {}", compFun.getName());
        return null;
    }
    public boolean delete(Long id) {
        String sql = "DELETE FROM compFun WHERE id = ?";
        logger.info("Удаление композитной функции под id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Композитная функция под id: {} удалена", id);
            } else {
                logger.warn("Композитная функция под id: {} не найдена", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления композитной функции под id: {}", id, e);
        }
        return false;
    }

    private CompFun mapResultSetToCompFun(ResultSet rs) throws SQLException {
        CompFun compFun = new CompFun();
        compFun.setId(rs.getLong("id"));
        compFun.setName(rs.getString("name"));
        return compFun;
    }
}