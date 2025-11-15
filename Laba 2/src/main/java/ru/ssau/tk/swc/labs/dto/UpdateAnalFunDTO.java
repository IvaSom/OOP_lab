package ru.ssau.tk.swc.labs.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateAnalFunDTO {
    @Size(min = 1, max = 100, message = "Название должно быть от 1 до 100 символов")
    private String name;

    @Min(value = 1, message = "Тип должен быть не менее 1")
    @Max(value = 10, message = "Тип должен быть не более 10")
    private Integer type;
    private static final Logger logger = LoggerFactory.getLogger(UpdateAnalFunDTO.class);

    public UpdateAnalFunDTO() {logger.info("Создан пустой DTO");}

    public UpdateAnalFunDTO(String name, Integer type) {
        this.name = name;
        this.type = type;
        logger.info("Создан DTO по значениям");
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
}
