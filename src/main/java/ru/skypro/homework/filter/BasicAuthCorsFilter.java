package ru.skypro.homework.filter;


import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Фильтр для настройки кросс-доменных запросов (CORS) с поддержкой авторизации.
 * Добавляет заголовок `Access-Control-Allow-Credentials: true` к каждому ответу сервера,
 * чтобы разрешить фронтенду отправлять запросы с учетными данными (например, cookies или заголовки авторизации)
 * с другого домена (например, `http://localhost:3000` к `http://localhost:8080`).
 */
@Component
public class BasicAuthCorsFilter extends OncePerRequestFilter {

    /**
     * Обрабатывает каждый входящий HTTP-запрос, добавляя необходимые заголовки CORS.
     *
     * @param httpServletRequest  запрос от клиента
     * @param httpServletResponse ответ, отправляемый клиенту
     * @param filterChain         цепочка фильтров для передачи управления дальше
     * @throws ServletException если произошла ошибка обработки запроса
     * @throws IOException      если произошла ошибка ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
