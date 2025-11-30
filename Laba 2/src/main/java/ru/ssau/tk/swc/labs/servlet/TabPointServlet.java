package ru.ssau.tk.swc.labs.servlet;

import ru.ssau.tk.swc.labs.dao.TabPointDAO;
import ru.ssau.tk.swc.labs.dao.PostgreSQLDataSourceProvider;
import ru.ssau.tk.swc.labs.dto.TabPointDTO;
import ru.ssau.tk.swc.labs.dto.CreateTabPointDTO;
import ru.ssau.tk.swc.labs.entity.TabPoint;
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

@WebServlet("/api/tab-points/*")
public class TabPointServlet extends HttpServlet {
    private TabPointDAO tabPointDAO;
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(TabPointServlet.class);

    @Override
    public void init() {
        logger.info("Инициализация TabPointServlet");
        this.tabPointDAO = new TabPointDAO(new PostgreSQLDataSourceProvider());
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("GET запрос для точек табулированных функций. Path: {}", pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<TabPoint> points = tabPointDAO.findAll();
                List<TabPointDTO> dtos = points.stream()
                        .map(TabPointDTO::fromEntity)
                        .collect(Collectors.toList());
                objectMapper.writeValue(resp.getWriter(), dtos);
                logger.info("Возвращено {} точек табулированных функций", dtos.size());
            } else {
                Long id = Long.parseLong(pathInfo.substring(1));
                var point = tabPointDAO.findByID(id);

                if (point.isPresent()) {
                    objectMapper.writeValue(resp.getWriter(), TabPointDTO.fromEntity(point.get()));
                    logger.info("Точка табулированной функции с ID {} найдена", id);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Точка табулированной функции не найдена\"}");
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка в doGet точек табулированных функций", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        logger.info("POST запрос для создания точки табулированной функции");

        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            CreateTabPointDTO dto = objectMapper.readValue(body, CreateTabPointDTO.class);

            TabPoint point = new TabPoint();
            point.setX(dto.getX());
            point.setY(dto.getY());
            point.setDerive(dto.getDerive());
            point.setFunID(dto.getFunID());

            Long newId = tabPointDAO.create(point);

            if (newId != null) {
                TabPoint created = tabPointDAO.findByID(newId).orElseThrow();
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getWriter(), TabPointDTO.fromEntity(created));
                logger.info("Создана точка табулированной функции с ID: {}", newId);
            } else {
                throw new Exception("Не удалось создать точку табулированной функции");
            }
        } catch (Exception e) {
            logger.error("Ошибка создания точки табулированной функции", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка создания точки табулированной функции\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("DELETE запрос для точки табулированной функции. Path: {}", pathInfo);

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Не указан ID точки\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            boolean deleted = tabPointDAO.delete(id);

            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                logger.info("Точка табулированной функции с ID {} удалена", id);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Точка табулированной функции не найдена\"}");
            }
        } catch (Exception e) {
            logger.error("Ошибка удаления точки табулированной функции", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка удаления точки табулированной функции\"}");
        }
    }
}
