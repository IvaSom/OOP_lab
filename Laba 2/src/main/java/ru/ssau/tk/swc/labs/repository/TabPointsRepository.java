package ru.ssau.tk.swc.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.swc.labs.entity.tab_points;
import java.util.Optional;

@Repository
public interface TabPointsRepository extends JpaRepository<tab_points, Long>{
    Optional<tab_points> findByXAndFunctionId(Double x, Long funid);
}
