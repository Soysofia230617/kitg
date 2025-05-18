import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.util.FileLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class IntervalGraphCheckTest {
    private IsIntervalGraph check;

    @BeforeEach
    void setUp() {
        check = new IsIntervalGraph();
    }
    private Graph loadGraph(String fileName) throws Exception {
        return FileLoader.loadGraphFromJson("src/test/resources/" + fileName);
    }

    // Вспомогательный метод для создания idToIndex
    private Map<Integer, Integer> createIdToIndex(Graph graph) {
        Map<Integer, Integer> idToIndex = new HashMap<>();
        List<Vertex> vertices = graph.getVertexList();
        for (int i = 0; i < vertices.size(); i++) {
            idToIndex.put(vertices.get(i).getId(), i);
        }
        return idToIndex;
    }

    // === Тесты для getNeighbours ===
    @Test
    void getNeighbours_Test1_Vertex2_ReturnsCorrectNeighbours() throws Exception {
        Graph graph = loadGraph("Test_1.json");
        Set<Integer> neighbours = check.getNeighbours(2, graph);
        assertThat(neighbours).containsExactlyInAnyOrder(1, 3);
    }

    @Test
    void getNeighbours_Test9_Vertex2_ReturnsCorrectNeighbours() throws Exception {
        Graph graph = loadGraph("Test_9.json");
        Set<Integer> neighbours = check.getNeighbours(2, graph);
        assertThat(neighbours).containsExactlyInAnyOrder(1, 3, 4);
    }

    @Test
    void getNeighbours_Test6_Vertex7_ReturnsCorrectNeighbours() throws Exception {
        Graph graph = loadGraph("Test_6.json");
        Set<Integer> neighbours = check.getNeighbours(7, graph);
        assertThat(neighbours).containsExactlyInAnyOrder(2, 4, 5, 6);
    }

    // === Тесты для performLexBFS ===
    @Test
    void performLexBFS_Test8_ReturnsValidOrder() throws Exception {
        Graph graph = loadGraph("Test_8.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        assertThat(order).hasSize(3).containsExactlyInAnyOrder(1, 2, 3);
        assertThat(new HashSet<>(order)).hasSize(3);
        // Стартовая вершина имеет степень 2
        int startVertex = order.get(0);
        Set<Integer> neighbors = check.getNeighbours(startVertex, graph);
        assertThat(neighbors).hasSize(2);
    }

    @Test
    void performLexBFS_Test9_ReturnsValidOrder() throws Exception {
        Graph graph = loadGraph("Test_9.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        assertThat(order).hasSize(4).containsExactlyInAnyOrder(1, 2, 3, 4);
        // Стартовая вершина — 2 (степень 3)
        int startVertex = order.get(0);
        Set<Integer> neighbors = check.getNeighbours(startVertex, graph);
        assertThat(neighbors.size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    void performLexBFS_Test10_ReturnsValidOrder() throws Exception {
        Graph graph = loadGraph("Test_10.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        assertThat(order).hasSize(6).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6);
        // Стартовая вершина — 5 (степень 4)
        int startVertex = order.get(0);
        Set<Integer> neighbors = check.getNeighbours(startVertex, graph);
        assertThat(neighbors.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void performLexBFS_EmptyGraph_ReturnsEmptyList() throws Exception {
        Graph graph = new Graph();
        graph.setVertexList(new ArrayList<>());
        graph.setEdgeList(new ArrayList<>());
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        assertThat(order).isEmpty();
    }

     //=== Тесты для isPerfectEliminationOrder ===
    @Test
    void isPerfectEliminationOrder_Test1_ReturnsTrue() throws Exception {
        Graph graph = loadGraph("Test_1.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        boolean result = check.isPerfectEliminationOrder(graph, order, idToIndex);
        assertThat(result).isTrue();
    }

    @Test
    void isPerfectEliminationOrder_Test2_ReturnsFalse() throws Exception {
        Graph graph = loadGraph("Test_2.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        boolean result = check.isPerfectEliminationOrder(graph, order, idToIndex);
        assertThat(result).isFalse();
    }

    @Test
    void isPerfectEliminationOrder_Test8_ReturnsTrue() throws Exception {
        Graph graph = loadGraph("Test_8.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        boolean result = check.isPerfectEliminationOrder(graph, order, idToIndex);
        assertThat(result).isTrue();
    }

    @Test
    void isPerfectEliminationOrder_Test9_ReturnsTrue() throws Exception {
        Graph graph = loadGraph("Test_9.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        boolean result = check.isPerfectEliminationOrder(graph, order, idToIndex);
        assertThat(result).isTrue();
    }

    // === Тесты для checkForInducedC4 ===
    @Test
    void checkForInducedC4_Test8_NoC4_ReturnsFalse() throws Exception {
        Graph graph = loadGraph("Test_8.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        boolean result = check.checkForInducedC4(graph, 1, 2, 3, order, 0);
        assertThat(result).isFalse();
    }

    @Test
    void checkForInducedC4_Test2_HasC4_ReturnsTrue() throws Exception {
        Graph graph = loadGraph("Test_2.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        // Проверяем C4: 1 ↔ 2 ↔ 3 ↔ 4
        boolean result = check.checkForInducedC4(graph, 1, 2, 4, order, 0);
        assertThat(result).isTrue();
    }

    @Test
    void checkForInducedC4_Test9_NoC4_ReturnsFalse() throws Exception {
        Graph graph = loadGraph("Test_9.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        // Проверяем для вершины 2 с соседями 1 и 3
        boolean result = check.checkForInducedC4(graph, 2, 1, 3, order, 0);
        assertThat(result).isFalse();
    }

    // === Тесты для computeMaximalCliques ===
    @Test
    void computeMaximalCliques_Test8_ReturnsSingleClique() throws Exception {
        Graph graph = loadGraph("Test_8.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        List<Set<Integer>> cliques = check.computeMaximalCliques(graph, order, idToIndex);
        assertThat(cliques).hasSize(1);
        assertThat(cliques.get(0)).containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    void computeMaximalCliques_Test9_ReturnsCorrectCliques() throws Exception {
        Graph graph = loadGraph("Test_9.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        List<Set<Integer>> cliques = check.computeMaximalCliques(graph, order, idToIndex);
        assertThat(cliques).hasSize(2);
        assertThat(cliques).contains(
                new HashSet<>(Arrays.asList(1, 2, 4)),
                new HashSet<>(Arrays.asList(2, 3, 4))
        );
    }

    @Test
    void computeMaximalCliques_Test10_ReturnsCorrectCliques() throws Exception {
        Graph graph = loadGraph("Test_10.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        List<Set<Integer>> cliques = check.computeMaximalCliques(graph, order, idToIndex);
        assertThat(cliques).hasSize(4);
        assertThat(cliques).contains(
                new HashSet<>(Arrays.asList(1, 2, 5)),
                new HashSet<>(Arrays.asList(2, 3, 5)),
                new HashSet<>(Arrays.asList(3, 4, 5)),
                new HashSet<>(Arrays.asList(4, 6))
        );
    }

    // === Тесты для orderCliquesByTreePath ===
    @Test
    void orderCliquesByTreePath_Test8_ReturnsSingleClique() throws Exception {
        Graph graph = loadGraph("Test_8.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        List<Set<Integer>> cliques = check.computeMaximalCliques(graph, order, idToIndex);
        List<Set<Integer>> orderedCliques = check.orderCliquesByTreePath(graph, cliques);
        assertThat(orderedCliques).hasSize(1);
        assertThat(orderedCliques.get(0)).containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    void orderCliquesByTreePath_Test9_ReturnsOrderedCliques() throws Exception {
        Graph graph = loadGraph("Test_9.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        List<Set<Integer>> cliques = check.computeMaximalCliques(graph, order, idToIndex);
        List<Set<Integer>> orderedCliques = check.orderCliquesByTreePath(graph, cliques);
        assertThat(orderedCliques).hasSize(2);
        assertThat(orderedCliques).contains(
                new HashSet<>(Arrays.asList(1, 2, 4)),
                new HashSet<>(Arrays.asList(2, 3, 4))
        );
    }

    @Test
    void orderCliquesByTreePath_Test10_ReturnsOrderedCliques() throws Exception {
        Graph graph = loadGraph("Test_10.json");
        Map<Integer, Integer> idToIndex = createIdToIndex(graph);
        List<Integer> order = check.performLexBFS(graph, idToIndex);
        List<Set<Integer>> cliques = check.computeMaximalCliques(graph, order, idToIndex);
        List<Set<Integer>> orderedCliques = check.orderCliquesByTreePath(graph, cliques);
        assertThat(orderedCliques).hasSize(4);
        assertThat(orderedCliques).contains(
                new HashSet<>(Arrays.asList(1, 2, 5)),
                new HashSet<>(Arrays.asList(2, 3, 5)),
                new HashSet<>(Arrays.asList(3, 4, 5)),
                new HashSet<>(Arrays.asList(4, 6))
        );
    }

    // === Тесты на интервальность (run) ===
    @Test
    void run_Test1_Returns1() throws Exception {
        Graph graph = loadGraph("Test_1.json");
        int result = check.run(graph);
        assertThat(result).isEqualTo(1);
    }

    @Test
    void run_Test2_Returns0() throws Exception {
        Graph graph = loadGraph("Test_2.json");
        int result = check.run(graph);
        assertThat(result).isEqualTo(0);
    }

    @Test
    void run_Test3_Returns0() throws Exception {
        Graph graph = loadGraph("Test_3.json");
        int result = check.run(graph);
        assertThat(result).isEqualTo(0);
    }

    @Test
    void run_Test4_Returns0() throws Exception {
        Graph graph = loadGraph("Test_4.json");
        int result = check.run(graph);
        assertThat(result).isEqualTo(0);
    }

    @Test
    void run_Test5_Returns0() throws Exception {
        Graph graph = loadGraph("Test_5.json");
        int result = check.run(graph);
        assertThat(result).isEqualTo(0);
    }

    @Test
    void run_Test6_Returns1() throws Exception {
        Graph graph = loadGraph("Test_6.json");
        int result = check.run(graph);
        assertThat(result).isEqualTo(1);
    }

    @Test
    void run_Test7_Returns1() throws Exception {
        Graph graph = loadGraph("Test_7.json");
        int result = check.run(graph);
        assertThat(result).isEqualTo(1);
    }

    @Test
    void run_Test8_Returns1() throws Exception {
        Graph graph = loadGraph("Test_8.json");
        int result = check.run(graph);
        assertThat(result).isEqualTo(1);
    }

    @Test
    void run_Test9_Returns1() throws Exception {
        Graph graph = loadGraph("Test_9.json");
        int result = check.run(graph);
        assertThat(result).isEqualTo(1);
    }

    @Test
    void run_Test10_Returns1() throws Exception {
        Graph graph = loadGraph("Test_10.json");
        int result = check.run(graph);
        assertThat(result).isEqualTo(1);
    }
}
