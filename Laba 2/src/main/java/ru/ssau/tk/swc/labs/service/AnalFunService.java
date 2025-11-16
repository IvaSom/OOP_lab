package ru.ssau.tk.swc.labs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ssau.tk.swc.labs.entity.analFun;
import ru.ssau.tk.swc.labs.repository.AnalFunRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalFunService {

    private static final Logger logger = LoggerFactory.getLogger(AnalFunService.class);

    private final AnalFunRepository analFunRepository;

    public AnalFunService(AnalFunRepository analFunRepository) {
        this.analFunRepository = analFunRepository;
    }

    public Optional<analFun> findSingleFunction(String name) {
        logger.info("Одиночный поиск функции по имени: {}", name);
        Optional<analFun> result = analFunRepository.findByName(name);
        logger.info("Одиночный поиск завершен. Найдено: {}", result.isPresent() ? "1 функция" : "0 функций");
        return result;
    }

    public List<analFun> findMultipleWithSorting(Integer[] types, String namePattern, String sortBy, String direction) {
        logger.info("Множественный поиск функций: типы: {}, паттерн имени: '{}', сортировка по {} {}",
                Arrays.toString(types), namePattern, sortBy, direction);

        List<analFun> allFunctions = analFunRepository.findAll();

        List<analFun> filtered = allFunctions.stream()
                .filter(f -> types == null || types.length == 0 || Arrays.asList(types).contains(f.getType()))
                .filter(f -> namePattern == null || f.getName().toLowerCase().contains(namePattern.toLowerCase()))
                .toList();

        Comparator<analFun> comparator = createComparator(sortBy, direction);

        List<analFun> result = filtered.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        logger.info("Множественный поиск завершен. Найдено функций: {}", result.size());
        return result;
    }



    //приватные методы
    private Comparator<analFun> createComparator(String sortBy, String direction) {
        Comparator<analFun> comparator = switch(sortBy.toLowerCase()) {
            case "name" -> Comparator.comparing(analFun::getName);
            case "type" -> Comparator.comparing(analFun::getType);
            default -> Comparator.comparing(analFun::getId);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}