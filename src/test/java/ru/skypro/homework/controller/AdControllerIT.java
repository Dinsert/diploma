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
import ru.skypro.homework.util.UserFixture;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AdControllerIT {

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

        // Очищаем заголовки в перехватчике запросов
        testRestTemplate.setInterceptors(Collections.singletonList((request, body, execution) ->
                execution.execute(request, body)));
    }

    @Test
    void getAllAds_SuccessfulRequest_ReturnsOk() {
        // Выполняем запрос
        ResponseEntity<Ads> response = restTemplate.getForEntity(url() + "/ads", Ads.class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResults().get(0).getTitle()).isEqualTo(AdFixture.getCreateAd().getTitle());
    }

    @Test
    void addAd_SuccessfulAdding_ReturnsCreated() throws IOException {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        CreateOrUpdateAd createAd = AdFixture.getCreateAd();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("properties", new HttpEntity<>(createAd, headers1));
        body.add("image", new FileSystemResource("src/test/resources/test.jpg"));

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers2);

        // Выполняем запрос
        ResponseEntity<Ad> response = restTemplate.exchange(url() + "/ads", HttpMethod.POST, requestEntity, Ad.class);

        // Удаляем добавленное изображение
        String imagePath = Objects.requireNonNull(response.getBody()).getImage();
        imageService.deleteImage(imagePath);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo(AdFixture.getCreateAd().getTitle());
    }

    @Test
    void addAd_WithoutAuthentication_ReturnsUnauthorized() {
        // Подготовка данных
        CreateOrUpdateAd createAd = AdFixture.getCreateAd();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("properties", new HttpEntity<>(createAd, headers1));
        body.add("image", new FileSystemResource("src/test/resources/test.jpg"));

        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers2);

        // Выполняем запрос
        ResponseEntity<Ad> response = restTemplate.exchange(url() + "/ads", HttpMethod.POST, requestEntity, Ad.class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getAds_SuccessfulRequest_ReturnsOk() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Выполняем запрос
        ResponseEntity<ExtendedAd> response = restTemplate.getForEntity(url() + "/ads/{id}", ExtendedAd.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDescription()).isEqualTo(AdFixture.getCreateAd().getDescription());
    }

    @Test
    void getAds_IncorrectAdId_ReturnsNotFound() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Выполняем запрос
        ResponseEntity<ExtendedAd> response = restTemplate.getForEntity(url() + "/ads/{id}", ExtendedAd.class, AdFixture.incorrectAdId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAds_WithoutAuthentication_ReturnsUnauthorized() {
        // Выполняем запрос
        ResponseEntity<ExtendedAd> response = restTemplate.getForEntity(url() + "/ads/{id}", ExtendedAd.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void removeAd_SuccessfulDeleted_ReturnsNoContent() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.exchange(url() + "/ads/{id}", HttpMethod.DELETE, null, Void.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void removeAd_IncorrectAdId_ReturnsNotFound() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.exchange(url() + "/ads/{id}", HttpMethod.DELETE, null, Void.class, AdFixture.incorrectAdId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void removeAd_WithoutAuthentication_ReturnsUnauthorized() {
        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.exchange(url() + "/ads/{id}", HttpMethod.DELETE, null, Void.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void removeAd_ForbiddenAdDeletedForUser_ReturnsForbidden() {
        // Подготовка данных
        Register register = UserFixture.getSecondRegisteredUser();

        restTemplate.postForEntity(url() + "/register", register, Void.class);

        // Аутентификация
        doAuthenticationSecondUserOrAdmin(register);

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.exchange(url() + "/ads/{id}", HttpMethod.DELETE, null, Void.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void removeAd_SuccessfulAdDeletedForAdmin_ReturnsNoContent() {
        // Подготовка данных
        Register register = UserFixture.getRegisteredAdmin();

        restTemplate.postForEntity(url() + "/register", register, Void.class);

        // Аутентификация
        doAuthenticationSecondUserOrAdmin(register);

        // Выполняем запрос
        ResponseEntity<Void> response = restTemplate.exchange(url() + "/ads/{id}", HttpMethod.DELETE, null, Void.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void updateAds_SuccessfulAdUpdated_ReturnsOk() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        CreateOrUpdateAd updateAd = AdFixture.getUpdateAd();

        // Выполняем запрос
        ResponseEntity<Ad> response = restTemplate.exchange(url() + "/ads/{id}", HttpMethod.PATCH, new HttpEntity<>(updateAd), Ad.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo(updateAd.getTitle());
    }

    @Test
    void updateAds_IncorrectAdId_ReturnsNotFound() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        CreateOrUpdateAd updateAd = AdFixture.getUpdateAd();

        // Выполняем запрос
        ResponseEntity<Ad> response = restTemplate.exchange(url() + "/ads/{id}", HttpMethod.PATCH, new HttpEntity<>(updateAd), Ad.class, AdFixture.incorrectAdId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateAds_WithoutAuthentication_ReturnsUnauthorized() {
        // Подготовка данных
        CreateOrUpdateAd updateAd = AdFixture.getUpdateAd();

        // Выполняем запрос
        ResponseEntity<Ad> response = restTemplate.exchange(url() + "/ads/{id}", HttpMethod.PATCH, new HttpEntity<>(updateAd), Ad.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void updateAds_ForbiddenAdUpdateForUser_ReturnsForbidden() {
        // Подготовка данных
        Register register = UserFixture.getSecondRegisteredUser();

        restTemplate.postForEntity(url() + "/register", register, Void.class);

        CreateOrUpdateAd updateAd = AdFixture.getUpdateAd();

        // Аутентификация
        doAuthenticationSecondUserOrAdmin(register);

        // Выполняем запрос
        ResponseEntity<Ad> response = restTemplate.exchange(url() + "/ads/{id}", HttpMethod.PATCH, new HttpEntity<>(updateAd), Ad.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void updateAds_SuccessfulAdUpdatedForAdmin_ReturnsNoContent() {
        // Подготовка данных
        Register register = UserFixture.getRegisteredAdmin();

        restTemplate.postForEntity(url() + "/register", register, Void.class);

        CreateOrUpdateAd updateAd = AdFixture.getUpdateAd();

        // Аутентификация
        doAuthenticationSecondUserOrAdmin(register);

        // Выполняем запрос
        ResponseEntity<Ad> response = restTemplate.exchange(url() + "/ads/{id}", HttpMethod.PATCH, new HttpEntity<>(updateAd), Ad.class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo(updateAd.getTitle());
    }

    @Test
    void getAdsMe_SuccessfulRequest_ReturnsOk() {
        // Аутентификация
        doAuthenticationFirstUser();

        // Выполняем запрос
        ResponseEntity<Ads> response = restTemplate.getForEntity(url() + "/ads/me", Ads.class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResults().get(0).getTitle()).isEqualTo(AdFixture.getCreateAd().getTitle());
    }

    @Test
    void getAdsMe_WithoutAuthentication_ReturnsUnauthorized() {
        // Выполняем запрос
        ResponseEntity<Ads> response = restTemplate.getForEntity(url() + "/ads/me", Ads.class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void updateImage_SuccessfulImageAdUpdated_ReturnsOk() throws IOException {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource("src/test/resources/test.jpg"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Выполняем запрос
        ResponseEntity<byte[]> response = restTemplate.exchange(url() + "/ads/{id}/image", HttpMethod.PATCH, new HttpEntity<>(body, headers),
                byte[].class, AdFixture.adId);

        // Получаем из БД адрес изображения объявления и получаем её массив байт
        String imagePath = jdbcTemplate.queryForObject("SELECT image FROM ad_entities WHERE pk = ?", String.class, AdFixture.adId);
        byte[] image = imageService.getImage(imagePath);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(image);

        // Удаляем добавленное изображение из ФС
        imageService.deleteImage(imagePath);
    }

    @Test
    void updateImage_IncorrectAdId_ReturnsNotFound() throws IOException {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource("src/test/resources/test.jpg"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Выполняем запрос
        ResponseEntity<byte[]> response = restTemplate.exchange(url() + "/ads/{id}/image", HttpMethod.PATCH, new HttpEntity<>(body, headers),
                byte[].class, AdFixture.incorrectAdId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateImage_WithoutAuthentication_ReturnsUnauthorized() throws IOException {
        // Подготовка данных
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource("src/test/resources/test.jpg"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Выполняем запрос
        ResponseEntity<byte[]> response = restTemplate.exchange(url() + "/ads/{id}/image", HttpMethod.PATCH, new HttpEntity<>(body, headers),
                byte[].class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void updateImage_ForbiddenImageAdUpdatedForUser_ReturnsForbidden() {
        // Подготовка данных
        Register register = UserFixture.getSecondRegisteredUser();

        restTemplate.postForEntity(url() + "/register", register, Void.class);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource("src/test/resources/test.jpg"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Аутентификация
        doAuthenticationSecondUserOrAdmin(register);

        // Выполняем запрос
        ResponseEntity<byte[]> response = restTemplate.exchange(url() + "/ads/{id}/image", HttpMethod.PATCH, new HttpEntity<>(body, headers),
                byte[].class, AdFixture.adId);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void updateImage_SuccessfulAdUpdatedForAdmin_ReturnsOk() throws IOException {
        // Подготовка данных
        Register register = UserFixture.getRegisteredAdmin();

        restTemplate.postForEntity(url() + "/register", register, Void.class);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource("src/test/resources/test.jpg"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Аутентификация
        doAuthenticationSecondUserOrAdmin(register);

        // Выполняем запрос
        ResponseEntity<byte[]> response = restTemplate.exchange(url() + "/ads/{id}/image", HttpMethod.PATCH, new HttpEntity<>(body, headers),
                byte[].class, AdFixture.adId);

        // Получаем из БД адрес изображения объявления и получаем её массив байт
        String imagePath = jdbcTemplate.queryForObject("SELECT image FROM ad_entities WHERE pk = ?", String.class, AdFixture.adId);
        byte[] image = imageService.getImage(imagePath);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(image);

        // Удаляем добавленное изображение из ФС
        imageService.deleteImage(imagePath);
    }

    @Test
    void returnAdImage_SuccessfulRequest_ReturnsOk() throws IOException {
        // Аутентификация
        doAuthenticationFirstUser();

        // Подготовка данных
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource("src/test/resources/test.jpg"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        restTemplate.exchange(url() + "/ads/{id}/image", HttpMethod.PATCH, new HttpEntity<>(body, headers),
                byte[].class, AdFixture.adId);

        String imagePath = jdbcTemplate.queryForObject("SELECT image FROM ad_entities WHERE pk = ?", String.class, AdFixture.adId);
        byte[] image = imageService.getImage(imagePath);

        // Выполняем запрос
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url() + imagePath, byte[].class);

        // Проверяем результат
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(image);

        // Удаляем добавленное изображение из ФС
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

    private void doAuthenticationSecondUserOrAdmin(Register register) {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders().add("Authorization", "Basic " + Base64.getEncoder()
                            .encodeToString((register.getUsername() + ":" + register.getPassword()).getBytes()));
                    return execution.execute(request, body);
                }));
    }
}