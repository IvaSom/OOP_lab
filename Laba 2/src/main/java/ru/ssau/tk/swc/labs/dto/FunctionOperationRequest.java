package ru.ssau.tk.swc.labs.dto;

public class FunctionOperationRequest {
    private Long functionId1;
    private Long functionId2;
    private String operation;

    public FunctionOperationRequest() {}

    public FunctionOperationRequest(Long functionId1, Long functionId2, String operation) {
        this.functionId1 = functionId1;
        this.functionId2 = functionId2;
        this.operation = operation;
    }

    public Long getFunctionId1() { return functionId1; }
    public void setFunctionId1(Long functionId1) { this.functionId1 = functionId1; }

    public Long getFunctionId2() { return functionId2; }
    public void setFunctionId2(Long functionId2) { this.functionId2 = functionId2; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
}