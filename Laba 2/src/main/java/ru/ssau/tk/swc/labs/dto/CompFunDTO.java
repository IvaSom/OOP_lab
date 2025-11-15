package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.CompFun;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompFunDTO {
    private Long id;
    private String name;

    private static final Logger logger = LoggerFactory.getLogger(CompFunDTO.class);

    public CompFunDTO() {
        logger.info("Создан пустой CompFunDTO");
    }

    public CompFunDTO(Long id, String name) {
        this.id = id;
        this.name = name;
        logger.info("Создан CompFunDTO по значениям");
    }

    public static CompFunDTO fromEntity(CompFun entity) {
        if (entity == null) {
            return null;
        }
        CompFunDTO temp = new CompFunDTO(entity.getId(), entity.getName());
        logger.info("Создан CompFunDTO по entity");
        return temp;
    }

    public CompFun toEntity() {
        CompFun entity = new CompFun();
        entity.setId(this.id);
        entity.setName(this.name);
        logger.info("CompFunDTO переведен в entity");
        return entity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        logger.info("Производится сравнение CompFunDTO");
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompFunDTO that = (CompFunDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        logger.info("Возвращается хэш-код CompFunDTO");
        return Objects.hash(id, name);
    }

}