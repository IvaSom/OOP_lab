package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.CompPoint;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompPointDTO {
    private Long id;
    private double x;
    private double y;
    private double derive;
    private Long funID;

    private static final Logger logger = LoggerFactory.getLogger(CompPointDTO.class);

    public CompPointDTO() {
        logger.info("Создан пустой CompPointDTO");
    }

    public CompPointDTO(Long id, double x, double y, double derive, Long funID) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.derive = derive;
        this.funID = funID;
        logger.info("Создан CompPointDTO по значениям");
    }

    public static CompPointDTO fromEntity(CompPoint entity) {
        if (entity == null) {
            return null;
        }
        CompPointDTO temp = new CompPointDTO(entity.getId(), entity.getX(), entity.getY(), entity.getDerive(), entity.getFunID());
        logger.info("Создан CompPointDTO по entity");
        return temp;
    }

    public CompPoint toEntity() {
        CompPoint entity = new CompPoint();
        entity.setId(this.id);
        entity.setX(this.x);
        entity.setY(this.y);
        entity.setDerive(this.derive);
        entity.setFunID(this.funID);
        logger.info("CompPointDTO переведен в entity");
        return entity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDerive(double derive) {
        this.derive = derive;
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

    public double getDerive() {
        return derive;
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
        logger.info("Производится сравнение CompPointDTO");
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompPointDTO that = (CompPointDTO) o;
        return (Objects.equals(id, that.id) &&
                Objects.equals(x, that.x) &&
                Objects.equals(y, that.y) &&
                Objects.equals(derive, that.derive) &&
                Objects.equals(funID, that.funID));
    }

    @Override
    public int hashCode() {
        logger.info("Возвращается хэш-код CompPointDTO");
        return Objects.hash(id, x, y, derive, funID);
    }
}