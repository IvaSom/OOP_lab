package ru.ssau.tk.swc.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.swc.labs.entity.composite_structure;
import java.util.List;

@Repository
public interface CompStructureRepository extends JpaRepository<composite_structure, Long> {

}
