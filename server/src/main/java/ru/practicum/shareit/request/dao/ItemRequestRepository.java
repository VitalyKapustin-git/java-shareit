package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    ItemRequest getItemRequestById(Long itemRequestId);

    List<ItemRequest> getItemRequestsByRequestorId(Long creatorId);

    List<ItemRequest> getItemRequestsByRequestorIdNot(Long userId, Pageable pageable);

}
