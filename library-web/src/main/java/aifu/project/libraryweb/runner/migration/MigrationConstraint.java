package aifu.project.libraryweb.runner.migration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationConstraint {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void migrate() {
        log.info("Migratisya jarayoni...");

        createIsUserActiveFunction();

        migrateUniqueLibrarianEmail();
        migrateUniqueStudentPassport();
        migrateUniqueStudentCardNumber();

        migrateUniqueBaseBookCategory();
        migrateUniqueInventoryNumber();
        migrateUniqueBookEpc();

        log.info("Migratsiya jarayoni yakunlandi.");
    }

    private void executeSql(String sql, String entityName) {
        try {
            jdbcTemplate.execute(sql);
            log.info("Migratisya '{}' bo'уicha muvaffaqiyatli amalga oshirildi yoki ushbu unique index mavjud", entityName);
        } catch (Exception e) {
            log.error("Migratsiya jarayonida xatolik yuz berdi. Entity: '{}'", entityName, e);
        }
    }

    private void createIsUserActiveFunction() {
        String sql = """
                CREATE OR REPLACE FUNCTION is_user_not_deleted(user_id BIGINT) RETURNS BOOLEAN AS $$
                BEGIN
                    RETURN (SELECT NOT is_deleted FROM users WHERE id = user_id);
                END;
                $$ LANGUAGE plpgsql IMMUTABLE;
                """;
        executeSql(sql, "SQL FUNKSIYA is_user_not_deleted");
    }

    private void migrateUniqueLibrarianEmail() {
        String sql = """
                CREATE UNIQUE INDEX IF NOT EXISTS unique_librarian_email_not_deleted
                    ON librarian (email)
                    WHERE is_user_not_deleted(id);
                """;
        executeSql(sql, "Librarian Email");
    }

    private void migrateUniqueStudentPassport() {
        String sql = """
                CREATE UNIQUE INDEX IF NOT EXISTS unique_student_passport_not_deleted
                    ON student (passport_code)
                    WHERE is_user_not_deleted(id);
                """;
        executeSql(sql, "Student Passport");
    }

    private void migrateUniqueStudentCardNumber() {
        String sql = """
                CREATE UNIQUE INDEX IF NOT EXISTS unique_student_card_number_not_deleted
                    ON student (card_number)
                    WHERE is_user_not_deleted(id);
                """;
        executeSql(sql, "Student Card Number");
    }

    private void migrateUniqueBaseBookCategory() {
        String sql = "CREATE UNIQUE INDEX IF NOT EXISTS unique_base_book_category_name_not_deleted ON base_book_category (name) WHERE is_deleted = false;";
        executeSql(sql, "BaseBookCategory Name");
    }

    private void migrateUniqueInventoryNumber() {
        String sql = "CREATE UNIQUE INDEX IF NOT EXISTS unique_book_inventory_number_not_deleted ON book_copy (inventory_number) WHERE is_deleted = false;";
        executeSql(sql, "BookCopy Inventory Number");
    }

    private void migrateUniqueBookEpc() {
        String sql = "CREATE UNIQUE INDEX IF NOT EXISTS unique_book_epc_not_deleted ON book_copy (epc) WHERE is_deleted = false;";
        executeSql(sql, "BookCopy EPC");
    }
}