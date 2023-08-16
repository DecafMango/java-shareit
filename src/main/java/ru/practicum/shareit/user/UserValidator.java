package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

public class UserValidator {
    // Метод, проверяющий, что все обязательные поля User не null
    public static void checkAllFields(User user) {
        if (user.getName() == null || user.getName().isBlank())
            throw new ValidationException("Имя пользователя не может быть пустым");
        if (user.getEmail() == null)
            throw new ValidationException("Почта пользователя не может быть пустой");
    }

    // Метод, проверяющий, что введенные поля User валидны (для запросов на обновление пользователя)
    public static void checkNotNullFields(User user) {
        if (user.getName() != null && user.getName().isBlank())
            throw new ValidationException("Имя пользователя не может быть пустым");
        // Поле email и так проверяет аннотацией @Email
    }
}
