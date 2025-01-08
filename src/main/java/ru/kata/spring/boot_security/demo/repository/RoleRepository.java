package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.Role;

import java.util.List;
import java.util.Optional;

//@Repository
//public interface RoleRepository extends JpaRepository<Role, Long> {
//    Optional<Role> getRoleByName(String roleName);
//}



@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT r FROM Role r JOIN FETCH r.users u WHERE u.id = :userId")
    List<Role> findRolesByUserId(Long userId);

    Optional<Role> getRoleByName(String role);
}

