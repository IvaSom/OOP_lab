package ru.ssau.tk.swc.labs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ssau.tk.swc.labs.entity.composite_structure;
import ru.ssau.tk.swc.labs.repository.CompStructureRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompStructureService {

    private static final Logger logger = LoggerFactory.getLogger(CompStructureService.class);

    private final CompStructureRepository compStructureRepository;

    public CompStructureService(CompStructureRepository compStructureRepository) {
        this.compStructureRepository = compStructureRepository;
    }

    public Optional<composite_structure> findSingleStructure(Long id) {
        logger.info("Одиночный поиск композитной структуры по ID: {}", id);
        Optional<composite_structure> result = compStructureRepository.findById(id);
        logger.info("Одиночный поиск завершен. Найдено: {}", result.isPresent() ? "1 структура" : "0 структур");
        return result;
    }
    public List<composite_structure> findByCompositeFunctionId(Long compositeFunctionId) {
        logger.info("Поиск структур по композитной функции: {}", compositeFunctionId);

        List<composite_structure> result = compStructureRepository.findAll()
                .stream()
                .filter(s -> s.getCompFun().getId().equals(compositeFunctionId))
                .collect(Collectors.toList());

        logger.info("Найдено структур для композитной функции {}: {}", compositeFunctionId, result.size());
        return result;
    }

    public List<composite_structure> findMultipleWithSorting(Long compositeFunctionId, String sortBy, String direction) {
        logger.info("Множественный поиск структур: композитная функция={}, сортировка по {} {}",
                compositeFunctionId, sortBy, direction);

        List<composite_structure> allStructures = compStructureRepository.findAll();

        List<composite_structure> filtered = allStructures.stream()
                .filter(s -> compositeFunctionId == null || s.getCompFun().getId().equals(compositeFunctionId))
                .toList();

        Comparator<composite_structure> comparator = createComparator(sortBy, direction);

        List<composite_structure> result = filtered.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        logger.info("Множественный поиск структур завершен. Найдено структур: {}", result.size());
        return result;
    }

    public List<composite_structure> findByAnalyticFunctionId(Long analyticFunctionId) {
        logger.info("Поиск структур по аналитической функции: {}", analyticFunctionId);

        List<composite_structure> result = compStructureRepository.findAll()
                .stream()
                .filter(s -> s.getAnalFun().getId().equals(analyticFunctionId))
                .collect(Collectors.toList());

        logger.info("Найдено структур для аналитической функции {}: {}", analyticFunctionId, result.size());
        return result;
    }


    private Comparator<composite_structure> createComparator(String sortBy, String direction) {
        Comparator<composite_structure> comparator = switch(sortBy.toLowerCase()) {
            case "order" -> Comparator.comparing(composite_structure::getExecutionOrder);
            case "composite" -> Comparator.comparing(s -> s.getCompFun().getId());
            case "analytic" -> Comparator.comparing(s -> s.getAnalFun().getId());
            default -> Comparator.comparing(composite_structure::getId);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

}