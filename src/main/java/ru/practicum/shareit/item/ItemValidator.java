package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

public class ItemValidator {
    // Метод, проверяющий, что все обязательные поля Item не null
    public static void checkAllFields(Item item) {
        if (item.getName() == null || item.getName().isBlank())
            throw new ValidationException("Имя вещи не может быть пустым");
        if (item.getDescription() == null || item.getDescription().isBlank())
            throw new ValidationException("Описание вещи не может быть пустым");
        if (item.getAvailable() == null)
            throw new ValidationException("Статус доступности вещи не может быть пустым");
    }

    // Метод, проверяющий, что введенные поля Item валидны (для запросов на обновление вещи)
    public static void checkNotNullFields(Item item) {
        if (item.getName() != null && item.getName().isBlank())
            throw new ValidationException("Имя вещи не может быть пустым");
        if (item.getDescription() != null && item.getDescription().isBlank())
            throw new ValidationException("Описание вещи не может быть пустым");
        // У статуса доступности всего лишь два состояния: True/False - в проверке не нуждается
    }
}
