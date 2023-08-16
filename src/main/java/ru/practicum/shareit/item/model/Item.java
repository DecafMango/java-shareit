package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class Item {
    @NotBlank
    private String name;
    private String description;
    @Getter
    private Boolean available;

    //TODO разобраться с полем request

}
