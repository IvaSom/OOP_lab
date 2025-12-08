package ru.ssau.tk.swc.labs.dto;

import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCompPointDTO {

    @NotNull(message = "x обязателен")
    private double x;

    @NotNull(message = "y обязателен")
    private double y;

    @NotNull(message = "ID функции обязательно")
    private Long funID;

    private static final Logger logger = LoggerFactory.getLogger(CreateCompPointDTO.class);

    public CreateCompPointDTO() {
        logger.info("Создан пустой CreateCompPointDTO");
    }

    public CreateCompPointDTO(double x, double y, Long funID) {
        this.x = x;
        this.y = y;
        this.funID = funID;
        logger.info("Создан CreateCompPointDTO по значениям");
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