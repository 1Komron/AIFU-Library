-- =================================================================
-- LIBRARIAN UCHUN UNIKALLIKNI TA'MINLASH (EMAIL BO'YICHA)
-- =================================================================

-- 1. Yordamchi ustun qo'shish va unga unikal indeks o'rnatish
ALTER TABLE public.librarian ADD COLUMN unique_email_token TEXT;
CREATE UNIQUE INDEX unique_librarian_email_token_idx ON public.librarian (unique_email_token);

-- 2. Librarian jadvaliga yozuv qo'shilganda/o'zgartirilganda ishlaydigan trigger funksiyasi
CREATE OR REPLACE FUNCTION sync_librarian_unique_token()
RETURNS TRIGGER AS $$
BEGIN
    -- users jadvalidan is_deleted holatini tekshiramiz
    IF (SELECT is_deleted FROM public.users WHERE id = NEW.id) THEN
        -- Agar foydalanuvchi o'chirilgan bo'lsa, token NULL bo'ladi
        NEW.unique_email_token := NULL;
ELSE
        -- Agar foydalanuvchi faol bo'lsa, token email'ga teng bo'ladi
        NEW.unique_email_token := NEW.email;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3. Trigger'ni Librarian jadvaliga bog'lash
CREATE TRIGGER librarian_sync_token_trigger
    BEFORE INSERT OR UPDATE ON public.librarian
                         FOR EACH ROW EXECUTE FUNCTION sync_librarian_unique_token();


-- =================================================================
-- STUDENT UCHUN UNIKALLIKNI TA'MINLASH (PASSPORT_CODE va CARD_NUMBER BO'YICHA)
-- =================================================================

-- 4. Student jadvaliga yordamchi ustunlar qo'shish va ularga unikal indekslar o'rnatish
ALTER TABLE public.student ADD COLUMN unique_passport_token TEXT;
CREATE UNIQUE INDEX unique_student_passport_token_idx ON public.student (unique_passport_token);

ALTER TABLE public.student ADD COLUMN unique_card_token TEXT;
CREATE UNIQUE INDEX unique_student_card_token_idx ON public.student (unique_card_token);

-- 5. Student jadvaliga yozuv qo'shilganda/o'zgartirilganda ishlaydigan trigger funksiyasi
CREATE OR REPLACE FUNCTION sync_student_unique_tokens()
RETURNS TRIGGER AS $$
BEGIN
    -- users jadvalidan is_deleted holatini tekshiramiz
    IF (SELECT is_deleted FROM public.users WHERE id = NEW.id) THEN
        -- Agar foydalanuvchi o'chirilgan bo'lsa, tokenlar NULL bo'ladi
        NEW.unique_passport_token := NULL;
NEW.unique_card_token := NULL;
ELSE
        -- Agar foydalanuvchi faol bo'lsa, tokenlar tegishli maydonlarga teng bo'ladi
        NEW.unique_passport_token := NEW.passport_code;
        NEW.unique_card_token := NEW.card_number;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 6. Trigger'ni Student jadvaliga bog'lash
CREATE TRIGGER student_sync_tokens_trigger
    BEFORE INSERT OR UPDATE ON public.student
                         FOR EACH ROW EXECUTE FUNCTION sync_student_unique_tokens();


-- =================================================================
-- USERS JADVALIDAGI O'ZGARISHLARNI BOG'LIQ JADVALLARGA TA'SIR ETTIRISH
-- =================================================================

-- 7. users.is_deleted o'zgarganda ishlaydigan yagona trigger funksiyasi
CREATE OR REPLACE FUNCTION sync_user_deletion_to_children()
RETURNS TRIGGER AS $$
BEGIN
    -- Agar o'zgarish LIBRARIAN rolidagi user'ga tegishli bo'lsa
    IF (NEW.role = 'LIBRARIAN') THEN
        IF NEW.is_deleted THEN
UPDATE public.librarian SET unique_email_token = NULL WHERE id = NEW.id;
ELSE
UPDATE public.librarian SET unique_email_token = (SELECT email FROM public.librarian WHERE id = NEW.id) WHERE id = NEW.id;
END IF;
END IF;

    -- Agar o'zgarish STUDENT rolidagi user'ga tegishli bo'lsa
    IF (NEW.role = 'STUDENT') THEN
        IF NEW.is_deleted THEN
UPDATE public.student SET unique_passport_token = NULL, unique_card_token = NULL WHERE id = NEW.id;
ELSE
UPDATE public.student SET
                          unique_passport_token = (SELECT passport_code FROM public.student WHERE id = NEW.id),
                          unique_card_token = (SELECT card_number FROM public.student WHERE id = NEW.id)
WHERE id = NEW.id;
END IF;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 8. Trigger'ni Users jadvaliga bog'lash
CREATE TRIGGER user_deletion_sync_trigger
    AFTER UPDATE ON public.users
    FOR EACH ROW
    WHEN (OLD.is_deleted IS DISTINCT FROM NEW.is_deleted) -- faqat is_deleted o'zgarganda ishga tushadi
EXECUTE FUNCTION sync_user_deletion_to_children();