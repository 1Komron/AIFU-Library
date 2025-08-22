CREATE TABLE notification (
                              id BIGSERIAL PRIMARY KEY,
                              student_id BIGINT,
                              book_copy_id INT,
                              notification_type notification_type,
                              notification_time TIMESTAMP,
                              is_read BOOLEAN DEFAULT FALSE,
                              CONSTRAINT fk_notification_student FOREIGN KEY (student_id) REFERENCES student(id),
                              CONSTRAINT fk_notification_book FOREIGN KEY (book_copy_id) REFERENCES book_copy(id)
);

