package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Item validator test")
public class ItemValidatorTest {

    @Test
    @DisplayName("should validate item fields")
    public void should_validate_item_fields() {
        ItemDto validItemDto = new ItemDto(null, "name", "description", true, null, null, null, null);

        ItemValidator.checkAllFields(validItemDto);
        ItemValidator.checkNotNullFields(validItemDto);
    }

    @Test
    @DisplayName("should not validate item due to invalid name")
    public void should_not_validate_item_due_to_invalid_name() {
        ItemDto invalidItemDto = new ItemDto(null, "", "description", true, null, null, null, null);

        assertThrows(ValidationException.class, () -> ItemValidator.checkAllFields(invalidItemDto));
        assertThrows(ValidationException.class, () -> ItemValidator.checkNotNullFields(invalidItemDto));
    }

    @Test
    @DisplayName("should not validate item due to invalid description")
    public void should_not_validate_item_due_to_invalid_description() {
        ItemDto invalidItemDto = new ItemDto(null, "name", "", true, null, null, null, null);

        assertThrows(ValidationException.class, () -> ItemValidator.checkAllFields(invalidItemDto));
        assertThrows(ValidationException.class, () -> ItemValidator.checkNotNullFields(invalidItemDto));
    }

    @Test
    @DisplayName("should not validate item due to null available field")
    public void should_not_validate_item_due_to_null_available_field() {
        ItemDto invalidItemDto = new ItemDto(null, "name", "description", null, null, null, null, null);

        assertThrows(ValidationException.class, () -> ItemValidator.checkAllFields(invalidItemDto));
    }

}
