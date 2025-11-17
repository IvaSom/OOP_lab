package ru.ssau.tk.swc.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.swc.labs.entity.anal_points;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalPointsRepository extends JpaRepository<anal_points, Long>{
    List<anal_points> findByX(Double x);
    List<anal_points> findByY(Double y);
    List<anal_points> findByDerive(Double derive);
    List<anal_points> findByFunctionId(Long funId);
    Optional<anal_points> findByXAndFunctionId(Double x, Long funId);
}
