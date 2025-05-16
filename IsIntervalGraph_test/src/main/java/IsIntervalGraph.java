import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.*;

public class IsIntervalGraph implements GraphCharacteristic {
    @Override
    public int run(Graph graph) {
        List<Vertex> vertices = graph.getVertexList();
        System.out.println("Vertices: " + vertices.stream().map(Vertex::getId).toList());
        int n = vertices.size();
        if (n == 0) return 1; // Пустой граф интервальный

        // Маппинг ID вершин на индексы
        Map<Integer, Integer> idToIndex = new HashMap<>();
        for (int i = 0; i < n; i++) {
            idToIndex.put(vertices.get(i).getId(), i);
        }

        // Шаг 1: Выполнить Lex-BFS
        List<Integer> lexBFSOrder = performLexBFS(graph, idToIndex);
        System.out.println("Lex-BFS order: " + lexBFSOrder);

        // Шаг 2: Проверить, является ли порядок хордальностью
        if (!isPerfectEliminationOrder(graph, lexBFSOrder, idToIndex)) {
            System.out.println("Graph is not chordal");
            return 0;
        }

        // Шаг 3: Вычислить максимальные клики
        List<Set<Integer>> maximalCliques = computeMaximalCliques(graph, lexBFSOrder, idToIndex);
        System.out.println("Maximal cliques before ordering: " + maximalCliques);

        // Шаг 4: Упорядочить клики по пути дерева клик
        maximalCliques = orderCliquesByTreePath(graph, maximalCliques);
        System.out.println("Maximal cliques after ordering: " + maximalCliques);

        // Шаг 5: Проверить, образуют ли клики путь
        for (Vertex v : vertices) {
            int vId = v.getId();
            List<Integer> cliques = new ArrayList<>();
            for (int i = 0; i < maximalCliques.size(); i++) {
                if (maximalCliques.get(i).contains(vId)) {
                    cliques.add(i);
                }
            }
            System.out.println("Vertex " + vId + " cliques: " + cliques);
            if (!cliques.isEmpty()) {
                int min = Collections.min(cliques);
                int max = Collections.max(cliques);
                for (int i = min; i <= max; i++) {
                    if (!cliques.contains(i)) {
                        System.out.println("Non-consecutive cliques for vertex " + vId);
                        return 0; // Клики не последовательны
                    }
                }
            }
        }

        return 1; // Граф интервальный
    }

    private List<Integer> performLexBFS(Graph graph, Map<Integer, Integer> idToIndex) {
        List<Vertex> vertices = graph.getVertexList();
        int n = vertices.size();
        List<Integer> order = new ArrayList<>();
        List<Set<Integer>> partition = new ArrayList<>();
        Set<Integer> allVertices = new HashSet<>();
        for (Vertex v : vertices) allVertices.add(v.getId());

        // Начать с вершины максимальной степени
        int maxDegree = -1;
        int startVertex = -1;
        for (Vertex v : vertices) {
            int degree = getNeighbours(v.getId(), graph).size();
            if (degree > maxDegree) {
                maxDegree = degree;
                startVertex = v.getId();
            }
        }
        System.out.println("Starting vertex: " + startVertex + " with degree: " + maxDegree);
        Set<Integer> initial = new HashSet<>();
        initial.add(startVertex);
        partition.add(initial);
        allVertices.remove(startVertex);
        if (!allVertices.isEmpty()) partition.add(allVertices);

        while (!partition.isEmpty()) {
            Set<Integer> lastClass = partition.get(partition.size() - 1);
            if (lastClass.isEmpty()) {
                partition.remove(partition.size() - 1);
                continue;
            }
            int v = lastClass.iterator().next();
            lastClass.remove(v);
            order.add(v);

            List<Set<Integer>> newPartition = new ArrayList<>();
            for (Set<Integer> classA : partition) {
                Set<Integer> neighbors = new HashSet<>();
                Set<Integer> nonNeighbors = new HashSet<>();
                for (int u : classA) {
                    if (getNeighbours(v, graph).contains(u)) {
                        neighbors.add(u);
                    } else {
                        nonNeighbors.add(u);
                    }
                }
                if (!neighbors.isEmpty()) newPartition.add(neighbors);
                if (!nonNeighbors.isEmpty()) newPartition.add(nonNeighbors);
            }
            partition = newPartition;
        }
        return order;
    }

