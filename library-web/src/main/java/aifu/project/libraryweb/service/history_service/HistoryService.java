package aifu.project.libraryweb.service.history_service;


import aifu.project.common_domain.entity.Booking;

public interface HistoryService {
    void add(Booking booking);

    public long getQuantityPerMonth(int month);
}
