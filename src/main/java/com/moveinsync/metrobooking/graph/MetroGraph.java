package com.moveinsync.metrobooking.graph;

import lombok.*;
import java.util.*;

@Data
public class MetroGraph {

    private Map<Long, List<GraphEdge>> adjacencyList = new HashMap<>();
    private Map<Long, GraphNode> nodes = new HashMap<>();

    public void addNode(GraphNode node) {
        nodes.put(node.getStopId(), node);
        adjacencyList.putIfAbsent(node.getStopId(), new ArrayList<>());
    }

    public void addEdge(GraphEdge edge) {
        adjacencyList.get(edge.getFromStopId()).add(edge);

        GraphEdge reverse = new GraphEdge(
                edge.getToStopId(),
                edge.getFromStopId(),
                edge.getRouteId(),
                edge.getRouteName(),
                edge.getRouteColor(),
                edge.getTravelTime()
        );
        adjacencyList.get(edge.getToStopId()).add(reverse);
    }

    public List<GraphEdge> getNeighbors(Long stopId) {
        return adjacencyList.getOrDefault(stopId, Collections.emptyList());
    }

    public boolean containsStop(Long stopId) {
        return nodes.containsKey(stopId);
    }

    public int getTotalStops() {
        return nodes.size();
    }
}