package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;

@Tag(name = "Объявления")
@RequestMapping("/ads")
@RestController
public class AdController {

    @Operation(summary = "Получение всех объявлений", operationId = "getAllAds")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Ads.class)))
    @GetMapping
    public Ads getAllAds() {
        return new Ads();
    }

    @Operation(summary = "Добавление объявления", operationId = "addAd")
    @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = Ad.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Ad addAd(@ModelAttribute CreateOrUpdateAd properties, @RequestParam MultipartFile image) {
        return new Ad();
    }

    @Operation(summary = "Получение информации об объявлении", operationId = "getAds")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ExtendedAd.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("{id}")
    public ExtendedAd getAds(@PathVariable int id) {
        return new ExtendedAd();
    }

    @Operation(summary = "Удаление объявления", operationId = "removeAd")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void removeAd(@PathVariable int id) {

    }

    @Operation(summary = "Обновление информации об объявлении", operationId = "updateAds")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Ad.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PatchMapping("{id}")
    public CreateOrUpdateAd updateAds(@PathVariable int id) {
        return new CreateOrUpdateAd();
    }

    @Operation(summary = "Получение объявлений авторизованного пользователя", operationId = "getAdsMe")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Ads.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/me")
    public Ads getAdsMe() {
        return new Ads();
    }

    @Operation(summary = "Обновление картинки объявления", operationId = "updateImage")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PatchMapping(value = "{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public byte[] updateImage(@PathVariable int id, @RequestParam MultipartFile image) {
        return new byte[id];
    }
}
