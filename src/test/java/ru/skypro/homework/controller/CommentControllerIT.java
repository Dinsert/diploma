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
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.util.AdFixture;
import ru.skypro.homework.util.CommentFixer;
import ru.skypro.homework.util.UserFixture;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CommentControllerIT {

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
    void setUp() throws IOException {
        // Запускаем PostgreSQL
        postgres.start();

        // Настраиваем перехватчик у RestTemplate
        RestTemplate testRestTemplate = restTemplate.getRestTemplate();
        testRestTemplate.setInterceptors(Collections.singletonList((request, body, execution) ->
                execution.execute(request, body)));

        // Очищаем БД
        jdbcTemplate.execute("TRUNCATE TABLE comment_entities, ad_entities, user_entities RESTART IDENTITY");

        // Регистрируем пользователя
        Register register = UserFixture.getFirstRegisteredUser();
        restTemplate.postForEntity(url() + "/register", register, Void.class);

        // Устанавливаем заголовок аутентификации
        testRestTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Basic " + Base64.getEncoder()
                    .encodeToString((register.getUsername() + ":" + register.getPassword()).getBytes()));
            return execution.execute(request, body);
        }));

        // Создаём объявление
        CreateOrUpdateAd createAd = AdFixture.getCreateAd();
        HttpHeaders headersForProperties = new HttpHeaders();
        headersForProperties.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("properties", new HttpEntity<>(createAd, headersForProperties));
        requestBody.add("image", new FileSystemResource("src/test/resources/test.jpg"));

        HttpHeaders generalHeaders = new HttpHeaders();
        generalHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, generalHeaders);

        ResponseEntity<Ad> response = restTemplate.exchange(url() + "/ads", HttpMethod.POST, requestEntity, Ad.class);

        // Получаем ссылку на добавленное изображение и удаляем его по этому пути из ФС
        String imagePath = Objects.requireNonNull(response.getBody()).getImage();
        imageService.deleteImage(imagePath);

        // Создаём комментарий
        CreateOrUpdateComment createComment = CommentFixer.getCreateComment();

        restTemplate.postForEntity(url() + "/ads/{id}/comments", createComment, Comment.class, AdFixture.adId);

        // Очищаем заголовки в перехватчике запросов
        testRestTemplate.setInterceptors(Collections.singletonList((request, body, execution) ->
                execution.execute(request, body)));
    }

    @Test
    void getComments_SuccessfulRequest_ReturnsOk() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Выполняем запрос
        ResponseEntity<Comments> response = restTemplate.getForEntity(url() + "/ads/{id}/comments", Comments.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResults().get(0).getText()).isEqualTo(CommentFixer.getCreateComment().getText());
    }

    @Test
    void getComments_IncorrectAdId_ReturnsNotFound() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Выполняем запрос
        ResponseEntity<Comments> response = restTemplate.getForEntity(url() + "/ads/{id}/comments", Comments.class, AdFixture.incorrectAdId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getComments_WithoutAuthentication_ReturnsUnauthorized() {
        // Выполняем запрос
        ResponseEntity<Comments> response = restTemplate.getForEntity(url() + "/ads/{id}/comments", Comments.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void addComment_SuccessfulCommentAdded_ReturnsOk() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        CreateOrUpdateComment createComment = CommentFixer.getCreateComment();

        // Выполняем запрос
        ResponseEntity<Comment> response = restTemplate.postForEntity(url() + "/ads/{id}/comments", createComment, Comment.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getText()).isEqualTo(createComment.getText());
    }

    @Test
    void addComment_IncorrectAdId_ReturnsNotFound() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        CreateOrUpdateComment createComment = CommentFixer.getCreateComment();

        // Выполняем запрос
        ResponseEntity<Comment> response = restTemplate.postForEntity(url() + "/ads/{id}/comments", createComment, Comment.class, AdFixture.incorrectAdId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void addComment_WithoutAuthentication_ReturnsUnauthorized() {
        // Подготовка данных
        CreateOrUpdateComment createComment = CommentFixer.getCreateComment();

        // Выполняем запрос
        ResponseEntity<Comment> response = restTemplate.postForEntity(url() + "/ads/{id}/comments", createComment, Comment.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void deleteComment_SuccessfulCommentRemoved_ReturnsOk() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.
                exchange(url() + "/ads/{adId}/comments/{commentId}", HttpMethod.DELETE, null, Void.class, AdFixture.adId, CommentFixer.commentId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteComment_IncorrectAdId_ReturnsNotFound() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.
                exchange(url() + "/ads/{adId}/comments/{commentId}", HttpMethod.DELETE, null, Void.class, AdFixture.incorrectAdId, CommentFixer.commentId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteComment_IncorrectCommentId_ReturnsNotFound() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.
                exchange(url() + "/ads/{adId}/comments/{commentId}", HttpMethod.DELETE, null, Void.class, AdFixture.adId, CommentFixer.incorrectCommentId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteComment_WithoutAuthentication_ReturnsUnauthorized() {
        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.
                exchange(url() + "/ads/{adId}/comments/{commentId}", HttpMethod.DELETE, null, Void.class, AdFixture.adId, CommentFixer.commentId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void deleteComment_ForbiddenCommentDeletedForUser_ReturnsForbidden() {
        // Подготовка данных
        Register register = UserFixture.getSecondRegisteredUser();

        restTemplate.postForEntity(url() + "/register", register, Void.class);

        // Аутентификация
        doAuthenticationSecondUserOrAdmin(register);

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.
                exchange(url() + "/ads/{adId}/comments/{commentId}", HttpMethod.DELETE, null, Void.class, AdFixture.adId, CommentFixer.commentId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void deleteComment_SuccessfulCommentDeletedForAdmin_ReturnsOk() {
        // Подготовка данных
        Register register = UserFixture.getRegisteredAdmin();

        restTemplate.postForEntity(url() + "/register", register, Void.class);

        // Аутентификация
        doAuthenticationSecondUserOrAdmin(register);

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.
                exchange(url() + "/ads/{adId}/comments/{commentId}", HttpMethod.DELETE, null, Void.class, AdFixture.adId, CommentFixer.commentId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void updateComment_SuccessfulCommentUpdated_ReturnsOk() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        CreateOrUpdateComment updateComment = CommentFixer.getUpdateComment();

        // Выполняем запрос
        ResponseEntity<Comment> response = restTemplate.exchange(url() + "/ads/{adId}/comments/{commentId}", HttpMethod.PATCH,
                new HttpEntity<>(updateComment), Comment.class, AdFixture.adId, CommentFixer.commentId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getText()).isEqualTo(updateComment.getText());
    }

    @Test
    void updateComment_IncorrectAdId_ReturnsNotFound() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        CreateOrUpdateComment updateComment = CommentFixer.getUpdateComment();

        // Выполняем запрос
        ResponseEntity<Comment> response = restTemplate.exchange(url() + "/ads/{adId}/comments/{commentId}", HttpMethod.PATCH,
                new HttpEntity<>(updateComment), Comment.class, AdFixture.incorrectAdId, CommentFixer.commentId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateComment_IncorrectCommentId_ReturnsNotFound() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        CreateOrUpdateComment updateComment = CommentFixer.getUpdateComment();

        // Выполняем запрос
        ResponseEntity<Comment> response = restTemplate.exchange(url() + "/ads/{adId}/comments/{commentId}", HttpMethod.PATCH,
                new HttpEntity<>(updateComment), Comment.class, AdFixture.adId, CommentFixer.incorrectCommentId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateComment_WithoutAuthentication_ReturnsUnauthorized() {
        // Подготовка данных
        CreateOrUpdateComment updateComment = CommentFixer.getUpdateComment();

        // Выполняем запрос
        ResponseEntity<Comment> response = restTemplate.exchange(url() + "/ads/{adId}/comments/{commentId}", HttpMethod.PATCH,
                new HttpEntity<>(updateComment), Comment.class, AdFixture.adId, CommentFixer.commentId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void updateComment_ForbiddenCommentUpdatedForUser_ReturnsForbidden() {
        // Подготовка данных
        Register register = UserFixture.getSecondRegisteredUser();

        restTemplate.postForEntity(url() + "/register", register, Void.class);

        CreateOrUpdateComment updateComment = CommentFixer.getUpdateComment();

        // Аутентификация
        doAuthenticationSecondUserOrAdmin(register);

        // Выполняем запрос
        ResponseEntity<Comment> response = restTemplate.exchange(url() + "/ads/{adId}/comments/{commentId}", HttpMethod.PATCH,
                new HttpEntity<>(updateComment), Comment.class, AdFixture.adId, CommentFixer.commentId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void updateComment_SuccessfulCommentUpdatedForAdmin_ReturnsOk() {
        // Подготовка данных
        Register register = UserFixture.getRegisteredAdmin();

        restTemplate.postForEntity(url() + "/register", register, Void.class);

        CreateOrUpdateComment updateComment = CommentFixer.getUpdateComment();

        // Аутентификация
        doAuthenticationSecondUserOrAdmin(register);

        // Выполняем запрос
        ResponseEntity<Comment> response = restTemplate.exchange(url() + "/ads/{adId}/comments/{commentId}", HttpMethod.PATCH,
                new HttpEntity<>(updateComment), Comment.class, AdFixture.adId, CommentFixer.commentId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getText()).isEqualTo(updateComment.getText());
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

    private void doAuthenticationSecondUserOrAdmin(Register register) {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders().add("Authorization", "Basic " + Base64.getEncoder()
                            .encodeToString((register.getUsername() + ":" + register.getPassword()).getBytes()));
                    return execution.execute(request, body);
                }));
    }
}