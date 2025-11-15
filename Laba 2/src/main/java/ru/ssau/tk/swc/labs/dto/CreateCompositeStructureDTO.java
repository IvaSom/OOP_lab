package ru.ssau.tk.swc.labs.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCompositeStructureDTO {

    @NotNull(message = "ID композитной функции обязателен")
    private Long compositeId;

    @NotNull(message = "ID аналитической функции обязателен")
    private Long analyticId;

    @NotNull(message = "Порядок выполнения обязателен")
    @Min(value = 1, message = "Порядок выполнения должен быть не менее 1")
    private Integer executionOrder;

    private static final Logger logger = LoggerFactory.getLogger(CreateCompositeStructureDTO.class);

    public CreateCompositeStructureDTO() {
        logger.info("Создан пустой CreateCompositeStructureDTO");
    }

    public CreateCompositeStructureDTO(Long compositeId, Long analyticId, Integer executionOrder) {
        this.compositeId = compositeId;
        this.analyticId = analyticId;
        this.executionOrder = executionOrder;
        logger.info("Создан CreateCompositeStructureDTO по значениям");
    }

    public Long getCompositeId() { return compositeId; }
    public void setCompositeId(Long compositeId) { this.compositeId = compositeId; }

    public Long getAnalyticId() { return analyticId; }
    public void setAnalyticId(Long analyticId) { this.analyticId = analyticId; }

    public Integer getExecutionOrder() { return executionOrder; }
    public void setExecutionOrder(Integer executionOrder) { this.executionOrder = executionOrder; }
}