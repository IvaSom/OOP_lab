package ru.ssau.tk.swc.labs.dto;

import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTabPointDTO {

    @NotNull(message = "x обязателен")
    private double x;

    @NotNull(message = "y обязателен")
    private double y;

    @NotNull(message = "производная обязательна")
    private double derive;

    @NotNull(message = "ID табулированной функции обязательно")
    private Long funID;

    private static final Logger logger = LoggerFactory.getLogger(CreateTabPointDTO.class);

    public CreateTabPointDTO() {
        logger.info("Создан пустой CreateTabPointDTO");
    }

    public CreateTabPointDTO(double x, double y, double derive, Long funID) {
        this.x = x;
        this.y = y;
        this.derive = derive;
        this.funID = funID;
        logger.info("Создан CreateTabPointDTO по значениям");
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
}