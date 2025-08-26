CREATE TABLE public.librarian (
                                  id BIGINT PRIMARY KEY,
                                  image_url VARCHAR(255),
                                  email VARCHAR(255) NOT NULL,
                                  password VARCHAR(255) NOT NULL,
                                  CONSTRAINT fk_librarian_user FOREIGN KEY (id) REFERENCES public.users(id)
);
