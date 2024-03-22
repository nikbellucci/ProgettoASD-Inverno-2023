/**
 * Nome: Niccolò
 * Cognome: Bellucci
 * Matricola: 998755
 * Email: niccolo.bellucci2@studio.unibo.it
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * L'intero programma seguirá la complessitá dominante che si presenta cioè quella della ricerca mediante l'algoritmo di Dijkstra O(n^2 * log(n)).
 */
public class Esercizio3 {
    public static String FILENAME;
    public static double[][] weights;
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Esercizio3 <input_file>");
            System.exit(1);
        }
        FILENAME = args[0];

        // Carica i nodi e i link dalla rete Abilene
        ArrayList<NodeAtlanta> nodes = extractNodes(FILENAME);
        ArrayList<Link> links = extractLinks(FILENAME);

//        System.out.println("#####################################################");
//        for (NodeAtlanta node: nodes) {
//            System.out.println(node.getUniqueId() + ":<" + node.getNameId() + ", (" + node.getLongitude() + ", " + node.getLatitude() + ")" + ">");
//        }
//        for (Link link: links) {
//            System.out.println("<" + link.getId() + ", (" + link.getSource() + ", " + link.getDestination() + ", " +link.getPreInstalledCapacity() + ")" + ">");
//        }
        calculateLinkWeights(links, findMaxPreInstalledCapacity(links));

        indexNodesInLinks(nodes, links);
        weights = createWeightsMatrix(nodes, links);

