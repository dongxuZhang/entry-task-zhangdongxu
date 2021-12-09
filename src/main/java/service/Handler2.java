package service;

import static constant.CommonConstant.DOUBLE_ZERO;
import static constant.CommonConstant.INT_ZERO;
import static constant.CommonConstant.TAG_KEY_HIGHWAY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import model.NodeEntity;
import model.WayEntity;
import org.openstreetmap.osmosis.core.container.v0_6.BoundContainer;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.springframework.util.CollectionUtils;
import utils.CommonUtils;

public class Handler2 implements Sink {

    private ExecutorService executorService;
    private final Map<Long, NodeEntity> nodes = new HashMap<>();
    private final Map<Long, WayEntity> ways = new HashMap<>();
    private final AtomicBoolean flag = new AtomicBoolean(true);

    private double totalLength = 0.0;
    private int nodeUpdateCount = INT_ZERO;
    private double roadLength = DOUBLE_ZERO;

    public void initialize(Map<String, Object> metaData) {
        executorService = Executors.newFixedThreadPool(50);
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
        } else if (flag.get() && entityContainer instanceof WayContainer) {
            Way way = ((WayContainer) entityContainer).getEntity();
            if (isRoad(way)) {
                List<NodeEntity> nodeEntities = new ArrayList<>();
                way.getWayNodes().forEach(wayNode -> {
                    NodeEntity nodeEntity;
                    if (!nodes.containsKey(wayNode.getNodeId())) {
                        nodeEntity = new NodeEntity(wayNode.getNodeId());
                        nodes.put(nodeEntity.getId(), nodeEntity);
                        nodeEntities.add(nodeEntity);
                    } else {
                        nodeEntity = nodes.get(wayNode.getNodeId());
                        nodeEntities.add(nodeEntity);
                    }
                });
                ways.put(way.getId(), new WayEntity(way.getId(), nodeEntities));
            }
        }
    }

    @Override
    public void close() {
        if (!flag.get()) {
            ways.values().forEach(way -> {
                long begin = System.currentTimeMillis();
                List<NodeEntity> wayNodes = way.getNodes();
                for (int i = 0; i < wayNodes.size() - 1; i++) {
                    NodeEntity node1 = wayNodes.get(i);
                    NodeEntity node2 = wayNodes.get(i + 1);
                    double result = CommonUtils.getDistance(node1.getLat(), node1.getLon(),
                        node2.getLat(), node2.getLon());
                    roadLength += result;
                    totalLength += result;
                }
                long end = System.currentTimeMillis();
                System.out.println("compute cost:" + (end - begin));
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
