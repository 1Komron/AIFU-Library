CREATE TABLE booking (
                         id BIGSERIAL PRIMARY KEY,
                         student_id BIGINT,
                         book_id INT,
                         given_at DATE,
                         due_date DATE,
                         status status,
                         issued_by_id BIGINT,
                         extended_by_id BIGINT,
                         extended_at DATE,
                         CONSTRAINT fk_booking_student FOREIGN KEY (student_id) REFERENCES student(id),
                         CONSTRAINT fk_booking_book FOREIGN KEY (book_id) REFERENCES book_copy(id),
                         CONSTRAINT fk_booking_issued_by FOREIGN KEY (issued_by_id) REFERENCES librarian(id),
                         CONSTRAINT fk_booking_extended_by FOREIGN KEY (extended_by_id) REFERENCES librarian(id)
);