//        for (Link link: links) {
//            System.out.println("<" + link.getId() + ", (Source: " + link.getSource() + ", Destination: " + link.getDestination() + ", " +link.getWeight() + ")" + ">");
//        }
//        System.out.println("#####################################################");


        long startTime = System.currentTimeMillis();
        List<List<Integer>> paths = findShortestPaths(weights, nodes.get(3).getUniqueId(), nodes.get(11).getUniqueId());
        long endTime = System.currentTimeMillis();

        System.out.println("Start time: " + startTime);
        System.out.println("End time: " + endTime);

        System.out.println("Total time (seconds): " + (endTime - startTime) / 1000.0);

        System.out.println("All paths with the same minimum cost:");
        for (List<Integer> path : paths) {
            System.out.print("Path: " + path + ", Cost: " + calculateCost(weights, path));
            System.out.println();
        }


    }

    public static ArrayList<NodeAtlanta> extractNodes(String fileName) {
        ArrayList<NodeAtlanta> nodes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isInNodeSection = false;
            int iteration = 0;

            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("NODES (")) {
                    isInNodeSection = true;
                    continue; // Salta la riga di intestazione
                }

                if (isInNodeSection && line.trim().equals(")")) {
                    isInNodeSection = false;
                    break; // Uscire dalla sezione dei nodi
                }

                if (isInNodeSection) {
                    // Estrai il nome del nodo dalla riga (prima parte prima della parentesi aperta)
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length > 0) {
                        nodes.add(new NodeAtlanta(iteration, parts[0],parts[2], parts[3]));
                        iteration++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nodes;
    }
    public static ArrayList<Link> extractLinks(String fileName) {
        ArrayList<Link> links = new ArrayList<>();
        Pattern linkPattern = Pattern.compile("(\\w+) \\( (\\w+) (\\w+) \\) (\\d+\\.\\d+) (\\d+\\.\\d+) (\\d+\\.\\d+) (\\d+\\.\\d+) \\( (.+?) \\)");

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isInLinkSection = false;

            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("LINKS (")) {
                    isInLinkSection = true;
                    continue; // Salta la riga di intestazione
                }

                if (isInLinkSection && line.trim().equals(")")) {
                    isInLinkSection = false;
                    break; // Uscire dalla sezione dei links
                }

                if (isInLinkSection) {
                    Matcher matcher = linkPattern.matcher(line.trim());
                    if (matcher.find()) {
                        String linkId = matcher.group(1);
                        String source = matcher.group(2);
                        String target = matcher.group(3);
                        double preInstalledCapacity = Double.parseDouble(matcher.group(4));

                        Link link = new Link(linkId, source, target, preInstalledCapacity);
                        links.add(link);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return links;
    }
    public static double findMaxPreInstalledCapacity(List<Link> links) {
        double maxCapacity = Double.MIN_VALUE;

        for (Link link : links) {
            if (link.getPreInstalledCapacity() > maxCapacity) {
                maxCapacity = link.getPreInstalledCapacity();
            }
        }

        return maxCapacity;
    }
    public static void calculateLinkWeights(List<Link> links, double maxPreInstalledCapacity) {
        for (Link link : links) {
            double weight = maxPreInstalledCapacity / link.getPreInstalledCapacity();
            link.setWeight(weight);
        }
    }
    public static void indexNodesInLinks(List<NodeAtlanta> nodes, List<Link> links) {
        Map<String, Integer> nodeIndexMap = new HashMap<>();

        // Crea una mappa che associa il nome del nodo al suo indice nell'ArrayList
        for (int i = 0; i < nodes.size(); i++) {
            NodeAtlanta node = nodes.get(i);
            nodeIndexMap.put(node.getNameId(), node.getUniqueId());
        }

        // Modifica i nodi di partenza e destinazione dei link con indici unici basati sulla posizione nell'ArrayList di nodi
        for (Link link : links) {
            String sourceNodeId = link.getSource();
            String targetNodeId = link.getDestination();

            if (nodeIndexMap.containsKey(sourceNodeId) && nodeIndexMap.containsKey(targetNodeId)) {
                int sourceIndex = nodeIndexMap.get(sourceNodeId);
                int targetIndex = nodeIndexMap.get(targetNodeId);

                link.setSource(Integer.toString(sourceIndex));
                link.setDestination(Integer.toString(targetIndex));
            }
        }
    }
    public static double[][] createWeightsMatrix(ArrayList<NodeAtlanta> nodes, List<Link> links) {
        double[][] weights = new double[nodes.size()][nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            Arrays.fill(weights[i], Double.POSITIVE_INFINITY);
        }

        for (int i = 0; i < nodes.size(); i++) {
            weights[i][i] = 0.0;
        }

        for (Link link: links) {
            weights[Integer.parseInt(link.getSource())][Integer.parseInt(link.getDestination())] = link.getWeight();
            weights[Integer.parseInt(link.getDestination())][Integer.parseInt(link.getSource())] = link.getWeight();
        }

        return weights;
    }

    /**
     * L' algoritmo di Dijkstra impiega O(n^2 * log(n)), dove n è il numero totale di nodi.
     * @param graph
     * @param source
     * @param destination
     * @return
     */
    public static List<List<Integer>> findShortestPaths(double[][] graph, int source, int destination) {
        int n = graph.length;
        List<List<Integer>> allPaths = new ArrayList<>();
        ArrayList<NodeAtlanta> minList = new ArrayList<>();
        boolean[] visited = new boolean[n];
        double[] distance = new double[n];
        Arrays.fill(distance, Double.POSITIVE_INFINITY);

        distance[source] = 0;
        minList.add(new NodeAtlanta(source, 0.0));

        while (!minList.isEmpty()) {
            quickSort(minList, 0, minList.size() - 1);

            NodeAtlanta currentNode = minList.get(0);
            minList.remove(0);
            int currentVertex = currentNode.getUniqueId();

            if (visited[currentVertex]) {
                continue;
            }

            visited[currentVertex] = true;

            for (int neighbor = 0; neighbor < n; neighbor++) {
                if (graph[currentVertex][neighbor] > 0.0) {
                    double newDistance = distance[currentVertex] + graph[currentVertex][neighbor];

                    if (newDistance < distance[neighbor]) {
                        distance[neighbor] = newDistance;
                        minList.add(new NodeAtlanta(neighbor, newDistance));
                    }
                }
            }
        }

        findPaths(graph, source, destination, new ArrayList<>(), allPaths, distance);

        return allPaths;
    }
    private static void findPaths(double[][] graph, int current, int destination, List<Integer> currentPath, List<List<Integer>> allPaths, double[] distance) {
        currentPath.add(current);

        if (current == destination) {
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            for (int neighbor = 0; neighbor < graph.length; neighbor++) {
                if (graph[current][neighbor] > 0.0) {
                    double nextDistance = distance[current] + graph[current][neighbor];
                    if (nextDistance == distance[neighbor]) {
                        findPaths(graph, neighbor, destination, currentPath, allPaths, distance);
                    }
                }
            }
        }

        currentPath.remove(currentPath.size() - 1);
    }
    private static double calculateCost(double[][] graph, List<Integer> path) {
        double cost = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            int fromNode = path.get(i);
            int toNode = path.get(i + 1);
            cost += graph[fromNode][toNode];
        }
        return cost;
    }

    /**
     * L' algoritmo quicksort impiega O(n^2 * log(n)), dove n è la lunghezza della lista da ordinare.
     * @param nodeList
     * @param begin
     * @param end
     */
    public static void quickSort(List<NodeAtlanta> nodeList, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(nodeList, begin, end);

            quickSort(nodeList, begin, partitionIndex - 1);
            quickSort(nodeList, partitionIndex + 1, end);
        }
    }
    private static int partition(List<NodeAtlanta> nodeList, int begin, int end) {
        double pivot = nodeList.get(end).getDistance();
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
            if (nodeList.get(j).getDistance() <= pivot) {
                i++;

                swap(nodeList, i, j);
            }
        }

        swap(nodeList, i + 1, end);

        return i + 1;
    }
    private static void swap(List<NodeAtlanta> nodeList, int i, int j) {
        NodeAtlanta temp = nodeList.get(i);
        nodeList.set(i, nodeList.get(j));
        nodeList.set(j, temp);
    }
}

class NodeAtlanta {
    private int uniqueId;
    private String nameId;
    private String longitude;
    private String latitude;
    private double distance;

    public NodeAtlanta(int uniqueId, String nameId, String longitude, String latitude) {
        this.uniqueId = uniqueId;
        this.nameId = nameId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public NodeAtlanta(int uniqueId, String nameId, double distance) {
        this.uniqueId = uniqueId;
        this.nameId = nameId;
        this.setDistance(distance);
    }

    public NodeAtlanta(int uniqueId, double distance) {
        this.uniqueId = uniqueId;
        this.distance = distance;
    }

    public String getNameId() {
        return nameId;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}

class Link {
    private final String id;
    private String source;
    private String destination;
    private final double preInstalledCapacity;
    private double weight;

    public Link(String id, String source, String destination, double preInstalledCapacity) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.preInstalledCapacity = preInstalledCapacity;
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public double getPreInstalledCapacity() {
        return preInstalledCapacity;
    }

    public double getWeight() {
        return weight;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}