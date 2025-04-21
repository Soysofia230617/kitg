package org.example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class IntervalGraphCheckerTest {
    private IntervalGraphChecker checker;

    @BeforeEach
    public void setUp() {
        // Допустим, что класс IntervalGraphChecker будет создан
        checker = new IntervalGraphChecker();
    }

    // Тест 1: Интервальный граф из примера Markdown-документа (7 вершин)
    // Проверяет, что граф, построенный из пересечений интервалов, корректно классифицируется
    @Test
    public void testExampleIntervalGraph() {
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("A", Arrays.asList("B", "C", "E", "G"));
        graph.put("B", Arrays.asList("A", "C", "D", "E", "F", "G"));
        graph.put("C", Arrays.asList("A", "B", "D", "E", "F", "G"));
        graph.put("D", Arrays.asList("B", "C", "E", "F", "G"));
        graph.put("E", Arrays.asList("A", "B", "C", "D", "G"));
        graph.put("F", Arrays.asList("B", "C", "D", "G"));
        graph.put("G", Arrays.asList("A", "B", "C", "D", "E", "F"));

        assertTrue(checker.isIntervalGraph(graph), "Граф из примера должен быть интервальным");
    }

    // Тест 2: Простой путь P_4 (1-2-3-4)
    // Проверяет обработку простых интервальных графов без циклов
    @Test
    public void testPathGraphP4() {
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("1", Arrays.asList("2"));
        graph.put("2", Arrays.asList("1", "3"));
        graph.put("3", Arrays.asList("2", "4"));
        graph.put("4", Arrays.asList("3"));

        assertTrue(checker.isIntervalGraph(graph), "Путь P_4 должен быть интервальным");
    }

    // Тест 3: Длинный путь P_100
    // Проверяет производительность на больших интервальных графах
    //Если с ним будут проблемы, пока забей хуй
    @Test
    public void testLongPathGraph() {
        Map<String, List<String>> graph = new HashMap<>();
        for (int i = 1; i <= 100; i++) {
            List<String> neighbors = new ArrayList<>();
            if (i > 1) neighbors.add(String.valueOf(i - 1));
            if (i < 100) neighbors.add(String.valueOf(i + 1));
            graph.put(String.valueOf(i), neighbors);
        }

        assertTrue(checker.isIntervalGraph(graph), "Длинный путь P_100 должен быть интервальным");
    }

    // Тест 4: Полный граф K_4
    // Проверяет обработку плотных интервальных графов
    @Test
    public void testCompleteGraphK4() {
        Map<String, List<String>> graph = new HashMap<>();
        String[] vertices = {"1", "2", "3", "4"};
        for (String v : vertices) {
            List<String> neighbors = new ArrayList<>(Arrays.asList(vertices));
            neighbors.remove(v);
            graph.put(v, neighbors);
        }

        assertTrue(checker.isIntervalGraph(graph), "Полный граф K_4 должен быть интервальным");
    }

    // Тест 5: Плотный интервальный граф (все интервалы пересекаются)
    // Проверяет графы с высокой плотностью рёбер
    @Test
    public void testDenseIntervalGraph() {
        Map<String, List<String>> graph = new HashMap<>();
        String[] vertices = {"1", "2", "3", "4"};
        for (String v : vertices) {
            List<String> neighbors = new ArrayList<>(Arrays.asList(vertices));
            neighbors.remove(v);
            graph.put(v, neighbors);
        }

        assertTrue(checker.isIntervalGraph(graph), "Плотный интервальный граф должен быть интервальным");
    }

    // Тест 6: Цикл C_4
    // Проверяет обнаружение неинтервального графа (индуцированный цикл длиной 4)
    @Test
    public void testCycleC4() {
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("1", Arrays.asList("2", "4"));
        graph.put("2", Arrays.asList("1", "3"));
        graph.put("3", Arrays.asList("2", "4"));
        graph.put("4", Arrays.asList("3", "1"));

        assertFalse(checker.isIntervalGraph(graph), "Цикл C_4 не является интервальным");
    }

    // Тест 7: Цикл C_5
    // Проверяет обнаружение более длинных индуцированных циклов
    @Test
    public void testCycleC5() {
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("1", Arrays.asList("2", "5"));
        graph.put("2", Arrays.asList("1", "3"));
        graph.put("3", Arrays.asList("2", "4"));
        graph.put("4", Arrays.asList("3", "5"));
        graph.put("5", Arrays.asList("4", "1"));

        assertFalse(checker.isIntervalGraph(graph), "Цикл C_5 не является интервальным");
    }

    // Тест 8: Граф с зонтиком
    // Проверяет обнаружение запрещённого подграфа для интервальных графов
    @Test
    public void testUmbrellaGraph() {
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("1", Arrays.asList("2", "3", "4"));
        graph.put("2", Arrays.asList("1", "5"));
        graph.put("3", Arrays.asList("1", "5"));
        graph.put("4", Arrays.asList("1"));
        graph.put("5", Arrays.asList("2", "3"));

        assertFalse(checker.isIntervalGraph(graph), "Граф с зонтиком не является интервальным");
    }

    // Тест 9: Граф с солнцем (S_3)
    // Проверяет обнаружение другого запрещённого подграфа
    @Test
    public void testSunGraph() {
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("1", Arrays.asList("2", "3", "4"));
        graph.put("2", Arrays.asList("1", "3", "5"));
        graph.put("3", Arrays.asList("1", "2", "6"));
        graph.put("4", Arrays.asList("1"));
        graph.put("5", Arrays.asList("2"));
        graph.put("6", Arrays.asList("3"));

        assertFalse(checker.isIntervalGraph(graph), "Граф с солнцем S_3 не является интервальным");
    }

    // Тест 10: Пустой граф
    // Проверяет краевой случай: граф без вершин
    @Test
    public void testEmptyGraph() {
        Map<String, List<String>> graph = new HashMap<>();

        assertTrue(checker.isIntervalGraph(graph), "Пустой граф должен быть интервальным");
    }

    // Тест 11: Граф с одной вершиной
    // Проверяет минимальный непустой интервальный граф
    @Test
    public void testSingleVertexGraph() {
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("1", Collections.emptyList());

        assertTrue(checker.isIntervalGraph(graph), "Граф с одной вершиной должен быть интервальным");
    }

    // Тест 12: Две вершины с ребром
    // Проверяет минимальный связный интервальный граф
    @Test
    public void testTwoVerticesWithEdge() {
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("1", Arrays.asList("2"));
        graph.put("2", Arrays.asList("1"));

        assertTrue(checker.isIntervalGraph(graph), "Граф с двумя вершинами и ребром должен быть интервальным");
    }

    // Тест 13: Две несвязанные вершины
    // Проверяет несвязный интервальный граф
    @Test
    public void testTwoIsolatedVertices() {
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("1", Collections.emptyList());
        graph.put("2", Collections.emptyList());

        assertTrue(checker.isIntervalGraph(graph), "Граф с двумя несвязанными вершинами должен быть интервальным");
    }

    // Тест 14: Несвязный интервальный граф (два пути P_3)
    // Проверяет обработку нескольких компонент связности
    @Test
    public void testDisconnectedIntervalGraph() {
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("1", Arrays.asList("2"));
        graph.put("2", Arrays.asList("1", "3"));
        graph.put("3", Arrays.asList("2"));
        graph.put("4", Arrays.asList("5"));
        graph.put("5", Arrays.asList("4", "6"));
        graph.put("6", Arrays.asList("5"));

        assertTrue(checker.isIntervalGraph(graph), "Несвязный граф из двух P_3 должен быть интервальным");
    }

    // Тест 15: Граф с изолированной вершиной
    // Проверяет сочетание связных и изолированных вершин
    @Test
    public void testGraphWithIsolatedVertex() {
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("1", Arrays.asList("2"));
        graph.put("2", Arrays.asList("1"));
        graph.put("3", Collections.emptyList());

        assertTrue(checker.isIntervalGraph(graph), "Граф с изолированной вершиной должен быть интервальным");
    }

    // Тест 16: Большой полный граф K_100
    // Проверяет производительность на больших плотных интервальных графах
    @Test
    public void testLargeCompleteGraph() {
        Map<String, List<String>> graph = new HashMap<>();
        for (int i = 1; i <= 100; i++) {
            List<String> neighbors = new ArrayList<>();
            for (int j = 1; j <= 100; j++) {
                if (j != i) neighbors.add(String.valueOf(j));
            }
            graph.put(String.valueOf(i), neighbors);
        }

        assertTrue(checker.isIntervalGraph(graph), "Большой полный граф K_100 должен быть интервальным");
    }

    // Тест 17: Некорректный вход (вершина ссылается на несуществующую)
    // Проверяет обработку ошибок входных данных
    @Test
    public void testInvalidInput() {
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("1", Arrays.asList("2")); // 2 не определена

        assertThrows(IllegalArgumentException.class, () -> checker.isIntervalGraph(graph),
                "Ожидается исключение для некорректного входа");
    }

    // Тест 18: Большой случайный интервальный граф (1000 вершин)
    // Проверяет производительность на больших интервальных графах, построенных из интервалов
    @Test
    public void testLargeRandomIntervalGraph() {
        Map<String, List<String>> graph = new HashMap<>();
        Random rand = new Random(42); // Фиксированный seed для воспроизводимости
        int n = 1000;
        Map<String, int[]> intervals = new HashMap<>();

        for (int i = 1; i <= n; i++) {
            String v = String.valueOf(i);
            int start = rand.nextInt(1000);
            int end = start + rand.nextInt(100) + 1;
            intervals.put(v, new int[]{start, end});
            graph.put(v, new ArrayList<>());
        }

        for (String u : intervals.keySet()) {
            for (String v : intervals.keySet()) {
                if (u.compareTo(v) >= 0) continue;
                int[] uInterval = intervals.get(u);
                int[] vInterval = intervals.get(v);
                if (uInterval[0] <= vInterval[1] && vInterval[0] <= uInterval[1]) {
                    graph.get(u).add(v);
                    graph.get(v).add(u);
                }
            }
        }

        assertTrue(checker.isIntervalGraph(graph), "Случайный интервальный граф должен быть интервальным");
    }
}