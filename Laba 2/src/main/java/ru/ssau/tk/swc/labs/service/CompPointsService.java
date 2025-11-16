package ru.ssau.tk.swc.labs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ssau.tk.swc.labs.entity.anal_points;
import ru.ssau.tk.swc.labs.entity.comp_points;
import ru.ssau.tk.swc.labs.repository.CompPointsRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompPointsService {

    private static final Logger logger = LoggerFactory.getLogger(CompPointsService.class);

    private final CompPointsRepository compPointsRepository;

    public CompPointsService(CompPointsRepository compPointsRepository) {
        this.compPointsRepository = compPointsRepository;
    }

    public Optional<comp_points> findSinglePoint(Double x, Long functionId) {
        logger.info("Одиночный поиск композитной точки: X={}, FunctionID={}", x, functionId);
        Optional<comp_points> result = compPointsRepository.findByXAndFubID(x, functionId);
        logger.info("Одиночный поиск завершен. Найдено: {}", result.isPresent() ? "1 точка" : "0 точек");
        return result;
    }

    public List<comp_points> findMultipleWithSorting(Double minX, Double maxX, Double minY, Double maxY, String sortBy, String direction, Long functionId) {
        logger.info("Множественный поиск точек с сортировкой: X[{}-{}], Y[{}-{}], сортировка по {} {} c functionId {}",
                minX, maxX, minY, maxY, sortBy, direction, functionId);

        List<comp_points> allPoints = compPointsRepository.findAll();

        List<comp_points> filtered = allPoints.stream()
                .filter(p -> p.getFunction().getId().equals(functionId))
                .filter(p -> p.getX() >= minX && p.getX() <= maxX)
                .filter(p -> p.getY() >= minY && p.getY() <= maxY)
                .toList();

        Comparator<comp_points> comparator = createComparator(sortBy, direction);

        List<comp_points> result = filtered.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        logger.info("Множественный поиск композитных точек завершен. Найдено точек: {}", result.size());
        return result;
    }

    public List<comp_points> breadthFirstSearch(Double startX, Double radius, Long functionId) {
        logger.info("Поиск в ширину композитных точек: старт X={}, радиус={}, функция={}", startX, radius, functionId);

        List<comp_points> result = new ArrayList<>();
        Queue<comp_points> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();

        Optional<comp_points> startPoint = compPointsRepository.findByXAndFubID(startX, functionId);
        if (startPoint.isEmpty()) {
            logger.warn("Поиск в ширину: стартовая композитная точка не найдена");
            return result;
        }

        queue.offer(startPoint.get());
        visited.add(startPoint.get().getId());

        while (!queue.isEmpty()) {
            comp_points current = queue.poll();
            result.add(current);

            List<comp_points> neighbors = compPointsRepository.findAll()
                    .stream()
                    .filter(p -> p.getFunction().getId().equals(functionId))
                    .filter(p -> !visited.contains(p.getId()))
                    .filter(p -> Math.abs(p.getX() - current.getX()) <= radius)
                    .toList();

            for (comp_points neighbor : neighbors) {
                if (visited.add(neighbor.getId())) {
                    queue.offer(neighbor);
                }
            }
        }

        logger.info("Поиск в ширину композитных точек завершен. Обнаружено точек: {}", result.size());
        return result;
    }


    public List<comp_points> findByFunctionId(Long functionId) {
        logger.info("Поиск всех точек по functionId: {}", functionId);

        List<comp_points> result = compPointsRepository.findAll()
                .stream()
                .filter(p -> p.getFunction().getId().equals(functionId))
                .collect(Collectors.toList());

        logger.info("Найдено точек для функции {}: {}", functionId, result.size());
        return result;
    }
    private Comparator<comp_points> createComparator(String sortBy, String direction) {
        Comparator<comp_points> comparator = switch(sortBy.toLowerCase()) {
            case "x" -> Comparator.comparing(comp_points::getX);
            case "y" -> Comparator.comparing(comp_points::getY);
            case "derive" -> Comparator.comparing(comp_points::getDerive);
            default -> Comparator.comparing(comp_points::getId);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

}