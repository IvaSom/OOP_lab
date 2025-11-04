package ru.ssau.tk.swc.labs.functions;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.io.Serializable;
import java.io.Serial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable, Serializable {

    @Serial
    private static final long serialVersionUID = -1240839760745047117L;

    static class Node implements Serializable {

        @Serial
        private static final long serialVersionUID = -311565367373451294L;
        public Node next;
        public Node prev;
        public double x;
        public double y;
    }

    private Node head = null;
    private static final Logger logger = LoggerFactory.getLogger(LinkedListTabulatedFunction.class);

    private void addNode(double x, double y) {
        if (head == null) {
            head = new Node();
            head.x = x;
            head.y = y;
            head.next = head;
            head.prev = head;
        } else {
            Node last = head.prev;
            Node node = new Node();
            last.next = node;
            head.prev = node;
            node.next = head;
            node.prev = last;
            node.x = x;
            node.y = y;

        }
        count++;
        logger.info("Узел добавлен");
    }

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            logger.error("Колличество точек x и y не совпало");
            throw new IllegalArgumentException("Количество точек должно совпадать!");
        }

        if (xValues.length < 2) {
            logger.error("Недостаточно точек для создания функции");
            throw new IllegalArgumentException("Нужно как минимум 2 точки!");
        }

        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                logger.error("Значения x записаны не в порядке возрастания");
                throw new IllegalArgumentException("xValues значения должны находиться в порядке возрастания!");
            }
        }

        for (int i = 0; i < xValues.length; i++) {
            addNode(xValues[i], yValues[i]);
        }
        logger.info("Конструктор LinkedListTabulatedFunction завершил работу");
    }

    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (count < 2) {
            logger.error("Недостаточно точек для создания функции");
            throw new IllegalArgumentException("Нужно как минимум 2 точки!");
        }
        if (xFrom > xTo) {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        double step = (xTo - xFrom) / (count - 1);

        for (int i = 0; i < count; i++) {
            double x = xFrom + i * step;
            double y = source.apply(x);
            addNode(x, y);
        }
        logger.info("Конструктор LinkedListTabulatedFunction завершил работу");
    }

    @Override
    public int getCount() {
        logger.info("Возвращается количество точек функции");
        return count;
    }

    @Override
    public double leftBound() {
        logger.info("Возвращается левая граница");
        return head.x;
    }

    @Override
    public double rightBound() {
        logger.info("Возвращается правая граница");
        return head.prev.x;
    }

    private Node getNode(int index) {
        if (index < 0 || index >= count) {
            logger.error("Невозможный индекс узла");
            throw new IndexOutOfBoundsException("Невозможный индекс!");
        }
        Node temp = head;
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        logger.info("Узел по индексу найден");
        return temp;
    }

    @Override
    public double getX(int index) {
        logger.info("x по индексу найден");
        return getNode(index).x;
    }

    @Override
    public double getY(int index) {
        logger.info("y по индексу найден");
        return getNode(index).y;
    }

    @Override
    public void setY(int index, double value) {
        getNode(index).y = value;
        logger.info("Значение y по указанному индексу установлено");
    }

    @Override
    public int indexOfX(double x) {
        Node temp = head;
        int i = 0;
        do {
            if (temp.x == x) {
                logger.info("Индекс для указанного x найден");
                return i;
            }
            i++;
            temp = temp.next;
        } while (temp != head);
        logger.info("Индекс для указанного x не найден");
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        Node temp = head;
        int i = 0;
        do {
            if (temp.y == y) {
                logger.info("Индекс для указанного y найден");
                return i;
            }
            i++;
            temp = temp.next;
        } while (temp != head);
        logger.info("Индекс для указанного y не найден");
        return -1;
    }

    @Override
    public int floorIndexOfX(double x) { //макс х < заданного
        if (head.prev.x < x) {
            logger.info("x больше правой границы, возвращается индекс последнего x");
            return count;
        }
        if (head.x > x) {
            logger.error("x меньше левой границы");
            throw new IllegalArgumentException("x меньше левой границы");
        }
        Node temp = head.prev; //идем сконнца тк х отсорт.
        int i = count - 1;
        do {
            if (temp.x <= x) {
                logger.info("Место для x найдено");
                return i;
            }
            i--;
            temp = temp.prev;
        } while (temp != head);
        logger.info("Место для x найдено");
        return 0;
    }

    @Override
    public double extrapolateLeft(double x) { //x должен быть между 0 и 1 индексами
        logger.info("Для экстраполяции слева начата интерполяция");
        double temp = interpolate(x, head.x, head.next.x, head.y, head.next.y);
        logger.info("Интерполяция завершена");
        return temp;
    }

    @Override
    public double extrapolateRight(double x) {
        logger.info("Для экстраполяции справа начата интерполяция");
        double temp =  interpolate(x, head.prev.prev.x, head.prev.x, head.prev.prev.y, head.prev.y);
        logger.info("Интерполяция завершена");
        return temp;
    }

    @Override
    public double interpolate(double x, int floorIndex) {
        return interpolate(x, getX(floorIndex), getX(floorIndex + 1), getY(floorIndex), getY(floorIndex + 1));
    }

    @Override
    public void insert(double x, double y) {
        if (x < head.x) {
            insertAtBegin(x, y);
            return;
        }

        Node temp = head;
        int i = 0;
        while (temp.x < x && i < count) {
            i++;
            temp = temp.next;
        }

        if (i == count)
            addNode(x, y);

        else if (temp.x == x)
            temp.y = y;

        else
            insertBeforeNode(temp, x, y);
        logger.info("Точка была добавлена в функцию");
    }

    private void insertAtBegin(double x, double y) {
        Node newNode = new Node();
        newNode.x = x;
        newNode.y = y;

        newNode.next = head;
        newNode.prev = head.prev;
        head.prev = newNode;
        head.next.prev = newNode;
        head = newNode;
        count++;
        logger.info("Точка была добавлена в начало функции");
    }

    private void insertBeforeNode(Node node, double x, double y) {
        Node newNode = new Node();
        newNode.x = x;
        newNode.y = y;

        newNode.next = node;
        newNode.prev = node.prev;
        node.prev = newNode;
        node.next.next = newNode;
        count++;
        logger.info("Точка была добавлена перед указанным узлом функции");
    }

    @Override
    public void remove(int index) {
        Node temp = getNode(index);
        if (temp == temp.next) {
            head = null;
            count = 0;
            return;
        }
        if (temp == head) {
            head = temp.next;
        }
        temp.next.prev = temp.prev;
        temp.prev.next = temp.next;
        count--;
        logger.info("Точка с указанным индексом была удалена");
    }

    @Override
    public Iterator<Point> iterator() {
        logger.info("Итеретор ArrayTabulatedFunction начал работу");
        return new Iterator<Point>() {
            private Node cur = head;
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < count;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    logger.error("Следующего элемента нет");
                    throw new NoSuchElementException("Следующего элемента нет");
                }
                Point point = new Point(cur.x, cur.y);
                cur = cur.next;
                index++;
                return point;
            }
        };
    }
}
