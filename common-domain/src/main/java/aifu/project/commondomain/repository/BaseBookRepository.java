package aifu.project.commondomain.repository;

import aifu.project.commondomain.entity.BaseBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseBookRepository extends JpaRepository<BaseBook, Integer> {
}
