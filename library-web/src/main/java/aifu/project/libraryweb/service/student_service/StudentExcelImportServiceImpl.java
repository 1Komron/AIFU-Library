package aifu.project.libraryweb.service.student_service;

import aifu.project.libraryweb.config.ImportStats;
import org.springframework.stereotype.Service;

import java.io.InputStream;
@Service
public class StudentExcelImportServiceImpl implements StudentExcelImportService {
    @Override
    public ImportStats importStudentsFromExcel(InputStream inputStream) {
        return null;
    }
}
