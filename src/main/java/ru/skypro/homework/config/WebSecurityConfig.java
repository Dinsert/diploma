package ru.skypro.homework.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.skypro.homework.dto.ErrorResponseDTO;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Конфигурация безопасности приложения.
 * Настраивает аутентификацию, авторизацию, CORS и обработку исключений безопасности.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final ObjectMapper objectMapper;

    /**
     * Список путей, которые не требуют аутентификации.
     */
    private static final String[] AUTH_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/login",
            "/register"
    };

    /**
     * Создает менеджер пользователей на основе JDBC для работы с базой данных.
     *
     * @param dataSource источник данных для подключения к базе
     * @return менеджер пользователей
     */
    @Bean
    public JdbcUserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery(
                "SELECT username, password, 1 as enabled FROM user_entities WHERE username = ?"
        );
        manager.setAuthoritiesByUsernameQuery(
                "SELECT username, authority FROM user_entities WHERE username = ?"
        );
        return manager;
    }

    /**
     * Настраивает цепочку безопасности для обработки HTTP-запросов.
     *
     * @param http объект конфигурации безопасности
     * @return сконфигурированная цепочка безопасности
     * @throws Exception если произошла ошибка конфигурации
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(HttpMethod.GET, "/ads").permitAll()
                                .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                                .requestMatchers(AUTH_WHITELIST).permitAll()
                                .anyRequest().authenticated())
                .httpBasic(withDefaults())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(this::authenticationEntryPoint)
                        .accessDeniedHandler(this::accessDeniedHandler));
        return http.build();
    }

    /**
     * Создает источник конфигурации CORS для обработки кросс-доменных запросов.
     *
     * @return источник конфигурации CORS
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Создает кодировщик паролей для безопасного хранения.
     *
     * @return экземпляр кодировщика паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Обрабатывает случай, когда требуется аутентификация.
     *
     * @param request       запрос от клиента
     * @param response      ответ клиенту
     * @param authException исключение аутентификации
     * @throws IOException если произошла ошибка ввода-вывода
     */
    private void authenticationEntryPoint(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Требуется аутентификация.",
                request.getRequestURI()
        );
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * Обрабатывает случай, когда доступ запрещен.
     *
     * @param request               запрос от клиента
     * @param response              ответ клиенту
     * @param accessDeniedException исключение доступа
     * @throws IOException если произошла ошибка ввода-вывода
     */
    private void accessDeniedHandler(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "У вас недостаточно прав для выполнения этого действия.",
                request.getRequestURI()
        );
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
