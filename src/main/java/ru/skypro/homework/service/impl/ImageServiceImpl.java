package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.service.ImageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Сервис для управления изображениями.
 * Предоставляет методы для сохранения, получения и удаления изображений.
 */
@Service
public class ImageServiceImpl implements ImageService {

    /**
     * Путь до корневой папки проекта
     */
    private static final String IMAGE_DIRECTORY = System.getProperty("user.dir");

    /**
     * Часть пути, которое соединяется с именем файла для того, чтобы клиент
     * по этому адресу получал изображение при HTTP-запросе.
     * <p>
     * Здесь настраивается конфигурация этого пути.
     *
     * @see ru.skypro.homework.config.WebConfig
     */
    private static final String IMAGES = "/images/";

    /**
     * Путь до корневой папки изображений в проекте.
     */
    @Value("${path.dir.image}")
    private String partDir;

    /**
     * Сохраняет загруженное изображение и возвращает путь к нему.
     *
     * @param multipartFile файл изображения
     * @return путь до сохранённого приложения
     * @throws IOException если произошла ошибка при обработке изображения
     */
    @Override
    public String saveImage(MultipartFile multipartFile) throws IOException {
        File directory = new File(IMAGE_DIRECTORY + partDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        File dest = new File(IMAGE_DIRECTORY + partDir + fileName);

        multipartFile.transferTo(dest);

        return IMAGES + fileName;
    }

    /**
     * Получает изображение по указанному пути в виде массива байтов.
     *
     * @param filePath путь к файлу изображения
     * @return массив байт найденного изображения
     * @throws IOException если произошла ошибка при обработке изображения
     */
    @Override
    public byte[] getImage(String filePath) throws IOException {
        File file = new File(IMAGE_DIRECTORY + partDir + filePath.replace(IMAGES, ""));

        return Files.readAllBytes(file.toPath());
    }

    /**
     * Удаляет изображение по указанному пути.
     *
     * @param filePath путь к файлу изображения
     * @throws IOException если произошла ошибка при обработке изображения
     */
    @Override
    public void deleteImage(String filePath) throws IOException {
        Path path = Path.of(IMAGE_DIRECTORY + partDir + filePath.replace(IMAGES, ""));

        Files.deleteIfExists(path);
    }
}
