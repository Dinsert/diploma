package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserEntityMapper;
import ru.skypro.homework.repository.UserEntityRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserEntityService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserEntityServiceImpl implements UserEntityService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityMapper userEntityMapper;
    private final ImageService imageService;

    @Transactional
    @Override
    public void setPassword(NewPassword newPassword, Authentication authentication) {
        UserEntity userEntity = userEntityRepository.findByUsername(authentication.getName()).orElseThrow();

        boolean matches = passwordEncoder.matches(newPassword.getCurrentPassword(), userEntity.getPassword());

        if (matches) {
            userEntity.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
            userEntityRepository.save(userEntity);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public User getUser(Authentication authentication) {
        UserEntity userEntity = userEntityRepository.findByUsername(authentication.getName()).orElseThrow();

        return userEntityMapper.toDTO(userEntity);
    }

    @Transactional
    @Override
    public UpdateUser updateUser(UpdateUser updateUser, Authentication authentication) {
        UserEntity userEntity = userEntityRepository.findByUsername(authentication.getName()).orElseThrow();

        userEntityMapper.updateUserEntity(updateUser, userEntity);
        userEntityRepository.save(userEntity);

        return updateUser;
    }

    @Transactional
    @Override
    public void updateUserImage(MultipartFile image, Authentication authentication) throws IOException {
        UserEntity userEntity = userEntityRepository.findByUsername(authentication.getName()).orElseThrow();
        String userEntityImage = userEntity.getImage();

        String imagePath = imageService.saveImage(image);
        userEntity.setImage(imagePath);
        userEntityRepository.save(userEntity);

        if (userEntityImage != null) {
            new Thread(()  -> {
                try {
                    Thread.sleep(800);
                    imageService.deleteImage(userEntityImage);
                } catch (IOException | InterruptedException ignored) {}
            }).start();
        }
    }
}
