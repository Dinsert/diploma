package ru.skypro.homework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация веб-приложения для обработки статических ресурсов.
 * Определяет маршруты для доступа к загруженным изображениям, хранящимся в файловой системе.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Настраивает обработчики статических ресурсов для доступа к изображениям.
     * Маппит URL-путь `/images/**` на директорию с загруженными файлами в корневой директории проекта.
     *
     * @param registry реестр обработчиков ресурсов
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/images/");
    }
}