CREATE TABLE public.admin_activity (
                                       id BIGSERIAL PRIMARY KEY,

                                       librarian_id BIGINT,

                                       action VARCHAR(255),

                                       student_name VARCHAR(255),
                                       student_surname VARCHAR(255),
                                       book_title VARCHAR(255),
                                       book_author VARCHAR(255),
                                       book_inventory_number VARCHAR(255),

                                       created_at TIMESTAMP,
                                       created_date DATE,

                                       CONSTRAINT fk_admin_activity_to_librarian FOREIGN KEY (librarian_id)
                                           REFERENCES public.librarian(id) ON DELETE SET NULL
);


CREATE INDEX idx_admin_activity_librarian_id ON public.admin_activity(librarian_id);

CREATE INDEX idx_admin_activity_created_date ON public.admin_activity(created_date);
