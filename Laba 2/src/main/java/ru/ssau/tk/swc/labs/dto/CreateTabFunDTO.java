package ru.ssau.tk.swc.labs.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTabFunDTO {

    @NotBlank(message = "Тип табулированной функции обязателен")
    @Size(min = 1, max = 50, message = "Тип должен быть от 1 до 50 символов")
    private String type;

    private static final Logger logger = LoggerFactory.getLogger(CreateTabFunDTO.class);

    public CreateTabFunDTO() {
        logger.info("Создан пустой CreateTabFunDTO");
    }

    public CreateTabFunDTO(String type) {
        this.type = type;
        logger.info("Создан CreateTabFunDTO по значениям");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
