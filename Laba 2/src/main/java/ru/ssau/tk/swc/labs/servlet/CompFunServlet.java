package ru.ssau.tk.swc.labs.servlet;

import ru.ssau.tk.swc.labs.dao.CompFunDAO;
import ru.ssau.tk.swc.labs.dao.PostgreSQLDataSourceProvider;
import ru.ssau.tk.swc.labs.dto.CompFunDTO;
import ru.ssau.tk.swc.labs.dto.CreateCompFunDTO;
import ru.ssau.tk.swc.labs.entity.CompFun;
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

public class CompFunServlet extends HttpServlet {
    private CompFunDAO compFunDAO;
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(CompFunServlet.class);

    @Override
    public void init() {
        logger.info("Инициализация CompFunServlet");
        this.compFunDAO = new CompFunDAO(new PostgreSQLDataSourceProvider());
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("GET запрос для композитных функций. Path: {}", pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<CompFun> functions = compFunDAO.findAll();
                List<CompFunDTO> dtos = functions.stream()
                        .map(CompFunDTO::fromEntity)
                        .collect(Collectors.toList());
                objectMapper.writeValue(resp.getWriter(), dtos);
                logger.info("Возвращено {} композитных функций", dtos.size());
            } else {
                Long id = Long.parseLong(pathInfo.substring(1));
                var function = compFunDAO.findByID(id);

                if (function.isPresent()) {
                    objectMapper.writeValue(resp.getWriter(), CompFunDTO.fromEntity(function.get()));
                    logger.info("Композитная функция с ID {} найдена", id);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Композитная функция не найдена\"}");
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка в doGet композитных функций", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        logger.info("POST запрос для создания композитной функции");

        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            CreateCompFunDTO dto = objectMapper.readValue(body, CreateCompFunDTO.class);

            CompFun function = new CompFun();
            function.setName(dto.getName());

            Long newId = compFunDAO.create(function);

            if (newId != null) {
                CompFun created = compFunDAO.findByID(newId).orElseThrow();
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getWriter(), CompFunDTO.fromEntity(created));
                logger.info("Создана композитная функция с ID: {}", newId);
            } else {
                throw new Exception("Не удалось создать композитную функцию");
            }
        } catch (Exception e) {
            logger.error("Ошибка создания композитной функции", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка создания композитной функции\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("DELETE запрос для композитной функции. Path: {}", pathInfo);

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Не указан ID композитной функции\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            boolean deleted = compFunDAO.delete(id);

            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                logger.info("Композитная функция с ID {} удалена", id);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Композитная функция не найдена\"}");
            }
        } catch (Exception e) {
            logger.error("Ошибка удаления композитной функции", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка удаления композитной функции\"}");
        }
    }
}