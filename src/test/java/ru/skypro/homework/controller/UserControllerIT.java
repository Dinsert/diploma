package ru.skypro.homework.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.util.UserFixture;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerIT {

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

    @Autowired
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        // Запускаем PostgreSQL
        postgres.start();

        // Настраиваем перехватчик у RestTemplate
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList((request, body, execution) ->
                execution.execute(request, body))
        );

        // Очищаем БД
        jdbcTemplate.execute("TRUNCATE TABLE comment_entities, ad_entities, user_entities RESTART IDENTITY");

        // Регистрируем пользователя
        Register register = UserFixture.getFirstRegisteredUser();
        restTemplate.postForEntity(url() + "/register", register, Void.class);
    }

    @Test
    void setPassword_SuccessfulChangedPassword_ReturnsOk() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword(UserFixture.getFirstRegisteredUser().getPassword());
        newPassword.setNewPassword(UserFixture.newPassword);

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.postForEntity(url() + "/users/set_password", newPassword, Void.class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void setPassword_IncorrectCurrentPassword_ReturnsForbidden() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword("wrongPassword");
        newPassword.setNewPassword(UserFixture.newPassword);

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.postForEntity(url() + "/users/set_password", newPassword, Void.class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void setPassword_WithoutAuthentication_ReturnsUnauthorized() {
        // Подготовка данных
        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword(UserFixture.getFirstRegisteredUser().getPassword());
        newPassword.setNewPassword(UserFixture.newPassword);

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.postForEntity(url() + "/users/set_password", newPassword, Void.class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getUser_SuccessfulRequest_ReturnsOk() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Выполняем запрос
        ResponseEntity<User> response = restTemplate.getForEntity(url() + "/users/me", User.class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo(UserFixture.getFirstRegisteredUser().getUsername());
    }

    @Test
    void getUser_WithoutAuthentication_ReturnsUnauthorized() {
        // Выполняем запрос
        ResponseEntity<User> response = restTemplate.getForEntity(url() + "/users/me", User.class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void updateUser_SuccessfulChangedUserData_ReturnsOk() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        UpdateUser updateUser = UserFixture.getUpdateUser();

        // Выполняем запрос
        ResponseEntity<UpdateUser> response = restTemplate.exchange(url() + "/users/me", HttpMethod.PATCH, new HttpEntity<>(updateUser), UpdateUser.class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFirstName()).isEqualTo(updateUser.getFirstName());
    }

    @Test
    void updateUser_WithoutAuthentication_ReturnsUnauthorized() {
        // Подготовка данных
        UpdateUser updateUser = UserFixture.getUpdateUser();

        // Выполняем запрос
        ResponseEntity<UpdateUser> response = restTemplate.exchange(url() + "/users/me", HttpMethod.PATCH, new HttpEntity<>(updateUser), UpdateUser.class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void updateUserImage_SuccessfulChangedUserImage_ReturnsOk() throws IOException {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource("src/test/resources/test.jpg"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.exchange(url() + "/users/me/image", HttpMethod.PATCH, requestEntity, Void.class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Удаляем добавленное изображение из ФС
        String imagePath = jdbcTemplate.queryForObject("SELECT image FROM user_entities WHERE username = ?", String.class,
                UserFixture.getFirstRegisteredUser().getUsername());
        imageService.deleteImage(imagePath);
    }

    @Test
    void updateUserImage_WithoutAuthentication_ReturnsUnauthorized() {
        // Подготовка данных
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource("src/test/resources/test.jpg"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.exchange(url() + "/users/me/image", HttpMethod.PATCH, requestEntity, Void.class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void returnUserImage_SuccessfulRequest_ReturnsOk() throws IOException {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource("src/test/resources/test.jpg"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        restTemplate.exchange(url() + "/users/me/image", HttpMethod.PATCH, requestEntity, Void.class);

        String imagePath = jdbcTemplate.queryForObject("SELECT image FROM user_entities WHERE username = ?", String.class,
                UserFixture.getFirstRegisteredUser().getUsername());
        byte[] image = imageService.getImage(imagePath);

        // Выполняем запрос
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url() + imagePath, byte[].class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(image);

        // Удаляем добавленное изображение
        imageService.deleteImage(imagePath);
    }

    private String url() {
        return "http://localhost:" + port;
    }

    private void doAuthenticationFirstUser() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders().add("Authorization", "Basic " + Base64.getEncoder()
                            .encodeToString((UserFixture.getFirstRegisteredUser().getUsername() + ":" + UserFixture.getFirstRegisteredUser().getPassword()).getBytes()));
                    return execution.execute(request, body);
                }));
    }
}