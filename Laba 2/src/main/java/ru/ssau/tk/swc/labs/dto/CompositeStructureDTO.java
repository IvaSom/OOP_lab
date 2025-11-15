package ru.ssau.tk.swc.labs.dto;

import ru.ssau.tk.swc.labs.entity.CompositeStructure;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositeStructureDTO {
    private Long id;
    private Long compositeId;
    private Long analyticId;
    private Integer executionOrder;

    private static final Logger logger = LoggerFactory.getLogger(CompositeStructureDTO.class);

    public CompositeStructureDTO() {
        logger.info("Создан пустой CompositeStructureDTO");
    }

    public CompositeStructureDTO(Long id, Long compositeId, Long analyticId, Integer executionOrder) {
        this.id = id;
        this.compositeId = compositeId;
        this.analyticId = analyticId;
        this.executionOrder = executionOrder;
        logger.info("Создан CompositeStructureDTO по значениям");
    }

    public static CompositeStructureDTO fromEntity(CompositeStructure entity) {
        if (entity == null) {
            return null;
        }
        CompositeStructureDTO temp = new CompositeStructureDTO(
                entity.getId(),
                entity.getComposite_id(),
                entity.getAnalytic_id(),
                entity.getOrder()
        );
        logger.info("Создан CompositeStructureDTO по entity");
        return temp;
    }

    public CompositeStructure toEntity() {
        CompositeStructure entity = new CompositeStructure();
        entity.setId(this.id);
        entity.setComposite_id(this.compositeId);
        entity.setAnalytic_id(this.analyticId);
        entity.setOrder(this.executionOrder);
        logger.info("CompositeStructureDTO переведен в entity");
        return entity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompositeId() { return compositeId; }
    public void setCompositeId(Long compositeId) { this.compositeId = compositeId; }

    public Long getAnalyticId() { return analyticId; }
    public void setAnalyticId(Long analyticId) { this.analyticId = analyticId; }

    public Integer getExecutionOrder() { return executionOrder; }
    public void setExecutionOrder(Integer executionOrder) { this.executionOrder = executionOrder; }

    @Override
    public boolean equals(Object o) {
        logger.info("Производится сравнение CompositeStructureDTO");
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositeStructureDTO that = (CompositeStructureDTO) o;
        return (Objects.equals(id, that.id) &&
                Objects.equals(compositeId, that.compositeId) &&
                Objects.equals(analyticId, that.analyticId) &&
                Objects.equals(executionOrder, that.executionOrder));
    }

    @Override
    public int hashCode() {
        logger.info("Возвращается хэш-код CompositeStructureDTO");
        return Objects.hash(id, compositeId, analyticId, executionOrder);
    }

}