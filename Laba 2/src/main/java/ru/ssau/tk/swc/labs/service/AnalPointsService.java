package ru.ssau.tk.swc.labs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ssau.tk.swc.labs.entity.anal_points;
import ru.ssau.tk.swc.labs.repository.AnalPointsRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalPointsService {

    private static final Logger logger = LoggerFactory.getLogger(AnalPointsService.class);

    private final AnalPointsRepository analPointsRepository;

    public AnalPointsService(AnalPointsRepository analPointsRepository) {
        this.analPointsRepository = analPointsRepository;
    }

    public Optional<anal_points> findSinglePoint(Double x, Long functionId) {
        logger.info("Одиночный поиск: X={}, FunctionID={}", x, functionId);
        Optional<anal_points> result = analPointsRepository.findByXAndFunId(x, functionId);
        logger.info("Одиночный поиск завершен. Найдено: {}", result.isPresent() ? "1 точка" : "0 точек");
        return result;
    }

    public List<anal_points> findMultipleWithSorting(Double minX, Double maxX, Double minY, Double maxY, String sortBy, String direction) {
        logger.info("Множественный поиск с сортировкой: X[{}-{}], Y[{}-{}], сортировка по {} {}", minX, maxX, minY, maxY, sortBy, direction);

        List<anal_points> allPoints = analPointsRepository.findAll();

        //фильтруем
        List<anal_points> filtered = allPoints.stream()
                .filter(p -> p.getX() >= minX && p.getX() <= maxX)
                .filter(p -> p.getY() >= minY && p.getY() <= maxY)
                .toList();

        Comparator<anal_points> comparator = createComparator(sortBy, direction);

        List<anal_points> result = filtered.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        logger.info("Множественный поиск завершен. Найдено точек: {}", result.size());
        return result;
    }

    //в ширину bfs
    public List<anal_points> breadthFirstSearch(Double startX, Double radius, Long functionId) {
        logger.info("Поиск в ширину: старт X={}, радиус={}, функция={}", startX, radius, functionId);

        List<anal_points> result = new ArrayList<>();
        Queue<anal_points> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();

        Optional<anal_points> startPoint = analPointsRepository.findByXAndFunId(startX, functionId);
        if (startPoint.isEmpty()) {
            logger.warn("Поиск в ширину: стартовая точка не найдена");
            return result;
        }

        queue.offer(startPoint.get());
        visited.add(startPoint.get().getId());

        while (!queue.isEmpty()) {
            anal_points current = queue.poll();
            result.add(current);

            List<anal_points> neighbors = analPointsRepository.findByFunId(functionId)
                    .stream()
                    .filter(p -> !visited.contains(p.getId()))
                    .filter(p -> Math.abs(p.getX() - current.getX()) <= radius)
                    .toList();

            for (anal_points neighbor : neighbors) {
                if (visited.add(neighbor.getId())) {
                    queue.offer(neighbor);
                }
            }
        }

        logger.info("Поиск в ширину завершен. Обнаружено точек: {}", result.size());
        return result;
    }

    //в глубину по производной
    public List<anal_points> depthFirstSearchByDerivative(Double startDerive, Long functionId) {
        logger.info("Поиск в глубину по производной: старт производная={}, функция={}", startDerive, functionId);

        List<anal_points> result = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        List<anal_points> startPoints = analPointsRepository.findByFunId(functionId)
                .stream()
                .filter(p -> Math.abs(p.getDerive() - startDerive) < 0.001)
                .toList();

        for (anal_points start : startPoints) {
            dfsByDerivative(start, result, visited, functionId);
        }

        logger.info("DFS поиск завершен. Обнаружено точек: {}", result.size());
        return result;
    }

    //приватные методы
    private Comparator<anal_points> createComparator(String sortBy, String direction) {
        Comparator<anal_points> comparator = switch(sortBy.toLowerCase()) {
            case "x" -> Comparator.comparing(anal_points::getX);
            case "y" -> Comparator.comparing(anal_points::getY);
            case "derive" -> Comparator.comparing(anal_points::getDerive);
            default -> Comparator.comparing(anal_points::getId);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    private void dfsByDerivative(anal_points current, List<anal_points> result, Set<Long> visited, Long functionId) {
        if (visited.contains(current.getId())) return;

        visited.add(current.getId());
        result.add(current);

        List<anal_points> similarDerivativePoints = analPointsRepository.findByFunId(functionId) //все точки нашей функции
                .stream()
                .filter(p -> !visited.contains(p.getId())) //фильтруем непосещенные
                .filter(p -> Math.abs(p.getDerive() - current.getDerive()) < 0.1) //оставили с близкими знач
                .toList();

        for (anal_points next : similarDerivativePoints) {
            dfsByDerivative(next, result, visited, functionId);
        }
    }

}