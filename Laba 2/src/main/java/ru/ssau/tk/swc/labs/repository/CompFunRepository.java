package ru.ssau.tk.swc.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.swc.labs.entity.compFun;
import java.util.List;

@Repository
public interface CompFunRepository extends JpaRepository<compFun, Long>{
    List<compFun> findByName(String name);
}
