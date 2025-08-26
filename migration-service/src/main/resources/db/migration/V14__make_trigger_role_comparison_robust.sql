-- V14__make_trigger_role_comparison_robust.sql

-- Funksiyani yanada ishonchliroq qilish uchun uni qayta e'lon qilamiz.
-- Bu yerda biz ENUM qiymatini TEXT'ga o'girib, keyin matn bilan solishtiramiz.
CREATE OR REPLACE FUNCTION sync_user_deletion_to_children()
RETURNS TRIGGER AS $$
BEGIN
    -- YECHIM: NEW.role o'zgaruvchisini TEXT tipiga o'giramiz (cast qilamiz)
    IF (NEW.role::text = 'LIBRARIAN') THEN
        IF NEW.is_deleted THEN
UPDATE public.librarian SET unique_email_token = NULL WHERE id = NEW.id;
ELSE
UPDATE public.librarian SET unique_email_token = (SELECT email FROM public.librarian WHERE id = NEW.id) WHERE id = NEW.id;
END IF;
END IF;

    -- Xuddi shu o'zgarishni STUDENT uchun ham qilamiz
    IF (NEW.role::text = 'STUDENT') THEN
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