package aifu.project.uhf_reader.repository;

import aifu.project.common_domain.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
                select case
                    when count(bc) = 0 then -1
                    when exists (
                        select 1 from Booking b where b.book.epc = :epc
                    ) then 1
                    else 0
                end
                from BookCopy bc
                where bc.epc = :epc
            """)
    Integer getBookEpcStatus(String epc);

}
