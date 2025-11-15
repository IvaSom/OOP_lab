package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.dao.AnalFunDAO;
import ru.ssau.tk.swc.labs.entity.*;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AnalFunDTO {
    private Long id;
    private String name;
    private Integer type;

    private static final Logger logger = LoggerFactory.getLogger(AnalFunDTO.class);

    public AnalFunDTO() {logger.info("Создан пустой DTO");}

    public AnalFunDTO(Long id, String name, Integer type) {
        this.id = id;
        this.name = name;
        this.type = type;
        logger.info("Создан DTO по значениям");
    }

    public static AnalFunDTO fromEntity(AnalFun entity) {
        if (entity == null) {
            return null;
        }
        AnalFunDTO temp = new AnalFunDTO(entity.getId(), entity.getName(), entity.getType());
        logger.info("Создан DTO по entity");
        return temp;
    }

    public AnalFun toEntity() {
        AnalFun entity = new AnalFun();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setType(this.type);
        logger.info("DTO переведен в entity");
        return entity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    @Override
    public boolean equals(Object o) {
        logger.info("Производится сравнение DTO");
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalFunDTO that = (AnalFunDTO) o;
        return (Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(type, that.type));
    }

    @Override
    public int hashCode() {
        logger.info("Возвращается хэш-код DTO");
        return Objects.hash(id, name, type);
    }

}
