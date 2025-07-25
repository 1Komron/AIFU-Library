package aifu.project.libraryweb.repository;

import aifu.project.common_domain.entity.User;
import aifu.project.common_domain.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select count(*) from User u where u.role = 'USER' and u.isDeleted = false")
    long getUsersCount();

    Page<User> findByRoleAndIsDeletedFalse(Role role, Pageable pageable);

    Page<User> findByRoleAndIsActiveAndIsDeletedFalse(Role role, boolean active, Pageable pageable);

    Page<User> findByIdAndRoleAndIsDeletedFalse(Long id, Role role, Pageable pageable);

    Page<User> findByPhoneAndRoleAndIsDeletedFalse(String phone, Role role, Pageable pageable);


}
