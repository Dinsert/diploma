package ru.skypro.homework.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.util.UserFixture;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerIT {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        postgres.start();

        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList((request, body, execution) ->
                execution.execute(request, body))
        );

        jdbcTemplate.execute("TRUNCATE TABLE comment_entities, ad_entities, user_entities RESTART IDENTITY");
    }

    @Test
    void register_SuccessfulRegistration_ReturnsCreated() {
        // Подготовка данных
        Register register = UserFixture.getFirstRegisteredUser();

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.postForEntity(
                url() + "/register",
                register,
                Void.class
        );

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void register_InvalidData_ReturnsBadRequest() {
        // Подготовка данных с некорректными данными (например, пустой username)
        Register register = UserFixture.getFirstRegisteredUser();
        register.setUsername(""); // Некорректный username

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.postForEntity(
                url() + "/register",
                register,
                Void.class
        );

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void login_SuccessfulLogin_ReturnsOk() {
        // Подготовка данных
        Login login = new Login();
        login.setUsername(UserFixture.getFirstRegisteredUser().getUsername());
        login.setPassword(UserFixture.getFirstRegisteredUser().getPassword());

        // Эмулируем регистрацию перед логином
        Register register = UserFixture.getFirstRegisteredUser();
        restTemplate.postForEntity(url() + "/register", register, Void.class);

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.postForEntity(
                url() + "/login",
                login,
                Void.class
        );

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() {
        // Подготовка данных с неверными учетными данными
        Login login = new Login();
        login.setUsername(UserFixture.getFirstRegisteredUser().getUsername());
        login.setPassword("wrongpassword");

        // Эмулируем регистрацию перед логином
        Register register = UserFixture.getFirstRegisteredUser();
        restTemplate.postForEntity(url() + "/register", register, Void.class);

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.postForEntity(
                url() + "/login",
                login,
                Void.class
        );

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private String url() {
        return "http://localhost:" + port;
    }
}