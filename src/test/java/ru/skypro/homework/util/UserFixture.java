package ru.skypro.homework.util;

import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UpdateUser;

public class UserFixture {

    public static final String newPassword = "newPassword";

    public static Register getFirstRegisteredUser() {
        Register register = new Register();
        register.setUsername("user1@mail.ru");
        register.setPassword("12345678");
        register.setFirstName("Adb");
        register.setLastName("Mga");
        register.setPhone("+79811234543");
        register.setRole(Role.USER);
        return register;
    }

    public static Register getSecondRegisteredUser() {
        Register register = new Register();
        register.setUsername("user2@mail.ru");
        register.setPassword("12345678");
        register.setFirstName("Adb");
        register.setLastName("Mga");
        register.setPhone("+79811234543");
        register.setRole(Role.USER);
        return register;
    }

    public static Register getRegisteredAdmin() {
        Register register = new Register();
        register.setUsername("admin@mail.ru");
        register.setPassword("12345678");
        register.setFirstName("Adb");
        register.setLastName("Mga");
        register.setPhone("+79811234543");
        register.setRole(Role.ADMIN);
        return register;
    }

    public static UpdateUser getUpdateUser() {
        UpdateUser updateUser = new UpdateUser();
        updateUser.setFirstName("upd1Name");
        updateUser.setLastName("upd2Name");
        updateUser.setPhone("+79991234567");
        return updateUser;
    }
}
