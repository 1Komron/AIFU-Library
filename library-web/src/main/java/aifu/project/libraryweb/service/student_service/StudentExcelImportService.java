package aifu.project.libraryweb.service.student_service;

import aifu.project.libraryweb.config.ImportStats;

import java.io.InputStream;

public interface StudentExcelImportService {

      ImportStats importStudentsFromExcel(InputStream inputStream);

}
