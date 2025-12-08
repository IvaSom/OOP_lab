package ru.ssau.tk.swc.labs.servlet;

import ru.ssau.tk.swc.labs.dao.CompPointDAO;
import ru.ssau.tk.swc.labs.dao.PostgreSQLDataSourceProvider;
import ru.ssau.tk.swc.labs.dto.CompPointDTO;
import ru.ssau.tk.swc.labs.dto.CreateCompPointDTO;
import ru.ssau.tk.swc.labs.entity.CompPoint;
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

@WebServlet("/api/comp-points/*")
public class CompPointServlet extends HttpServlet {
    private CompPointDAO compPointDAO;
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(CompPointServlet.class);

    @Override
    public void init() {
        logger.info("Инициализация CompPointServlet");
        this.compPointDAO = new CompPointDAO(new PostgreSQLDataSourceProvider());
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("GET запрос для точек композитных функций. Path: {}", pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<CompPoint> points = compPointDAO.findAll();
                List<CompPointDTO> dtos = points.stream()
                        .map(CompPointDTO::fromEntity)
                        .collect(Collectors.toList());
                objectMapper.writeValue(resp.getWriter(), dtos);
                logger.info("Возвращено {} точек композитных функций", dtos.size());
            } else {
                Long id = Long.parseLong(pathInfo.substring(1));
                var point = compPointDAO.findByID(id);

                if (point.isPresent()) {
                    objectMapper.writeValue(resp.getWriter(), CompPointDTO.fromEntity(point.get()));
                    logger.info("Точка композитной функции с ID {} найдена", id);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Точка композитной функции не найдена\"}");
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка в doGet точек композитных функций", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        logger.info("POST запрос для создания точки композитной функции");

        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            CreateCompPointDTO dto = objectMapper.readValue(body, CreateCompPointDTO.class);

            CompPoint point = new CompPoint();
            point.setX(dto.getX());
            point.setY(dto.getY());
            point.setFunID(dto.getFunID());

            Long newId = compPointDAO.create(point);

            if (newId != null) {
                CompPoint created = compPointDAO.findByID(newId).orElseThrow();
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getWriter(), CompPointDTO.fromEntity(created));
                logger.info("Создана точка композитной функции с ID: {}", newId);
            } else {
                throw new Exception("Не удалось создать точку композитной функции");
            }
        } catch (Exception e) {
            logger.error("Ошибка создания точки композитной функции", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка создания точки композитной функции\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("DELETE запрос для точки композитной функции. Path: {}", pathInfo);

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Не указан ID точки\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            boolean deleted = compPointDAO.delete(id);

            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                logger.info("Точка композитной функции с ID {} удалена", id);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Точка композитной функции не найдена\"}");
            }
        } catch (Exception e) {
            logger.error("Ошибка удаления точки композитной функции", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка удаления точки композитной функции\"}");
        }
    }
}
