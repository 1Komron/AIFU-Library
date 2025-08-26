-- V13__fix_role_comparison_in_trigger.sql

-- users.is_deleted o'zgarganda ishlaydigan funksiyani tuzatamiz
CREATE OR REPLACE FUNCTION sync_user_deletion_to_children()
RETURNS TRIGGER AS $$
BEGIN
    -- MUAMMO SHU YERDA EDI: 'LIBRARIAN' -> 'LIBRARIAN'::role
    -- Endi biz "LIBRARIAN" matnini "role" tipiga o'girib (cast) keyin taqqoslayapmiz
    IF (NEW.role = 'LIBRARIAN'::role) THEN
        IF NEW.is_deleted THEN
UPDATE public.librarian SET unique_email_token = NULL WHERE id = NEW.id;
ELSE
UPDATE public.librarian SET unique_email_token = (SELECT email FROM public.librarian WHERE id = NEW.id) WHERE id = NEW.id;
END IF;
END IF;

    -- Xuddi shunday o'zgarishni STUDENT uchun ham qilamiz
    IF (NEW.role = 'STUDENT'::role) THEN
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