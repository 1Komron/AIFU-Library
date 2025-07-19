package aifu.project.uhf_reader.repository;

import aifu.project.common_domain.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsBookingByBook_Epc(String epc);
}
