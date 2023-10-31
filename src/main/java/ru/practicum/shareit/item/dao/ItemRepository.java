package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;


public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwner_IdOrderById(Long userId, Pageable page);
}
