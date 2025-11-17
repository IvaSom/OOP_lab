package ru.ssau.tk.swc.labs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ssau.tk.swc.labs.entity.tab_points;
import ru.ssau.tk.swc.labs.repository.TabPointsRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TabPointsService {

    private static final Logger logger = LoggerFactory.getLogger(TabPointsService.class);

    private final TabPointsRepository tabPointsRepository;

    public TabPointsService(TabPointsRepository tabPointsRepository) {
        this.tabPointsRepository = tabPointsRepository;
    }

    public Optional<tab_points> findSinglePoint(Double x, Long functionId) {
        logger.info("Одиночный поиск точки: X={}, FunctionID={}", x, functionId);
        Optional<tab_points> result = tabPointsRepository.findByXAndFunctionId(x, functionId);
        logger.info("Одиночный поиск завершен. Найдено: {}", result.isPresent() ? "1 точка" : "0 точек");
        return result;
    }

    public List<tab_points> findMultipleWithSorting(Double minX, Double maxX, Double minY, Double maxY, String sortBy, String direction, Long functionId) {
        logger.info("Множественный поиск точек с сортировкой: X[{}-{}], Y[{}-{}], сортировка по {} {} c functionId {}",
                minX, maxX, minY, maxY, sortBy, direction, functionId);

        List<tab_points> allPoints = tabPointsRepository.findAll();

        List<tab_points> filtered = allPoints.stream()
                .filter(p -> p.getFunction().getId().equals(functionId))
                .filter(p -> p.getX() >= minX && p.getX() <= maxX)
                .filter(p -> p.getY() >= minY && p.getY() <= maxY)
                .toList();

        Comparator<tab_points> comparator = createComparator(sortBy, direction);

        List<tab_points> result = filtered.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        logger.info("Множественный поиск завершен. Найдено точек: {}", result.size());
        return result;
    }

    public List<tab_points> breadthFirstSearch(Double startX, Double radius, Long functionId) {
        logger.info("Поиск в ширину: старт X={}, радиус={}, функция={}", startX, radius, functionId);

        List<tab_points> result = new ArrayList<>();
        Queue<tab_points> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();

        Optional<tab_points> startPoint = tabPointsRepository.findByXAndFunctionId(startX, functionId);
        if (startPoint.isEmpty()) {
            logger.warn("Поиск в ширину: стартовая точка не найдена");
            return result;
        }

        queue.offer(startPoint.get());
        visited.add(startPoint.get().getId());

        while (!queue.isEmpty()) {
            tab_points current = queue.poll();
            result.add(current);

            List<tab_points> neighbors = tabPointsRepository.findAll()
                    .stream()
                    .filter(p -> p.getFunction().getId().equals(functionId))
                    .filter(p -> !visited.contains(p.getId()))
                    .filter(p -> Math.abs(p.getX() - current.getX()) <= radius)
                    .toList();

            for (tab_points neighbor : neighbors) {
                if (visited.add(neighbor.getId())) {
                    queue.offer(neighbor);
                }
            }
        }

        logger.info("Поиск в ширину табличных точек завершен. Обнаружено точек: {}", result.size());
        return result;
    }

    public List<tab_points> findByFunctionId(Long functionId) {
        logger.info("Поиск всех точек по functionId: {}", functionId);

        List<tab_points> result = tabPointsRepository.findAll()
                .stream()
                .filter(p -> p.getFunction().getId().equals(functionId))
                .collect(Collectors.toList());

        logger.info("Найдено точек для функции {}: {}", functionId, result.size());
        return result;
    }

    private Comparator<tab_points> createComparator(String sortBy, String direction) {
        Comparator<tab_points> comparator = switch(sortBy.toLowerCase()) {
            case "x" -> Comparator.comparing(tab_points::getX);
            case "y" -> Comparator.comparing(tab_points::getY);
            case "derive" -> Comparator.comparing(tab_points::getDerive);
            default -> Comparator.comparing(tab_points::getId);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}