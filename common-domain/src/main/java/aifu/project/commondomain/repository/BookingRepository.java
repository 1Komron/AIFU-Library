package aifu.project.commondomain.repository;

import aifu.project.commondomain.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.book bc " +
            "JOIN FETCH bc.book bb " +
            "WHERE b.user.chatId = :chatId")
    List<Booking> findAllWithBooksByUser_ChatId(@Param("chatId") Long chatId);

}
