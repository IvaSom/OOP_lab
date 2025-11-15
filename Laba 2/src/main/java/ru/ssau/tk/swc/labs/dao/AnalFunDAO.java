package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.AnalFun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnalFunDAO {
    private static final Logger logger = LoggerFactory.getLogger(AnalFunDAO.class);
    private DataSourceProvider dataSourceProvider;

    public AnalFunDAO(DataSourceProvider dataSourceProvider){
        this.dataSourceProvider = dataSourceProvider;
    }

    public Optional<AnalFun> findByID(Long id){
        String sql = "SELECT * FROM analFun WHERE id = ?";
        logger.info("Начало поиска аналитической функции по id: {}", id);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setLong(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    AnalFun analFun = mapResultSetToAnalFun(rs);
                    logger.info("Аналитическая функция по id: {} найдена", id);
                    return Optional.of(analFun);
                }
            }
        }catch (SQLException e){
            logger.error("Ошибка поиска аналитической функции под id: {}", id, e);
        }
        logger.debug("Аналитическая функция по id: {} не найдена", id);
        return Optional.empty();
    }

    public Optional<AnalFun> findByName(String name){
        String sql = "SELECT * FROM analFun WHERE name = ?";
        logger.info("Начало поиска аналитической функции по имени: {}", name);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, name);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    AnalFun analFun = mapResultSetToAnalFun(rs);
                    logger.info("Аналитическая функция: {} найдена", name);
                    return Optional.of(analFun);
                }
            }
        }catch (SQLException e){
            logger.error("Ошибка поиска аналитической функции: {}", name, e);
        }
        logger.debug("Аналитическая функция: {} не найдена", name);
        return Optional.empty();
    }

    public List<AnalFun> findAll(){
        String sql = "SELECT * FROM analFun ORDER BY name";
        logger.info("Выгрузка всех аналитических функций");
        List<AnalFun> analFuns = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                analFuns.add(mapResultSetToAnalFun(rs));
            }
            logger.info("Выгруженно {} аналитических функций", analFuns.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех аналитических функций", e);
        }
        return analFuns;
    }

    public Long create(AnalFun analFun){
        String sql = "INSERT INTO analFun (name, type) VALUES (?, ?)";
        logger.info("Создание аналитической функции: {}", analFun.getName());

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, analFun.getName());
            stmt.setInt(2, analFun.getType());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        logger.info("Аналиточеская функция: {} под id: {} создана", analFun.getName(),id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания аналитической функции", e);
        }
        logger.error("Ошибка создания аналитической функции: {}", analFun.getName());
        return null;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM analFun WHERE id = ?";
        logger.info("Удаление аналитической функции под id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            if (stmt.executeUpdate() > 0)
                logger.info("Аналитическая функция под id: {} удалена", id);
            else
                logger.warn("Аналитическая функция под id: {} не найдена", id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления аналитической функции под id: {}", id, e);
        }
        return false;
    }

    private AnalFun mapResultSetToAnalFun(ResultSet rs) throws SQLException {
        AnalFun analFun = new AnalFun();
        analFun.setId(rs.getLong("id"));
        analFun.setName(rs.getString("name"));
        analFun.setType(rs.getInt("type"));
        return analFun;
    }
}