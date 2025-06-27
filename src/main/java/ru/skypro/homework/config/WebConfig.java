package ru.skypro.homework.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация веб-приложения для обработки статических ресурсов.
 * Определяет маршруты для доступа к загруженным изображениям, хранящимся в файловой системе.
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Путь до корневой папки изображений в проекте.
     */
    @Value("${path.dir.image}")
    private String imagePath;

    /**
     * Настраивает обработчики статических ресурсов для доступа к изображениям.
     * Маппит URL-путь `/images/**` на директорию с загруженными файлами в корневой директории проекта.
     *
     * @param registry реестр обработчиков ресурсов
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + (System.getenv("SPRING_DATASOURCE_URL") == null ? System.getProperty("user.dir") : "") + imagePath);
    }
}