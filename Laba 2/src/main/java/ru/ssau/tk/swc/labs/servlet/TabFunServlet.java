package ru.ssau.tk.swc.labs.servlet;

import ru.ssau.tk.swc.labs.dao.TabFunDAO;
import ru.ssau.tk.swc.labs.dao.PostgreSQLDataSourceProvider;
import ru.ssau.tk.swc.labs.dto.TabFunDTO;
import ru.ssau.tk.swc.labs.dto.CreateTabFunDTO;
import ru.ssau.tk.swc.labs.entity.TabFun;
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

@WebServlet("/api/tab-fun/*")
public class TabFunServlet extends HttpServlet {
    private TabFunDAO tabFunDAO;
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(TabFunServlet.class);

    @Override
    public void init() {
        logger.info("Инициализация TabFunServlet");
        this.tabFunDAO = new TabFunDAO(new PostgreSQLDataSourceProvider());
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<TabFun> functions = tabFunDAO.findAll();
                List<TabFunDTO> dtos = functions.stream()
                        .map(TabFunDTO::fromEntity)
                        .collect(Collectors.toList());
                objectMapper.writeValue(resp.getWriter(), dtos);
                logger.info("Возвращено {} табулированных функций", dtos.size());
            } else {
                Long id = Long.parseLong(pathInfo.substring(1));
                var function = tabFunDAO.findByID(id);

                if (function.isPresent()) {
                    objectMapper.writeValue(resp.getWriter(), TabFunDTO.fromEntity(function.get()));
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

        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            CreateTabFunDTO dto = objectMapper.readValue(body, CreateTabFunDTO.class);

            TabFun function = new TabFun();
            function.setType(dto.getType());

            Long newId = tabFunDAO.create(function);

            if (newId != null) {
                TabFun created = tabFunDAO.findByID(newId).orElseThrow();
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getWriter(), TabFunDTO.fromEntity(created));
                logger.info("Создана табулированная функция с ID: {}", newId);
            }
        } catch (Exception e) {
            logger.error("Ошибка создания", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка создания\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Не указан ID\"}");
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            boolean deleted = tabFunDAO.delete(id);

            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                logger.info("Табулированная функция с ID {} удалена", id);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Ошибка удаления", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
