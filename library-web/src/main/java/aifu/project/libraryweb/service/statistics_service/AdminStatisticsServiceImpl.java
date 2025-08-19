package aifu.project.libraryweb.service.statistics_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.dto.activity_dto.ActivityAnalyticsDTO;
import aifu.project.common_domain.dto.activity_dto.ActivityDTO;
import aifu.project.common_domain.dto.activity_dto.ActivityDailyAnalyticsDTO;
import aifu.project.common_domain.dto.activity_dto.TopAdmin;
import aifu.project.common_domain.entity.*;
import aifu.project.libraryweb.entity.SecurityLibrarian;
import aifu.project.libraryweb.repository.AdminActivityRepository;
import aifu.project.libraryweb.repository.LibrarianRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminStatisticsServiceImpl implements AdminStatisticsService {
    private final AdminActivityRepository adminActivityRepository;
    private final LibrarianRepository librarianRepository;

    private static final String ISSUED = "ISSUED";
    private static final String LAST_MONTH = "last-month";
    private static final String COUNT = "count";
    private static final String PERCENT = "percent";

    @Override
    public ResponseEntity<ResponseMessage> getIssuedBooks(String period) {
        Librarian librarian = getLibrarian();

        YearMonth currentMonth = YearMonth.now();
        YearMonth targetMonth = period.equals(LAST_MONTH)
                ? currentMonth.minusMonths(1)
                : currentMonth;

        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();

        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        long count = adminActivityRepository.countByLibrarianAndActionAndCreatedAtBetween(
                librarian,
                ISSUED,
                startTime,
                endTime);

        long totalCount = adminActivityRepository.countByActionAndCreatedAtBetween(ISSUED, startTime, endTime);

        YearMonth prevMonth = targetMonth.minusMonths(1);
        LocalDate prevStart = prevMonth.atDay(1);
        LocalDate prevEnd = prevMonth.atEndOfMonth();

        long prevCount = adminActivityRepository.countByLibrarianAndActionAndCreatedAtBetween(
                librarian,
                ISSUED,
                prevStart.atStartOfDay(),
                prevEnd.atTime(LocalTime.MAX));

        double percent = 0.0;
        if (prevCount > 0) {
            percent = ((double) (count - prevCount) / prevCount) * 100;
        }

        double totalPercent = ((double) count / totalCount) * 100;

        Map<String, Object> response = Map.of(
                COUNT, count,
                PERCENT, percent,
                "totalCount", totalCount,
                "totalPercent", totalPercent
        );


        return ResponseEntity.ok(new ResponseMessage(true, "Berilgan kitoblar soni. period: " + period, response));
    }

    @Override
    public ResponseEntity<ResponseMessage> getExtendedBooks(String period) {
        Librarian librarian = getLibrarian();

        YearMonth currentMonth = YearMonth.now();
        YearMonth targetMonth = period.equals(LAST_MONTH)
                ? currentMonth.minusMonths(1)
                : currentMonth;

        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();

        long count = adminActivityRepository.countByLibrarianAndActionAndCreatedAtBetween(
                librarian,
                "EXTENDED",
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX));

        YearMonth prevMonth = targetMonth.minusMonths(1);
        LocalDate prevStart = prevMonth.atDay(1);
        LocalDate prevEnd = prevMonth.atEndOfMonth();

        long prevCount = adminActivityRepository.countByLibrarianAndActionAndCreatedAtBetween(
                librarian,
                "EXTENDED",
                prevStart.atStartOfDay(),
                prevEnd.atTime(LocalTime.MAX));

        double percent = 0.0;
        if (prevCount > 0) {
            percent = ((double) (count - prevCount) / prevCount) * 100;
        }

        Map<String, Object> response = Map.of(
                COUNT, count,
                PERCENT, percent
        );

        return ResponseEntity.ok(new ResponseMessage(true, "Vaqti uzaytirilgan kitoblar soni. period: " + period, response));
    }

    @Override
    public ResponseEntity<ResponseMessage> getReturnBooks(String period) {
        Librarian librarian = getLibrarian();

        YearMonth currentMonth = YearMonth.now();
        YearMonth targetMonth = period.equals(LAST_MONTH)
                ? currentMonth.minusMonths(1)
                : currentMonth;

        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();

        long count = adminActivityRepository.countByLibrarianAndActionAndCreatedAtBetween(
                librarian,
                "RETURNED",
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX));

        YearMonth prevMonth = targetMonth.minusMonths(1);
        LocalDate prevStart = prevMonth.atDay(1);
        LocalDate prevEnd = prevMonth.atEndOfMonth();

        long prevCount = adminActivityRepository.countByLibrarianAndActionAndCreatedAtBetween(
                librarian,
                "RETURNED",
                prevStart.atStartOfDay(),
                prevEnd.atTime(LocalTime.MAX));

        double percent = 0.0;
        if (prevCount > 0) {
            percent = ((double) (count - prevCount) / prevCount) * 100;
        }

        Map<String, Object> response = Map.of(
                COUNT, count,
                PERCENT, percent
        );

        return ResponseEntity.ok(new ResponseMessage(true, "Qaytib olingan kitoblar soni. period: " + period, response));
    }

    @Override
    public ResponseEntity<ResponseMessage> getActivityToday() {
        Librarian librarian = getLibrarian();

        LocalDate today = LocalDate.now();
        LocalDateTime startTime = today.atStartOfDay();
        LocalDateTime endTime = today.atTime(LocalTime.MAX);

        List<ActivityDTO> activities = adminActivityRepository.findActivities(librarian, startTime, endTime);
        ActivityAnalyticsDTO activityAnalytics = adminActivityRepository.findActivityAnalytics(librarian, startTime, endTime);

        Map<String, Object> response = Map.of(
                "activities", activities,
                "analytics", activityAnalytics
        );

        log.info("Bugungi activity. Librarian: {}, activity ro'xyati: {}", librarian.getId(), activities);

        return ResponseEntity.ok(new ResponseMessage(true, "Bugungi activity", response));
    }

    @Override
    public ResponseEntity<ResponseMessage> getActivityAnalytics(String period) {
        Librarian librarian = getLibrarian();

        YearMonth currentMonth = YearMonth.now();
        YearMonth targetMonth = period.equals(LAST_MONTH)
                ? currentMonth.minusMonths(1)
                : currentMonth;

        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.equals(currentMonth)
                ? LocalDate.now()
                : targetMonth.atEndOfMonth();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<ActivityDailyAnalyticsDTO> activityAnalytics = adminActivityRepository.findDailyActivityAnalytics(librarian, startDateTime, endDateTime);

        log.info("Activity statistikasi: {}", activityAnalytics);

        return ResponseEntity.ok(new ResponseMessage(true, "Statistika", activityAnalytics));
    }

    @Override
    public ResponseEntity<ResponseMessage> getActivity(String period) {
        Librarian librarian = getLibrarian();

        YearMonth currentMonth = YearMonth.now();
        YearMonth targetMonth = period.equals(LAST_MONTH)
                ? currentMonth.minusMonths(1)
                : currentMonth;

        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();

        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);

        List<ActivityDTO> activities = adminActivityRepository.findActivities(librarian, startTime, endTime);
        ActivityAnalyticsDTO activityAnalytics = adminActivityRepository.findActivityAnalytics(librarian, startTime, endTime);

        log.info("Activity (Period '{}'). Librarian: {}, activity ro'xyati: {}", period, librarian.getId(), activities);

        Map<String, Object> response = Map.of(
                "activities", activities,
                "analytics", activityAnalytics
        );

        return ResponseEntity.ok(new ResponseMessage(true, "Activity. period: " + period, response));
    }

    @Override
    public ResponseEntity<ResponseMessage> getTop(String period) {
        YearMonth currentMonth = YearMonth.now();
        YearMonth targetMonth = period.equals(LAST_MONTH)
                ? currentMonth.minusMonths(1)
                : currentMonth;

        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();

        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);

        List<TopAdmin> topAdmins = adminActivityRepository.findTopAdmins(startTime, endTime);

        log.info("Top adminlar ro'yxati '{}'. (Period '{}'). ", topAdmins.stream().map(TopAdmin::id), period);

        return ResponseEntity.ok(new ResponseMessage(true, "Top Admin. period: " + period, topAdmins));
    }

    @Override
    public void createActivity(Booking booking, String action, Librarian librarian) {
        BookCopy bookCopy = booking.getBook();
        BaseBook baseBook = bookCopy.getBook();
        Student student = booking.getStudent();

        AdminActivity adminActivity = new AdminActivity();
        adminActivity.setCreatedAt(LocalDateTime.now());
        adminActivity.setCreatedDate(LocalDate.now());
        adminActivity.setLibrarian(librarian);
        adminActivity.setAction(action);

        adminActivity.setStudentName(student.getName());
        adminActivity.setStudentSurname(student.getSurname());

        adminActivity.setBookAuthor(baseBook.getAuthor());
        adminActivity.setBookTitle(baseBook.getTitle());
        adminActivity.setBookInventoryNumber(bookCopy.getInventoryNumber());

        adminActivity = adminActivityRepository.save(adminActivity);

        log.info("Admin activity yaratildi: {}", adminActivity);
    }

    private static Librarian getLibrarian() {
        SecurityLibrarian securityLibrarian = (SecurityLibrarian) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return securityLibrarian.toBase();
    }
}
