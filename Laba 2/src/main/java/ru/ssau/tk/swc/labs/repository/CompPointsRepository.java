package ru.ssau.tk.swc.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.swc.labs.entity.comp_points;
import java.util.Optional;

@Repository
public interface CompPointsRepository extends JpaRepository<comp_points, Long>{
    Optional<comp_points> findByXAndFubID(Double x, Long FunID);
}
