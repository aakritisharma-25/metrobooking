package com.moveinsync.metrobooking.service;

import com.moveinsync.metrobooking.dto.PathResult;
import com.moveinsync.metrobooking.dto.PathSegment;
import com.moveinsync.metrobooking.graph.GraphEdge;
import com.moveinsync.metrobooking.graph.MetroGraph;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PathFinderService {

    // Transfer penalty in minutes (discourages unnecessary transfers)
    private static final double TRANSFER_PENALTY = 5.0;

    public PathResult findOptimalPath(MetroGraph graph, Long sourceId, Long destinationId) {

        // Edge case: source and destination are same
        if (sourceId.equals(destinationId)) {
            return PathResult.builder()
                    .pathFound(false)
                    .segments(new ArrayList<>())
                    .totalStops(0)
                    .totalInterchanges(0)
                    .totalTravelTime(0)
                    .build();
        }

        // Edge case: stop doesn't exist in graph
        if (!graph.containsStop(sourceId) || !graph.containsStop(destinationId)) {
            return PathResult.builder()
                    .pathFound(false)
                    .segments(new ArrayList<>())
                    .build();
        }

        // Dijkstra's Algorithm
        // cost map: stopId -> minimum cost to reach this stop
        Map<Long, Double> cost = new HashMap<>();
        // previous map: stopId -> edge used to reach this stop
        Map<Long, GraphEdge> previous = new HashMap<>();
        // visited set
        Set<Long> visited = new HashSet<>();

        // Priority queue: [cost, stopId]
        PriorityQueue<long[]> pq = new PriorityQueue<>(
                Comparator.comparingDouble(a -> a[1])
        );

        // Initialize all costs to infinity
        for (Long stopId : graph.getNodes().keySet()) {
            cost.put(stopId, Double.MAX_VALUE);
        }

        // Start from source with cost 0
        cost.put(sourceId, 0.0);
        pq.offer(new long[]{sourceId, 0});

        while (!pq.isEmpty()) {
            long[] current = pq.poll();
            Long currentStopId = current[0];

            if (visited.contains(currentStopId)) continue;
            visited.add(currentStopId);

            // Reached destination!
            if (currentStopId.equals(destinationId)) break;

            // Check all neighbors
            for (GraphEdge edge : graph.getNeighbors(currentStopId)) {
                Long neighborId = edge.getToStopId();
                if (visited.contains(neighborId)) continue;

                // Calculate transfer penalty if changing lines
                double penalty = 0.0;
                if (previous.containsKey(currentStopId)) {
                    GraphEdge prevEdge = previous.get(currentStopId);
                    if (!prevEdge.getRouteId().equals(edge.getRouteId())) {
                        penalty = TRANSFER_PENALTY;
                    }
                }

                double newCost = cost.get(currentStopId) + edge.getTravelTime() + penalty;

                if (newCost < cost.get(neighborId)) {
                    cost.put(neighborId, newCost);
                    previous.put(neighborId, edge);
                    pq.offer(new long[]{neighborId, (long) newCost});
                }
            }
        }

        // No path found
        if (cost.get(destinationId) == Double.MAX_VALUE) {
            return PathResult.builder()
                    .pathFound(false)
                    .segments(new ArrayList<>())
                    .build();
        }

        // Reconstruct path
        return reconstructPath(graph, previous, sourceId, destinationId, cost.get(destinationId));
    }

    private PathResult reconstructPath(MetroGraph graph, Map<Long, GraphEdge> previous,
                                       Long sourceId, Long destinationId, double totalCost) {
        List<PathSegment> segments = new ArrayList<>();
        int interchanges = 0;
        String previousRouteId = null;

        // Walk backwards from destination to source
        LinkedList<GraphEdge> path = new LinkedList<>();
        Long current = destinationId;

        while (previous.containsKey(current)) {
            path.addFirst(previous.get(current));
            current = previous.get(current).getFromStopId();
        }

        // Add source stop
        segments.add(PathSegment.builder()
                .stopName(graph.getNodes().get(sourceId).getStopName())
                .stopCode(graph.getNodes().get(sourceId).getStopCode())
                .routeName(path.isEmpty() ? "" : path.getFirst().getRouteName())
                .routeColor(path.isEmpty() ? "" : path.getFirst().getRouteColor())
                .isInterchange(false)
                .build());

        // Add intermediate and destination stops
        for (GraphEdge edge : path) {
            Long stopId = edge.getToStopId();
            boolean isInterchange = false;

            if (previousRouteId != null && !previousRouteId.equals(String.valueOf(edge.getRouteId()))) {
                isInterchange = true;
                interchanges++;
            }

            segments.add(PathSegment.builder()
                    .stopName(graph.getNodes().get(stopId).getStopName())
                    .stopCode(graph.getNodes().get(stopId).getStopCode())
                    .routeName(edge.getRouteName())
                    .routeColor(edge.getRouteColor())
                    .isInterchange(isInterchange)
                    .build());

            previousRouteId = String.valueOf(edge.getRouteId());
        }

        return PathResult.builder()
                .pathFound(true)
                .segments(segments)
                .totalStops(segments.size())
                .totalInterchanges(interchanges)
                .totalTravelTime(totalCost)
                .build();
    }
}