package cz.cvut.fel.integracniportal.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Abstract super class representing a node in our file system.
 */
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"parentId", "name"}),
        @UniqueConstraint(columnNames = {"userId", "name", "space"})
})
public class NodeName extends AbstractEntity<String> {

    @Id
    private String nodeId;

    private String name;

    private String userId;

    private String parentId;

    private String space;

    @Override
    public String getId() {
        return nodeId;
    }

    @Override
    public void setId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }
}
