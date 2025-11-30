package ru.ssau.tk.swc.labs.servlet;

import ru.ssau.tk.swc.labs.dao.CompositeStructureDAO;
import ru.ssau.tk.swc.labs.dao.PostgreSQLDataSourceProvider;
import ru.ssau.tk.swc.labs.dto.CompositeStructureDTO;
import ru.ssau.tk.swc.labs.dto.CreateCompositeStructureDTO;
import ru.ssau.tk.swc.labs.entity.CompositeStructure;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/composite-structures/*")
public class CompositeStructureServlet extends HttpServlet {
    private CompositeStructureDAO compositeStructureDAO;
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(CompositeStructureServlet.class);

    @Override
    public void init() {
        logger.info("Инициализация CompositeStructureServlet");
        this.compositeStructureDAO = new CompositeStructureDAO(new PostgreSQLDataSourceProvider());
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("GET запрос для композитных структур. Path: {}", pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<CompositeStructure> structures = compositeStructureDAO.findAll();
                List<CompositeStructureDTO> dtos = structures.stream()
                        .map(CompositeStructureDTO::fromEntity)
                        .collect(Collectors.toList());
                objectMapper.writeValue(resp.getWriter(), dtos);
                logger.info("Возвращено {} композитных структур", dtos.size());
            } else {
                Long id = Long.parseLong(pathInfo.substring(1));
                var structure = compositeStructureDAO.findByID(id);

                if (structure.isPresent()) {
                    objectMapper.writeValue(resp.getWriter(), CompositeStructureDTO.fromEntity(structure.get()));
                    logger.info("Композитная структура с ID {} найдена", id);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Композитная структура не найдена\"}");
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка в doGet композитных структур", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        logger.info("POST запрос для создания композитной структуры");

        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            CreateCompositeStructureDTO dto = objectMapper.readValue(body, CreateCompositeStructureDTO.class);

            CompositeStructure structure = new CompositeStructure();
            structure.setComposite_id(dto.getCompositeId());
            structure.setAnalytic_id(dto.getAnalyticId());
            structure.setOrder(dto.getExecutionOrder());

            Long newId = compositeStructureDAO.create(structure);

            if (newId != null) {
                CompositeStructure created = compositeStructureDAO.findByID(newId).orElseThrow();
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getWriter(), CompositeStructureDTO.fromEntity(created));
                logger.info("Создана композитная структура с ID: {}", newId);
            } else {
                throw new Exception("Не удалось создать композитную структуру");
            }
        } catch (Exception e) {
            logger.error("Ошибка создания композитной структуры", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка создания композитной структуры\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("DELETE запрос для композитной структуры. Path: {}", pathInfo);

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Не указан ID структуры\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            boolean deleted = compositeStructureDAO.delete(id);

            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                logger.info("Композитная структура с ID {} удалена", id);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Композитная структура не найдена\"}");
            }
        } catch (Exception e) {
            logger.error("Ошибка удаления композитной структуры", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка удаления композитной структуры\"}");
        }
    }
}