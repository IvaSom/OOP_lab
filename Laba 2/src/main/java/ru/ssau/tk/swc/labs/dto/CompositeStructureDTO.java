package ru.ssau.tk.swc.labs.dto;

public class CompositeStructureDTO {
    private Long id;
    private Long compositeFunctionId;
    private Long analyticFunctionId;
    private Integer executionOrder;

    public CompositeStructureDTO() {}

    public CompositeStructureDTO(Long id, Long compositeFunctionId, Long analyticFunctionId, Integer executionOrder) {
        this.id = id;
        this.compositeFunctionId = compositeFunctionId;
        this.analyticFunctionId = analyticFunctionId;
        this.executionOrder = executionOrder;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCompositeFunctionId() { return compositeFunctionId; }
    public void setCompositeFunctionId(Long compositeFunctionId) { this.compositeFunctionId = compositeFunctionId; }
    public Long getAnalyticFunctionId() { return analyticFunctionId; }
    public void setAnalyticFunctionId(Long analyticFunctionId) { this.analyticFunctionId = analyticFunctionId; }
    public Integer getExecutionOrder() { return executionOrder; }
    public void setExecutionOrder(Integer executionOrder) { this.executionOrder = executionOrder; }
}