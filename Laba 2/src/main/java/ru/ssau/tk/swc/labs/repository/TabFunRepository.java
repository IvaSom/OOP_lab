package ru.ssau.tk.swc.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.swc.labs.entity.tabFun;
import java.util.List;

@Repository
public interface TabFunRepository extends JpaRepository<tabFun, Long>{
    List<tabFun> findByName(String name);
}
