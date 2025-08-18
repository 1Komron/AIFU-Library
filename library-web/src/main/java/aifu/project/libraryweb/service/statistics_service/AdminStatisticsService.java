package aifu.project.libraryweb.service.statistics_service;

import aifu.project.common_domain.dto.ResponseMessage;
import aifu.project.common_domain.entity.Booking;
import aifu.project.common_domain.entity.Librarian;
import org.springframework.http.ResponseEntity;

public interface AdminStatisticsService {
    ResponseEntity<ResponseMessage> getIssuedBooks(String period);

    ResponseEntity<ResponseMessage> getExtendedBooks(String period);

    ResponseEntity<ResponseMessage> getReturnBooks(String period);

    ResponseEntity<ResponseMessage> getActivityToday();

    ResponseEntity<ResponseMessage> getActivityAnalytics(String period);

    ResponseEntity<ResponseMessage> getActivity(String period);

    ResponseEntity<ResponseMessage> getTop(String period);

    void createActivity(Booking booking, String action, Librarian librarian);
}
