package ru.ssau.tk.swc.labs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.swc.labs.entity.analFun;
import ru.ssau.tk.swc.labs.entity.compFun;
import ru.ssau.tk.swc.labs.entity.composite_structure;
import ru.ssau.tk.swc.labs.functions.CompositeFunction;
import ru.ssau.tk.swc.labs.functions.MathFunction;
import ru.ssau.tk.swc.labs.repository.CompStructureRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreateCompositeFunctionService {

    @Autowired
    private CompStructureRepository compStructureRepository;

    @Autowired
    private MathFunctionFactory mathFunctionFactory;

    @Transactional(readOnly = true)
    public MathFunction buildCompositeFunction(compFun compositeFunction) {
        List<composite_structure> structures = compStructureRepository.findAll().stream()
                .filter(structure -> structure.getCompFun().getId().equals(compositeFunction.getId()))
                .sorted(Comparator.comparing(composite_structure::getExecutionOrder))
                .toList();

        if (structures.isEmpty()) {
            throw new IllegalArgumentException("No analytic functions found for composite function: " + compositeFunction.getName());
        }

        MathFunction resultFunction = mathFunctionFactory.createMathFunction(structures.get(0).getAnalFun());

        for (int i = 1; i < structures.size(); i++) {
            MathFunction nextFunction = mathFunctionFactory.createMathFunction(structures.get(i).getAnalFun());
            resultFunction = new CompositeFunction(resultFunction, nextFunction);
        }

        return resultFunction;
    }
}