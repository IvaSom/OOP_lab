package ru.ssau.tk.swc.labs.servlet;

import ru.ssau.tk.swc.labs.dao.AnalPointDAO;
import ru.ssau.tk.swc.labs.dao.PostgreSQLDataSourceProvider;
import ru.ssau.tk.swc.labs.dto.AnalPointDTO;
import ru.ssau.tk.swc.labs.dto.CreateAnalPointDTO;
import ru.ssau.tk.swc.labs.entity.AnalPoint;
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

@WebServlet("/api/anal-points/*")
public class AnalPointServlet extends HttpServlet {
    private AnalPointDAO analPointDAO;
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(AnalPointServlet.class);

    @Override
    public void init() {
        logger.info("Инициализация AnalPointServlet");
        this.analPointDAO = new AnalPointDAO(new PostgreSQLDataSourceProvider());
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("GET запрос для точек аналитических функций. Path: {}", pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<AnalPoint> points = analPointDAO.findAll();
                List<AnalPointDTO> dtos = points.stream()
                        .map(AnalPointDTO::fromEntity)
                        .collect(Collectors.toList());
                objectMapper.writeValue(resp.getWriter(), dtos);
                logger.info("Возвращено {} точек аналитических функций", dtos.size());
            } else {
                Long id = Long.parseLong(pathInfo.substring(1));
                var point = analPointDAO.findByID(id);

                if (point.isPresent()) {
                    objectMapper.writeValue(resp.getWriter(), AnalPointDTO.fromEntity(point.get()));
                    logger.info("Точка аналитической функции с ID {} найдена", id);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Точка аналитической функции не найдена\"}");
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка в doGet точек аналитических функций", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        logger.info("POST запрос для создания точки аналитической функции");

        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            CreateAnalPointDTO dto = objectMapper.readValue(body, CreateAnalPointDTO.class);

            AnalPoint point = new AnalPoint();
            point.setX(dto.getX());
            point.setY(dto.getY());
            point.setFunID(dto.getFunID());

            Long newId = analPointDAO.create(point);

            if (newId != null) {
                AnalPoint created = analPointDAO.findByID(newId).orElseThrow();
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getWriter(), AnalPointDTO.fromEntity(created));
                logger.info("Создана точка аналитической функции с ID: {}", newId);
            } else {
                throw new Exception("Не удалось создать точку аналитической функции");
            }
        } catch (Exception e) {
            logger.error("Ошибка создания точки аналитической функции", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка создания точки аналитической точки\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("DELETE запрос для точки аналитической функции. Path: {}", pathInfo);

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Не указан ID точки\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            boolean deleted = analPointDAO.delete(id);

            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                logger.info("Точка аналитической функции с ID {} удалена", id);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Точка аналитической функции не найдена\"}");
            }
        } catch (Exception e) {
            logger.error("Ошибка удаления точки аналитической функции", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка удаления точки аналитической функции\"}");
        }
    }
}