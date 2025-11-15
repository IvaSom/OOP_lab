package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.TabFun;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabFunDTO {
    private Long id;
    private String type;

    private static final Logger logger = LoggerFactory.getLogger(TabFunDTO.class);

    public TabFunDTO() {
        logger.info("Создан пустой TabFunDTO");
    }

    public TabFunDTO(Long id, String type) {
        this.id = id;
        this.type = type;
        logger.info("Создан TabFunDTO по значениям");
    }

    public static TabFunDTO fromEntity(TabFun entity) {
        if (entity == null) {
            return null;
        }
        TabFunDTO temp = new TabFunDTO(entity.getId(), entity.getType());
        logger.info("Создан TabFunDTO по entity");
        return temp;
    }

    public TabFun toEntity() {
        TabFun entity = new TabFun();
        entity.setId(this.id);
        entity.setType(this.type);
        logger.info("TabFunDTO переведен в entity");
        return entity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        logger.info("Производится сравнение TabFunDTO");
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TabFunDTO that = (TabFunDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        logger.info("Возвращается хэш-код TabFunDTO");
        return Objects.hash(id, type);
    }
}