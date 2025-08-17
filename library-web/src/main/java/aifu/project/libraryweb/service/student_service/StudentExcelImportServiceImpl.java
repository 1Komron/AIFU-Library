package aifu.project.libraryweb.service.student_service;

import aifu.project.libraryweb.config.ImportStats;
import aifu.project.libraryweb.config.ImporterColumnProperties;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.service.PassportHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class StudentExcelImportServiceImpl implements StudentExcelImportService {

    private final StudentRepository studentRepository;
    private final ImporterColumnProperties importerColumnProperties;
    private final PassportHasher passportHasher;

    @Override
    public ImportStats importStudentsFromExcel(InputStream inputStream) {
        return null;
    }
}
