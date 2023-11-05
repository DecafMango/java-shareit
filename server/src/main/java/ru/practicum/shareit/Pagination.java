package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ValidationException;

public class Pagination {

    public static Pageable createPageTemplate(Integer from, Integer size, Sort.Direction direction) {
        Pageable page = null;
        if (from != null && size != null) {
            if (from < 0 || size <= 0)
                throw new ValidationException("Параметры from и size должны быть следующего вида: from >= 0 size > 0");
            Sort sort = Sort.by(direction, "id");
            page = PageRequest.of(from / size, size, sort);
        }
        return page;
    }
}
