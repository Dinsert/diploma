package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Интерфейс сервиса для работы с изображениями.
 * Предоставляет методы для сохранения, получения и удаления изображений.
 */
public interface ImageService {

    /**
     * Сохраняет загруженное изображение и возвращает путь к нему.
     *
     * @param multipartFile файл изображения
     * @return путь к сохраненному изображению
     * @throws IOException если произошла ошибка при сохранении
     */
    String saveImage(MultipartFile multipartFile) throws IOException;

    /**
     * Получает изображение по указанному пути в виде массива байтов.
     *
     * @param filePath путь к изображению
     * @return массив байтов изображения
     * @throws IOException если произошла ошибка при чтении
     */
    byte[] getImage(String filePath) throws IOException;

    /**
     * Удаляет изображение по указанному пути.
     *
     * @param filePath путь к изображению
     * @throws IOException если произошла ошибка при удалении
     */
    void deleteImage(String filePath) throws IOException;
}