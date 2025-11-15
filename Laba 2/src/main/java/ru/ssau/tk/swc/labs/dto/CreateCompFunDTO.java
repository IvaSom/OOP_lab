package ru.ssau.tk.swc.labs.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCompFunDTO {

    @NotBlank(message = "Название композитной функции обязательно")
    @Size(min = 1, max = 50, message = "Название должно быть от 1 до 50 символов")
    private String name;

    private static final Logger logger = LoggerFactory.getLogger(CreateCompFunDTO.class);

    public CreateCompFunDTO() {
        logger.info("Создан пустой CreateCompFunDTO");
    }

    public CreateCompFunDTO(String name) {
        this.name = name;
        logger.info("Создан CreateCompFunDTO по значениям");
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
