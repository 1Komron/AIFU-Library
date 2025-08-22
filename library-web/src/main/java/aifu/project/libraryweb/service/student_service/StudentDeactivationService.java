package aifu.project.libraryweb.service.student_service;

import aifu.project.common_domain.dto.action_dto.DeactivationStats;

import java.io.InputStream;

public interface StudentDeactivationService {

   DeactivationStats deactivateStudents (InputStream inputStream);
}