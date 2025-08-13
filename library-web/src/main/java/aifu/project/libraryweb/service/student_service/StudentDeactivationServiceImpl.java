package aifu.project.libraryweb.service.student_service;


import aifu.project.common_domain.dto.action_dto.DeactivationStats;
import aifu.project.libraryweb.repository.StudentRepository;
import aifu.project.libraryweb.service.PassportHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentDeactivationServiceImpl implements StudentDeactivationService {

    private final StudentRepository studentRepository;

    private final PassportHasher passportHasher;





    @Override
    public DeactivationStats deactivateStudents(InputStream inputStream) {


        return null;
    }
}