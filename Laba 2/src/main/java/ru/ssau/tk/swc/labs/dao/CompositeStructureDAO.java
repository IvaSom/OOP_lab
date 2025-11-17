package ru.ssau.tk.swc.labs.dao;

import ru.ssau.tk.swc.labs.entity.CompositeStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompositeStructureDAO {
    private static final Logger logger = LoggerFactory.getLogger(CompositeStructureDAO.class);
    private DataSourceProvider dataSourceProvider;

    public CompositeStructureDAO(DataSourceProvider dataSourceProvider){
        this.dataSourceProvider = dataSourceProvider;
    }

    public Optional<CompositeStructure> findByID(Long id){
        String sql = "SELECT * FROM composite_structure WHERE id = ?";
        logger.info("Начало поиска вложенных функций по id: {}", id);

        try(Connection conn = dataSourceProvider.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setLong(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    CompositeStructure compositeStructure = mapResultSetToCompositeStructure(rs);
                    logger.info("Вложенные функции по id: {} найдены", id);
                    return Optional.of(compositeStructure);
                }
            }
        }catch (SQLException e){
            logger.error("Ошибка поиска вложенных функций под id: {}", id, e);
        }
        logger.debug("Вложенные функции по id: {} не найдены", id);
        return Optional.empty();
    }

    public List<CompositeStructure> findAll(){
        String sql = "SELECT * FROM composite_structure ORDER BY id";
        logger.info("Выгрузка всех вложенных функций");
        List<CompositeStructure> compositeStructures = new ArrayList<>();

        try (Connection conn = dataSourceProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                compositeStructures.add(mapResultSetToCompositeStructure(rs));
            }
            logger.info("Выгруженно {} вложенных функций", compositeStructures.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех вложенных функций", e);
        }
        return compositeStructures;
    }

    public Long create(CompositeStructure compositeStructure){
        String sql = "INSERT INTO composite_structure (composite_id, analytic_id, execution_order) VALUES (?, ?, ?)";
        logger.info("Создание вложенных функций: {}, {}", compositeStructure.getComposite_id(), compositeStructure.getAnalytic_id());

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, compositeStructure.getComposite_id());
            stmt.setLong(2, compositeStructure.getAnalytic_id());
            stmt.setInt(3, compositeStructure.getOrder());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        logger.info("Вложенная функция под id: {} создана",id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания вложенной функции", e);
        }
        logger.error("Ошибка создания вложенных функций: {}, {}", compositeStructure.getComposite_id(), compositeStructure.getAnalytic_id());
        return null;
    }
    public boolean delete(Long id) {
        String sql = "DELETE FROM composite_structure WHERE id = ?";
        logger.info("Удаление вложенных функций под id: {}", id);

        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Вложенные функции под id: {} удалены", id);
            } else {
                logger.warn("Вложенные функции под id: {} не найдены", id);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления вложенных функций под id: {}", id, e);
        }
        return false;
    }

    private CompositeStructure mapResultSetToCompositeStructure(ResultSet rs) throws SQLException {
        CompositeStructure compositeStructure = new CompositeStructure();
        compositeStructure.setId(rs.getLong("id"));
        compositeStructure.setComposite_id(rs.getLong("composite_id"));
        compositeStructure.setAnalytic_id(rs.getLong("analytic_id"));
        compositeStructure.setOrder(rs.getInt("execution_order"));
        return compositeStructure;
    }
}