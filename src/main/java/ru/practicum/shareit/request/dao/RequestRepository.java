package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> getAllByRequestor_IdOrderByCreatedDesc(Long requestorId);

    Page<Request> getAllByRequestor_IdNotOrderByCreatedDesc(Long requestorId, Pageable page);
}
