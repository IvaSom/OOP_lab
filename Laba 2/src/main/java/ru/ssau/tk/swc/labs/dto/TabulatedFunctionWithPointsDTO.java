package ru.ssau.tk.swc.labs.dto;

import java.util.List;

public class TabulatedFunctionWithPointsDTO {
    private TabFunDTO function;
    private List<TabPointsDTO> points;

    public TabulatedFunctionWithPointsDTO() {}

    public TabulatedFunctionWithPointsDTO(TabFunDTO function, List<TabPointsDTO> points) {
        this.function = function;
        this.points = points;
    }

    public TabFunDTO getFunction() { return function; }
    public void setFunction(TabFunDTO function) { this.function = function; }

    public List<TabPointsDTO> getPoints() { return points; }
    public void setPoints(List<TabPointsDTO> points) { this.points = points; }
}