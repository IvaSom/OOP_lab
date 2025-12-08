package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.*;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalPointDTO {
    private Long id;
    private double x;
    private double y;
    private Long funID;

    private static final Logger logger = LoggerFactory.getLogger(AnalPointDTO.class);
    public AnalPointDTO() {logger.info("Создан пустой AnalPointDTO");}

    public AnalPointDTO(Long id, double x, double y, Long funID){
        this.id = id;
        this.x = x;
        this.y = y;
        this.funID = funID;
        logger.info("Создан AnalPointDTO по значениям");
    }

    public static AnalPointDTO fromEntity(AnalPoint entity) {
        if (entity == null) {
            return null;
        }
        AnalPointDTO temp = new AnalPointDTO(entity.getId(), entity.getX(), entity.getY(), entity.getFunID());
        logger.info("Создан AnalPointDTO по entity");
        return temp;
    }

    public AnalPoint toEntity() {
        AnalPoint entity = new AnalPoint();
        entity.setId(this.id);
        entity.setX(this.x);
        entity.setY(this.y);
        entity.setFunID(this.funID);
        logger.info("AnalPointDTO переведен в entity");
        return entity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFunID(Long funID) {
        this.funID = funID;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Long getFunID() {
        return funID;
    }

    @Override
    public boolean equals(Object o) {
        logger.info("Производится сравнение AnalPointDTO");
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalPointDTO that = (AnalPointDTO) o;
        return (Objects.equals(id, that.id) && Objects.equals(x, that.x) && Objects.equals(y, that.y) && Objects.equals(funID, that.funID));
    }

    @Override
    public int hashCode() {
        logger.info("Возвращается хэш-код AnalPointDTO");
        return Objects.hash(id, x, y, funID);
    }
}
