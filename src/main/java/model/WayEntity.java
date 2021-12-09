package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

public class WayEntity {

    private final long id;
    private final List<NodeEntity> nodes = new ArrayList<>();

    public WayEntity(long id, Collection<NodeEntity> nodes) {
        this.id = id;
        addAllNodes(nodes);
    }

    public long getId() {
        return id;
    }


    public void addAllNodes(Collection<NodeEntity> nodes) {
        this.nodes.addAll(nodes);
    }

    public List<NodeEntity> getNodes() {
        return nodes;
    }

}

