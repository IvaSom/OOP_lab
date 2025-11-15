package ru.ssau.tk.swc.labs.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateAnalFunDTO {

    @NotBlank(message = "название функции обязательно")
    @Size(min = 1, max = 100, message = "название должно быть от 1 до 100 символов")
    private String name;

    @NotBlank(message = "тип функции обязателен")
    @Min(value = 1, message = "Тип должен быть не менее 1")
    @Max(value = 10, message = "Тип должен быть не более 10")
    private Integer type;
    private static final Logger logger = LoggerFactory.getLogger(CreateAnalFunDTO.class);

    public CreateAnalFunDTO() {logger.info("Создан пустой DTO");}

    public CreateAnalFunDTO(String name, Integer type) {
        this.name = name;
        this.type = type;
        logger.info("Создан DTO по значениям");
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
}
