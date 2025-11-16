package ru.ssau.tk.swc.labs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ssau.tk.swc.labs.entity.compFun;
import ru.ssau.tk.swc.labs.repository.CompFunRepository;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class CompFunService {

    private static final Logger logger = LoggerFactory.getLogger(CompFunService.class);

    private final CompFunRepository compFunRepository;

    public CompFunService(CompFunRepository compFunRepository) {
        this.compFunRepository = compFunRepository;
    }

    public Optional<compFun> findSingleFunction(String name) {
        logger.info("Одиночный поиск функции по имени: {}", name);
        List<compFun> functions = compFunRepository.findByName(name);
        Optional<compFun> result = functions.stream().findFirst();
        logger.info("Одиночный поиск завершен. Найдено: {}", result.isPresent() ? "1 функция" : "0 функций");
        return result;
    }

    public Optional<compFun> findSingleFunctionById(Long id) {
        logger.info("Одиночный поиск функции по ID: {}", id);
        Optional<compFun> result = compFunRepository.findById(id);
        logger.info("Одиночный поиск по ID завершен. Найдено: {}", result.isPresent() ? "1 функция" : "0 функций");
        return result;
    }


    public List<compFun> findMultipleWithSorting(String[] names, String sortBy, String direction) {
        logger.info("Множественный поиск композитных функций: имена {}, сортировка по {} {}",
                names != null ? String.join(", ", names) : "все", sortBy, direction);

        List<compFun> allFunctions = compFunRepository.findAll();

        List<compFun> filtered = allFunctions.stream()
                .filter(f -> names == null || names.length == 0)
                .toList();

        Comparator<compFun> comparator = createComparator(sortBy, direction);

        List<compFun> result = filtered.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        logger.info("Множественный поиск композитных функций завершен. Найдено функций: {}", result.size());
        return result;
    }

    private Comparator<compFun> createComparator(String sortBy, String direction) {
        Comparator<compFun> comparator = switch(sortBy.toLowerCase()) {
            case "name" -> Comparator.comparing(compFun::getName);
            default -> Comparator.comparing(compFun::getId);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}