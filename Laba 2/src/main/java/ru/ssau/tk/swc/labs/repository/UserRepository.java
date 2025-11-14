package ru.ssau.tk.swc.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.swc.labs.entity.users;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<users, Long>{
    Optional<users> findByLogin(String login);
    Optional<users> findByEmail(String email);
    Optional<users> findByLoginAndPassword(String login, String password);
}
