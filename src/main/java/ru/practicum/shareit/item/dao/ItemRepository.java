package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Item getItemById(long id);

    List<Item> getItemsByOwnerId(long id);

    void removeItemByIdAndOwnerId(long id, long ownerId);

    @Query("select i.id from Item i where i.ownerId = ?1")
    List<Long> getAllUserItemsId(long ownerId);

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) and i.available = true")
    List<Item> findByText(@Param("text") String text);

}
