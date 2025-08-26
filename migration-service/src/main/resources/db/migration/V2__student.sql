CREATE TABLE student (
                         id BIGINT PRIMARY KEY,
                         degree VARCHAR(255),
                         faculty VARCHAR(255),
                         passport_code VARCHAR(255),
                         card_number VARCHAR(255) NOT NULL,
                         admission_time DATE,
                         graduation_time DATE,
                         phone_number VARCHAR(50),
                         chat_id BIGINT UNIQUE,
                         CONSTRAINT fk_student_user FOREIGN KEY (id)
                             REFERENCES users(id) ON DELETE CASCADE
);

--CREATE INDEX idx_chatId ON student(chat_id);
CREATE INDEX idx_passport_code ON student(passport_code);