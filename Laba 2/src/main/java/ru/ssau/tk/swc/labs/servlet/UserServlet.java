package ru.ssau.tk.swc.labs.servlet;

import ru.ssau.tk.swc.labs.dao.UserDAO;
import ru.ssau.tk.swc.labs.dao.PostgreSQLDataSourceProvider;
import ru.ssau.tk.swc.labs.dto.UserDTO;
import ru.ssau.tk.swc.labs.dto.CreateUserDTO;
import ru.ssau.tk.swc.labs.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    private UserDAO userDAO;
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);

    @Override
    public void init() throws ServletException {
        logger.info("Инициализация UserServlet");
        this.userDAO = new UserDAO(new PostgreSQLDataSourceProvider());
        this.objectMapper = new ObjectMapper();
        logger.info("UserServlet успешно инициализирован");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("GET запрос для пользователей. Path: {}", pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                logger.info("Получение всех пользователей");
                List<User> users = userDAO.findAll();
                List<UserDTO> userDTOs = users.stream()
                        .map(UserDTO::fromEntity)
                        .collect(Collectors.toList());

                String jsonResponse = objectMapper.writeValueAsString(userDTOs);
                resp.getWriter().write(jsonResponse);
                logger.info("Успешно возвращено {} пользователей", userDTOs.size());

            } else {
                String idStr = pathInfo.substring(1);
                logger.info("Получение пользователя по ID: {}", idStr);

                try {
                    Long id = Long.parseLong(idStr);
                    var user = userDAO.findByID(id);

                    if (user.isPresent()) {
                        UserDTO userDTO = UserDTO.fromEntity(user.get());
                        String jsonResponse = objectMapper.writeValueAsString(userDTO);
                        resp.getWriter().write(jsonResponse);
                        logger.info("Пользователь с ID {} найден", id);
                    } else {
                        logger.warn("Пользователь с ID {} не найден", id);
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write("{\"error\": \"Пользователь не найден\"}");
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Неверный формат ID: {}", idStr);
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\": \"Неверный формат ID\"}");
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка при обработке GET запроса", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Внутренняя ошибка сервера\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        logger.info("POST запрос для создания пользователя");

        try {
            String requestBody = req.getReader().lines().collect(Collectors.joining());
            logger.debug("Тело запроса: {}", requestBody);

            CreateUserDTO createUserDTO = objectMapper.readValue(requestBody, CreateUserDTO.class);
            logger.info("Создание пользователя: {}", createUserDTO.getLogin());

            User user = new User();
            user.setName(createUserDTO.getName());
            user.setLogin(createUserDTO.getLogin());
            user.setEmail(createUserDTO.getEmail());
            user.setPassword(createUserDTO.getPassword());

            Long newUserId = userDAO.create(user);

            if (newUserId != null) {
                User createdUser = userDAO.findByID(newUserId).orElseThrow();
                UserDTO userDTO = UserDTO.fromEntity(createdUser);

                String jsonResponse = objectMapper.writeValueAsString(userDTO);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(jsonResponse);
                logger.info("Пользователь создан с ID: {}", newUserId);
            } else {
                logger.error("Ошибка при создании пользователя");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"Ошибка при создании пользователя\"}");
            }

        } catch (Exception e) {
            logger.error("Ошибка при создании пользователя", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка при создании пользователя: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("PUT запрос для обновления пользователя. Path: {}", pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                logger.warn("PUT запрос без указания ID пользователя");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Не указан ID пользователя\"}");
                return;
            }

            String idStr = pathInfo.substring(1);
            logger.info("Обновление пользователя с ID: {}", idStr);

            Long id = Long.parseLong(idStr);

            var existingUser = userDAO.findByID(id);
            if (existingUser.isEmpty()) {
                logger.warn("Пользователь с ID {} не найден для обновления", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Пользователь не найден\"}");
                return;
            }

            String requestBody = req.getReader().lines().collect(Collectors.joining());
            UserDTO updateDTO = objectMapper.readValue(requestBody, UserDTO.class);

            User userToUpdate = existingUser.get();
            userToUpdate.setName(updateDTO.getName());
            userToUpdate.setLogin(updateDTO.getLogin());
            userToUpdate.setEmail(updateDTO.getEmail());

            boolean updated = userDAO.updateName(userToUpdate) &&
                    userDAO.updateLogin(userToUpdate) &&
                    userDAO.updateEmail(userToUpdate);

            if (updated) {
                User updatedUser = userDAO.findByID(id).orElseThrow();
                UserDTO userDTO = UserDTO.fromEntity(updatedUser);

                String jsonResponse = objectMapper.writeValueAsString(userDTO);
                resp.getWriter().write(jsonResponse);
                logger.info("Пользователь с ID {} успешно обновлен", id);
            } else {
                logger.error("Ошибка при обновлении пользователя с ID {}", id);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"Ошибка при обновлении пользователя\"}");
            }

        } catch (NumberFormatException e) {
            logger.warn("Неверный формат ID: {}", pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Неверный формат ID\"}");
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пользователя", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка при обновлении пользователя\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();
        logger.info("DELETE запрос для пользователя. Path: {}", pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                logger.warn("DELETE запрос без указания ID пользователя");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Не указан ID пользователя\"}");
                return;
            }

            String idStr = pathInfo.substring(1);
            logger.info("Удаление пользователя с ID: {}", idStr);

            Long id = Long.parseLong(idStr);
            boolean deleted = userDAO.delete(id);

            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                logger.info("Пользователь с ID {} успешно удален", id);
            } else {
                logger.warn("Пользователь с ID {} не найден для удаления", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Пользователь не найден\"}");
            }

        } catch (NumberFormatException e) {
            logger.warn("Неверный формат ID: {}", pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Неверный формат ID\"}");
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка при удалении пользователя\"}");
        }
    }
}