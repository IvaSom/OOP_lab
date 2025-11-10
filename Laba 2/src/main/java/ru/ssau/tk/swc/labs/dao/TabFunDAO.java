package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.TabFun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TabFunDAO {
    private static final Logger logger = LoggerFactory.getLogger(TabFunDAO.class);
    private DataSourceProvider dataSourceProvider;

    public TabFunDAO(DataSourceProvider dataSourceProvider){
        this.dataSourceProvider = dataSourceProvider;
    }

    public Optional<TabFun> findByID(Long id){
        String sql = "SELECT * FROM tabFun WHERE id = ?";
        logger.info("Начало поиска табулированной функции по id: {}", id);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setLong(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    TabFun tabFun = mapResultSetToTabFun(rs);
                    logger.info("Табулированная функция по id: {} найдена", id);
                    return Optional.of(tabFun);
                }
            }
        }catch (SQLException e){
            logger.error("Ошибка поиска табулированной функции под id: {}", id, e);
        }
        logger.debug("Табулированная функция по id: {} не найдена", id);
        return Optional.empty();
    }

    public Optional<TabFun> findByName(String name){
        String sql = "SELECT * FROM tabFun WHERE name = ?";
        logger.info("Начало поиска табулированной функции по имени: {}", name);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, name);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    TabFun tabFun = mapResultSetToTabFun(rs);
                    logger.info("Табулированная функция: {} найдена", name);
                    return Optional.of(tabFun);
                }
            }
        }catch (SQLException e){
            logger.error("Ошибка поиска табулированной функции: {}", name, e);
        }
        logger.debug("Табулированная функция: {} не найдена", name);
        return Optional.empty();
    }

    public List<TabFun> findAll(){
        String sql = "SELECT * FROM tabFun ORDER BY name";
        logger.info("Выгрузка всех аналитических функций");
        List<TabFun> tabFuns = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tabFuns.add(mapResultSetToTabFun(rs));
            }
            logger.info("Выгруженно {} аналитических функций", tabFuns.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех аналитических функций", e);
        }
        return tabFuns;
    }

    public Long create(TabFun tabFun){
        String sql = "INSERT INTO tabFun (name, type) VALUES (?, ?)";
        logger.info("Создание табулированной функции: {}", tabFun.getName());

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tabFun.getName());
            stmt.setString(2, tabFun.getType());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        logger.info("Аналиточеская функция: {} под id: {} создана", tabFun.getName(),id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания табулированной функции", e);
        }
        logger.error("Ошибка создания табулированной функции: {}", tabFun.getName());
        return null;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM tabFun WHERE id = ?";
        logger.info("Удаление табулированной функции под id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            if (stmt.executeUpdate() > 0)
                logger.info("Табулированная функция под id: {} удалена", id);
            else
                logger.warn("Табулированная функция под id: {} не найдена", id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления табулированной функции под id: {}", id, e);
        }
        return false;
    }

    private TabFun mapResultSetToTabFun(ResultSet rs) throws SQLException {
        TabFun tabFun = new TabFun();
        tabFun.setId(rs.getLong("id"));
        tabFun.setName(rs.getString("name"));
        tabFun.setType(rs.getString("type"));
        return tabFun;
    }
}
