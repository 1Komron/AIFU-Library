-- Librarian: email aktivlar orasida unique likka tekshirish
CREATE UNIQUE INDEX IF NOT EXISTS unique_librarian_email_not_deleted
    ON librarian (email)
    WHERE id IN (
        SELECT u.id FROM users u WHERE u.is_deleted = false
    );

-- Student: passportCode aktivlar orasida unique likka tekshirish
CREATE UNIQUE INDEX IF NOT EXISTS unique_student_passport_not_deleted
    ON student (passport_code)
    WHERE id IN (
        SELECT u.id FROM users u WHERE u.is_deleted = false
    );

-- Student: cardNumber aktivlar orasida unique likka tekshirish
CREATE UNIQUE INDEX IF NOT EXISTS unique_student_card_not_deleted
    ON student (card_number)
    WHERE id IN (
        SELECT u.id FROM users u WHERE u.is_deleted = false
    );

-- BaseBookCategory: name aktivlar orasida unique likka tekshirish
CREATE UNIQUE INDEX IF NOT EXISTS unique_basebookcategory_name_not_deleted
    ON base_book_category (name)
    WHERE is_deleted = false;

-- BookCopy: inventoryNumber aktivlar orasida unique likka tekshirish
CREATE UNIQUE INDEX IF NOT EXISTS unique_bookcopy_inventory_not_deleted
    ON book_copy (inventory_number)
    WHERE is_deleted = false;

-- BookCopy: epc aktivlar orasida unique likka tekshirish
CREATE UNIQUE INDEX IF NOT EXISTS unique_bookcopy_epc_not_deleted
    ON book_copy (epc)
    WHERE is_deleted = false;

