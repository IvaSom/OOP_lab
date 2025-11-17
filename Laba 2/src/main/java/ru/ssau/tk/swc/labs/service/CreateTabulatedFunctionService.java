package ru.ssau.tk.swc.labs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.tabFun;
import ru.ssau.tk.swc.labs.entity.tab_points;
import ru.ssau.tk.swc.labs.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.swc.labs.functions.TabulatedFunction;
import ru.ssau.tk.swc.labs.repository.TabPointsRepository;

import java.util.Comparator;
import java.util.List;

@Service
public class CreateTabulatedFunctionService {

    @Autowired
    private TabPointsRepository tabPointsRepository;

    @Transactional(readOnly = true)
    public TabulatedFunction createTabulatedFunction(tabFun tableFunction) {
        List<tab_points> points = tabPointsRepository.findAll().stream()
                .filter(point -> point.getFunction().getId().equals(tableFunction.getId()))
                .sorted(Comparator.comparing(tab_points::getX))
                .toList();

        if (points.size() < 2) {
            throw new IllegalArgumentException("Для создания табличной функции нужно как минимум 2 точки. Найдено: " + points.size());
        }

        double[] xValues = new double[points.size()];
        double[] yValues = new double[points.size()];

        for (int i = 0; i < points.size(); i++) {
            tab_points point = points.get(i);
            xValues[i] = point.getX();
            yValues[i] = point.getY();
        }

        return new LinkedListTabulatedFunction(xValues, yValues);
    }


    //получение массива производных
    @Transactional(readOnly = true)
    public double[] getDerivatives(tabFun tableFunction) {
        List<tab_points> points = tabPointsRepository.findAll().stream()
                .filter(point -> point.getFunction().getId().equals(tableFunction.getId()))
                .sorted(Comparator.comparing(tab_points::getX))
                .toList();

        double[] derivatives = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            derivatives[i] = points.get(i).getDerive();
        }

        return derivatives;
    }
}