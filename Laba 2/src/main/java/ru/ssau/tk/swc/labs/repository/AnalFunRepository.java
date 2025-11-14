package ru.ssau.tk.swc.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.swc.labs.entity.analFun;
import java.util.Optional;

@Repository
public interface AnalFunRepository extends JpaRepository<analFun, Long>{
    Optional<analFun> findByName(String name);
}
