package service;

import static constant.CommonConstant.DOUBLE_ZERO;
import static constant.CommonConstant.INT_ZERO;
import static constant.CommonConstant.TAG_KEY_HIGHWAY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import model.NodeEntity;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.springframework.util.CollectionUtils;
import utils.CommonUtils;

public class Handler implements Sink {

    private ExecutorService executorService;
    private final List<Future<Double>> futures = new ArrayList<>();
    private final Map<Long, NodeEntity> nodes = new HashMap<>();
    private final HashSet<Long> ways = new HashSet<>();
    private final AtomicBoolean flag = new AtomicBoolean(true);

    private double totalLength = DOUBLE_ZERO;
    private long nodeUpdateCount = INT_ZERO;
    private double roadLength = DOUBLE_ZERO;

    public void initialize(Map<String, Object> metaData) {
        executorService = Executors.newFixedThreadPool(10);
    }

    public void complete() {
    }

    @Override
    public void process(EntityContainer entityContainer) {
        if (!flag.get() && entityContainer instanceof NodeContainer
            && nodeUpdateCount < nodes.size()) {
            //update node lat/lon
            Node node = ((NodeContainer) entityContainer).getEntity();
            if (nodes.containsKey(node.getId())) {
                NodeEntity nodeEntity = nodes.get(node.getId());
                nodeEntity.setLat(node.getLatitude());
                nodeEntity.setLon(node.getLongitude());
                nodeUpdateCount++;
            }
        } else if (entityContainer instanceof WayContainer) {
            Way way = ((WayContainer) entityContainer).getEntity();
            if (flag.get()) {
                if (isRoad(way)) {
                    way.getWayNodes().forEach(wayNode -> {
                        if (!nodes.containsKey(wayNode.getNodeId())) {
                            NodeEntity nodeEntity = new NodeEntity(wayNode.getNodeId());
                            nodes.put(nodeEntity.getId(), nodeEntity);
                        }
                    });
                    ways.add(way.getId());
                }
            } else if (ways.contains(way.getId())) {
                List<WayNode> wayNodes = way.getWayNodes();
                futures.add(executorService.submit(() -> computeLength(wayNodes)));
            }
        }
    }

    private double computeLength(List<WayNode> wayNodes) {
        double result = 0.0;
        for (int i = 0; i < wayNodes.size() - 1; i++) {
            NodeEntity node1 = nodes.get(wayNodes.get(i).getNodeId());
            NodeEntity node2 = nodes.get(wayNodes.get(i + 1).getNodeId());
            result += CommonUtils.getDistance(node1.getLat(), node1.getLon(),
                node2.getLat(), node2.getLon());
        }
        return result;
    }

    @Override
    public void close() {
        if (!flag.get() && !CollectionUtils.isEmpty(futures)) {
            futures.forEach(future -> {
                try {
                    totalLength += future.get();
                    roadLength += future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }
        flag.set(!flag.get());
    }


    public int getRoadSize() {
        return ways.size();
    }

    public double getRoadLength() {
        return roadLength;
    }

    public double getTotalLength() {
        return totalLength;
    }

    public void flush() {
        nodes.clear();
        ways.clear();
        flag.set(true);
        roadLength = DOUBLE_ZERO;
        nodeUpdateCount = INT_ZERO;
        executorService.shutdown();
        futures.clear();
    }

    public static boolean isRoad(Way way) {
        //be tagged with a highway=* and nocycle
        boolean hasHighway = false;
        boolean noCycle = false;
        List<WayNode> wayNodes = way.getWayNodes();
        if (!CollectionUtils.isEmpty(wayNodes) && wayNodes.size() > 1) {
            if (wayNodes.get(0).getNodeId() != wayNodes.get(wayNodes.size() - 1).getNodeId()) {
                noCycle = true;
            }
        }
        for (Tag tag : way.getTags()) {
            if (hasHighway(tag)) {
                hasHighway = true;
            }
            if (noCycle && hasHighway) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasHighway(Tag tag) {
        return TAG_KEY_HIGHWAY.equals(tag.getKey());
    }

}
