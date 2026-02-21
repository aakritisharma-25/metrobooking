package com.moveinsync.metrobooking.service;

import com.moveinsync.metrobooking.graph.GraphEdge;
import com.moveinsync.metrobooking.graph.GraphNode;
import com.moveinsync.metrobooking.graph.MetroGraph;
import com.moveinsync.metrobooking.model.Route;
import com.moveinsync.metrobooking.model.Stop;
import com.moveinsync.metrobooking.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GraphService {

    private final RouteRepository routeRepository;

    // Cache the graph so we don't rebuild it every time
    @Cacheable("metroGraph")
    public MetroGraph buildGraph() {
        log.info("Building metro graph from database...");
        MetroGraph graph = new MetroGraph();

        List<Route> routes = routeRepository.findAll();

        // Add all stops as nodes
        for (Route route : routes) {
            List<Stop> stops = route.getStops();
            for (Stop stop : stops) {
                if (!graph.containsStop(stop.getId())) {
                    GraphNode node = new GraphNode(
                            stop.getId(),
                            stop.getName(),
                            stop.getCode(),
                            stop.getIsInterchange()
                    );
                    graph.addNode(node);
                }
            }

            // Add edges between consecutive stops
            for (int i = 0; i < stops.size() - 1; i++) {
                Stop from = stops.get(i);
                Stop to = stops.get(i + 1);

                GraphEdge edge = new GraphEdge(
                        from.getId(),
                        to.getId(),
                        route.getId(),
                        route.getName(),
                        route.getColor(),
                        2.0 // default 2 mins per stop
                );
                graph.addEdge(edge);
            }
        }

        log.info("Metro graph built with {} stops", graph.getTotalStops());
        return graph;
    }

    // Call this when routes/stops are updated
    @CacheEvict(value = "metroGraph", allEntries = true)
    public void refreshGraph() {
        log.info("Metro graph cache cleared - will rebuild on next request");
    }
}