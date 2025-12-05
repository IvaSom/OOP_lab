package ru.ssau.tk.swc.labs.servlet;

import ru.ssau.tk.swc.labs.dao.AnalFunDAO;
import ru.ssau.tk.swc.labs.dao.PostgreSQLDataSourceProvider;
import ru.ssau.tk.swc.labs.dto.AnalFunDTO;
import ru.ssau.tk.swc.labs.dto.CreateAnalFunDTO;
import ru.ssau.tk.swc.labs.entity.AnalFun;
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

@WebServlet("/api/anal-fun/*")
public class AnalFunServlet extends HttpServlet {
    private AnalFunDAO analFunDAO;
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(AnalFunServlet.class);

    @Override
    public void init() {
        logger.info("Инициализация AnalFunServlet");
        this.analFunDAO = new AnalFunDAO(new PostgreSQLDataSourceProvider());
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("GET запрос для аналитических функций. Path: {}", pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<AnalFun> functions = analFunDAO.findAll();
                List<AnalFunDTO> dtos = functions.stream()
                        .map(AnalFunDTO::fromEntity)
                        .collect(Collectors.toList());
                objectMapper.writeValue(resp.getWriter(), dtos);
                logger.info("Возвращено {} аналитических функций", dtos.size());
            } else {
                String idStr = pathInfo.substring(1);
                Long id = Long.parseLong(idStr);
                var function = analFunDAO.findByID(id);

                if (function.isPresent()) {
                    objectMapper.writeValue(resp.getWriter(), AnalFunDTO.fromEntity(function.get()));
                    logger.info("Аналитическая функция с ID {} найдена", id);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Функция не найдена\"}");
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка в doGet", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Внутренняя ошибка\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        logger.info("POST запрос для создания аналитической функции");

        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            CreateAnalFunDTO dto = objectMapper.readValue(body, CreateAnalFunDTO.class);

            AnalFun function = new AnalFun();
            function.setName(dto.getName());
            function.setType(dto.getType());

            Long newId = analFunDAO.create(function);

            if (newId != null) {
                AnalFun created = analFunDAO.findByID(newId).orElseThrow();
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getWriter(), AnalFunDTO.fromEntity(created));
                logger.info("Создана аналитическая функция с ID: {}", newId);
            } else {
                throw new Exception("Не удалось создать функцию");
            }
        } catch (Exception e) {
            logger.error("Ошибка создания функции", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка создания: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Не указан ID\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            boolean deleted = analFunDAO.delete(id);

            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                logger.info("Аналитическая функция с ID {} удалена", id);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Функция не найдена\"}");
            }
        } catch (Exception e) {
            logger.error("Ошибка удаления", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка удаления\"}");
        }
    }
}