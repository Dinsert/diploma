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

@Service
public class ImageServiceImpl implements ImageService {

    private static final String IMAGE_DIRECTORY = System.getProperty("user.dir");
    private static final String IMAGES = "/images/";

    @Value("${path.dir.image}")
    private String partDir;

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

    @Override
    public byte[] getImage(String filePath) throws IOException {
        File file = new File(IMAGE_DIRECTORY + partDir + filePath.replace(IMAGES, ""));

        return Files.readAllBytes(file.toPath());
    }

    @Override
    public void deleteImage(String filePath) throws IOException {
        Path path = Path.of(IMAGE_DIRECTORY + partDir + filePath.replace(IMAGES, ""));

        Files.deleteIfExists(path);
    }
}
