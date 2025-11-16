package ru.ssau.tk.swc.labs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ssau.tk.swc.labs.entity.tabFun;
import ru.ssau.tk.swc.labs.repository.TabFunRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TabFunService {

    private static final Logger logger = LoggerFactory.getLogger(TabFunService.class);

    private final TabFunRepository tabFunRepository;

    public TabFunService(TabFunRepository tabFunRepository) {
        this.tabFunRepository = tabFunRepository;
    }

    public Optional<tabFun> findSingleFunction(String name) {
        logger.info("Одиночный поиск функции по имени: {}", name);
        List<tabFun> functions = tabFunRepository.findByName(name);
        Optional<tabFun> result = functions.stream().findFirst();
        logger.info("Одиночный поиск завершен. Найдено: {}", result.isPresent() ? "1 функция" : "0 функций");
        return result;
    }

    public Optional<tabFun> findSingleFunctionById(Long id) {
        logger.info("Одиночный поиск функции по ID: {}", id);
        Optional<tabFun> result = tabFunRepository.findById(id);
        logger.info("Одиночный поиск по ID завершен. Найдено: {}", result.isPresent() ? "1 функция" : "0 функций");
        return result;
    }

    public List<tabFun> findMultipleWithSorting(Integer[] types, String sortBy, String direction) {
        logger.info("Множественный поиск функций: типы {}, сортировка по {} {}",
                types != null ? Arrays.toString(types) : "все", sortBy, direction);

        List<tabFun> allFunctions = tabFunRepository.findAll();

        List<tabFun> filtered = allFunctions.stream()
                .filter(f -> types == null || types.length == 0 || Arrays.asList(types).contains(f.getType()))
                .toList();

        Comparator<tabFun> comparator = createComparator(sortBy, direction);

        List<tabFun> result = filtered.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        logger.info("Множественный поиск функций завершен. Найдено функций: {}", result.size());
        return result;
    }


    private Comparator<tabFun> createComparator(String sortBy, String direction) {
        Comparator<tabFun> comparator = switch(sortBy.toLowerCase()) {
            case "type" -> Comparator.comparing(tabFun::getType);
            default -> Comparator.comparing(tabFun::getId);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}