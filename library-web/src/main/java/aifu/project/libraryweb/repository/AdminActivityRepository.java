package aifu.project.libraryweb.repository;

import aifu.project.common_domain.dto.activity_dto.ActivityAnalyticsDTO;
import aifu.project.common_domain.dto.activity_dto.ActivityDTO;
import aifu.project.common_domain.dto.activity_dto.TopAdmin;
import aifu.project.common_domain.entity.AdminActivity;
import aifu.project.common_domain.entity.Librarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminActivityRepository extends JpaRepository<AdminActivity, Long> {
    long countByLibrarianAndActionAndCreatedAtBetween(Librarian librarian, String action, LocalDateTime start, LocalDateTime end);

    @Query("""
            select new aifu.project.common_domain.dto.activity_dto.ActivityDTO(
                        a.studentName,
                        a.studentSurname,
                        a.bookAuthor,
                        a.bookTitle,
                        a.bookInventoryNumber,
                        a.action,
                        a.createdAt,
                        new aifu.project.common_domain.dto.activity_dto.ActivityAnalyticsDTO(
                            count(a),
                            sum(case when a.action = 'ISSUED' then 1 else 0 end),
                            sum(case when a.action = 'EXTENDED' then 1 else 0 end),
                            sum(case when a.action = 'RETURNED' then 1 else 0 end)
                )
            ) from AdminActivity a
            where a.librarian = :librarian and a.createdAt between :startTime and :endTime
            group by a.studentName, a.studentSurname, a.bookAuthor, a.bookTitle, a.bookInventoryNumber
            """)
    List<ActivityDTO> findActivities(Librarian librarian, LocalDateTime startTime, LocalDateTime endTime);

    @Query("""
                select new aifu.project.common_domain.dto.activity_dto.ActivityAnalyticsDTO(
                    count(a),
                    sum(case when a.action = 'ISSUED' then 1 else 0 end),
                    sum(case when a.action = 'EXTENDED' then 1 else 0 end),
                    sum(case when a.action = 'RETURNED' then 1 else 0 end)
                )
                from AdminActivity a
                where a.librarian = :librarian
                  and a.createdAt between :startDate and :endDate
            """)
    ActivityAnalyticsDTO findActivityAnalytics(Librarian librarian, LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
            select new aifu.project.common_domain.dto.activity_dto.TopAdmin(
                            l.id,
                            l.imageUrl,
                            l.name,
                            l.surname,
                            l.email,
                            new aifu.project.common_domain.dto.activity_dto.ActivityAnalyticsDTO(
                                count(a),
                                sum(case when a.action = 'ISSUED' then 1 else 0 end),
                                sum(case when a.action = 'EXTENDED' then 1 else 0 end),
                                sum(case when a.action = 'RETURNED' then 1 else 0 end)
                        )) from AdminActivity a
                        join a.librarian l
                        where a.createdAt between :startTime and :endTime
                        group by l.id, l.imageUrl, l.name, l.surname, l.email
                        order by count(a) desc
            """)
    List<TopAdmin> findTopAdmins(LocalDateTime startTime, LocalDateTime endTime);
}
