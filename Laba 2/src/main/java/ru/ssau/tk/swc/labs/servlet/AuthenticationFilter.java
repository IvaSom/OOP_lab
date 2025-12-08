package ru.ssau.tk.swc.labs.servlet;

import ru.ssau.tk.swc.labs.dao.UserDAO;
import ru.ssau.tk.swc.labs.dao.PostgreSQLDataSourceProvider;
import ru.ssau.tk.swc.labs.entity.User;
import ru.ssau.tk.swc.labs.entity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private UserDAO userDAO;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Инициализация AuthenticationFilter");
        this.userDAO = new UserDAO(new PostgreSQLDataSourceProvider());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        logger.info("Запрос к {} метод {}", path, httpRequest.getMethod());

        if (isPublicEndpoint(path)) {
            logger.debug("Публичный эндпоинт, пропускаем аутентификацию");
            chain.doFilter(request, response);
            return;
        }

        // Проверяем Basic Auth заголовок
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            logger.warn("Запрос без заголовка Authorization к защищенному эндпоинту: {}", path);
            sendUnauthorized(httpResponse, "Требуется аутентификация");
            return;
        }

        String encodedCredentials = authHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(encodedCredentials));
        String[] parts = credentials.split(":", 2);

        if (parts.length != 2) {
            logger.warn("Неверный формат Basic Auth для запроса: {}", path);
            sendUnauthorized(httpResponse, "Неверный формат аутентификации");
            return;
        }

        String login = parts[0];
        String password = parts[1];

        Optional<User> userOpt = userDAO.findByLoginAndPassword(login, password);
        if (userOpt.isEmpty()) {
            logger.warn("Неудачная аутентификация для пользователя: {}", login);
            sendUnauthorized(httpResponse, "Неверный логин или пароль");
            return;
        }

        User user = userOpt.get();
        logger.info("Пользователь {} успешно аутентифицирован. Роль: {}", login, user.getRole());

        if (!hasAccess(user.getRole(), path, httpRequest.getMethod())) {
            logger.warn("Пользователь {} (роль: {}) пытается получить доступ к запрещенному ресурсу: {}",
                    login, user.getRole(), path);
            sendForbidden(httpResponse);
            return;
        }

        httpRequest.setAttribute("user", user);
        httpRequest.setAttribute("userRole", user.getRole());
        httpRequest.setAttribute("userId", user.getId());

        logger.debug("Доступ разрешен для пользователя {} к {}", login, path);
        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/users") && (
                path.equals("/api/users") ||
                        path.equals("/api/users/") ||
                        path.matches("/api/users/\\d+") // GET по ID тоже публичный
        );
    }

    private boolean hasAccess(Role role, String path, String method) {
        if (role == Role.ADMIN) {
            return true;
        }
        if (method.equals("GET")) {
            return true;
        }
        if (method.equals("DELETE") || method.equals("PUT") || method.equals("POST")) {
            return path.startsWith("/api/users/");
        }

        return false;
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
        response.setHeader("WWW-Authenticate", "Basic realm=\"Lab6 API\"");
    }

    private void sendForbidden(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"Доступ запрещен\"}");
    }

    @Override
    public void destroy() {
        logger.info("AuthenticationFilter уничтожен");
    }
}
