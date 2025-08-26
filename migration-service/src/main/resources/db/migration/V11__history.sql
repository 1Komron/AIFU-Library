CREATE TABLE history (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT,
                         book_id INT,
                         issued_by_id BIGINT,
                         returned_by_id BIGINT,
                         given_at DATE,
                         due_date DATE,
                         returned_at DATE,
                         CONSTRAINT fk_history_student FOREIGN KEY (user_id) REFERENCES student(id),
                         CONSTRAINT fk_history_book FOREIGN KEY (book_id) REFERENCES book_copy(id),
                         CONSTRAINT fk_history_issued_by FOREIGN KEY (issued_by_id) REFERENCES librarian(id),
                         CONSTRAINT fk_history_returned_by FOREIGN KEY (returned_by_id) REFERENCES librarian(id)
);