    private boolean isPerfectEliminationOrder(Graph graph, List<Integer> order, Map<Integer, Integer> idToIndex) {
        int n = graph.getVertexList().size();
        for (int i = 0; i < n; i++) {
            int v = order.get(i);
            List<Integer> rightNeighbors = new ArrayList<>();
            for (int j = i + 1; j < n; j++) {
                int u = order.get(j);
                if (getNeighbours(v, graph).contains(u)) {
                    rightNeighbors.add(u);
                }
            }
            System.out.println("Vertex " + v + " right neighbors: " + rightNeighbors);
            if (rightNeighbors.size() <= 1) continue; // Одна или ноль вершин — тривиально клика
            // Проверяем, что первый правый сосед связан с остальными правыми соседями
            int firstNeighbor = rightNeighbors.get(0);
            for (int j = 1; j < rightNeighbors.size(); j++) {
                int u = rightNeighbors.get(j);
                // Проверяем, есть ли ребро между firstNeighbor и u
                if (!getNeighbours(firstNeighbor, graph).contains(u)) {
                    // Дополнительная проверка: если граф — путь, это может быть допустимо
                    // Проверяем, не создаёт ли это индуцированный цикл C4
                    boolean formsC4 = checkForInducedC4(graph, v, firstNeighbor, u, order, i);
                    if (formsC4) {
                        System.out.println("Non-clique: " + firstNeighbor + " and " + u + " not connected, forms C4");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkForInducedC4(Graph graph, int v, int u1, int u2, List<Integer> order, int vIndex) {
        // Проверяем, создают ли вершины v, u1, u2 и некоторая другая вершина индуцированный цикл C4
        // Для этого ищем вершину w, которая связана с u1 и u2, но не с v
        for (Vertex wVertex : graph.getVertexList()) {
            int w = wVertex.getId();
            if (w == v || w == u1 || w == u2) continue;
            Set<Integer> wNeighbors = getNeighbours(w, graph);
            // Условие для C4: w связан с u1 и u2, но не с v, и u1 не связан с u2
            if (wNeighbors.contains(u1) && wNeighbors.contains(u2) && !wNeighbors.contains(v)) {
                // Дополнительно проверяем, что u1 и u2 не связаны (уже известно)
                return true; // Найден индуцированный C4: v -> u1 -> w -> u2 -> v
            }
        }
        return false; // Нет индуцированного C4
    }

    private List<Set<Integer>> computeMaximalCliques(Graph graph, List<Integer> order, Map<Integer, Integer> idToIndex) {
        List<Set<Integer>> cliques = new ArrayList<>();
        int n = graph.getVertexList().size();

        // Генерируем кандидатов на клики
        for (int i = 0; i < n; i++) {
            int v = order.get(i);
            Set<Integer> clique = new HashSet<>();
            clique.add(v);
            for (int j = i + 1; j < n; j++) {
                int u = order.get(j);
                if (getNeighbours(v, graph).contains(u)) {
                    boolean valid = true;
                    for (int w : clique) {
                        if (w != v && !getNeighbours(w, graph).contains(u)) {
                            valid = false;
                            break;
                        }
                    }
                    if (valid) {
                        clique.add(u);
                    }
                }
            }
            if (clique.size() > 1 || getNeighbours(v, graph).isEmpty()) {
                cliques.add(clique);
            }
        }

        // Фильтруем немаксимальные клики
        List<Set<Integer>> maximalCliques = new ArrayList<>();
        for (Set<Integer> clique : cliques) {
            boolean isMaximal = true;
            for (Set<Integer> other : cliques) {
                if (other != clique && other.containsAll(clique) && other.size() > clique.size()) {
                    isMaximal = false;
                    break;
                }
            }
            if (isMaximal) {
                maximalCliques.add(clique);
            }
        }

        System.out.println("Candidate cliques: " + cliques);
        System.out.println("Maximal cliques after filtering: " + maximalCliques);
        return maximalCliques;
    }

    private List<Set<Integer>> orderCliquesByTreePath(Graph graph, List<Set<Integer>> cliques) {
        // Создаём граф смежности клик
        List<List<Integer>> cliqueGraph = new ArrayList<>();
        for (int i = 0; i < cliques.size(); i++) {
            cliqueGraph.add(new ArrayList<>());
        }

        // Проверяем пересечения клик
        for (int i = 0; i < cliques.size(); i++) {
            for (int j = i + 1; j < cliques.size(); j++) {
                Set<Integer> intersection = new HashSet<>(cliques.get(i));
                intersection.retainAll(cliques.get(j));
                if (!intersection.isEmpty()) {
                    cliqueGraph.get(i).add(j);
                    cliqueGraph.get(j).add(i);
                }
            }
        }

        // Выполняем BFS для упорядочивания клик
        List<Set<Integer>> orderedCliques = new ArrayList<>();
        boolean[] visited = new boolean[cliques.size()];
        Queue<Integer> queue = new LinkedList<>();
        int startClique = 0;
        for (int i = 0; i < cliques.size(); i++) {
            if (cliques.get(i).size() > 1) {
                startClique = i;
                break;
            }
        }

        queue.add(startClique);
        visited[startClique] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            orderedCliques.add(cliques.get(current));
            for (int neighbor : cliqueGraph.get(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }

        return orderedCliques;
    }

    private Set<Integer> getNeighbours(int vertexId, Graph graph) {
        Set<Integer> neighbours = new HashSet<>();
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getSource() == vertexId) {
                neighbours.add(edge.getTarget());
                System.out.println("Edge: " + edge.getSource() + " -> " + edge.getTarget());
            } else if (edge.getTarget() == vertexId && !graph.isDirect()) {
                neighbours.add(edge.getSource());
                System.out.println("Edge (undirected): " + edge.getTarget() + " -> " + edge.getSource());
            }
        }
        System.out.println("Neighbours of " + vertexId + ": " + neighbours);
        return neighbours;
    }
}