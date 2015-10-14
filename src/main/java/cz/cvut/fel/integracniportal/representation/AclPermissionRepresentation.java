package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Representation class for acl permission.
 *
 * @author Eldar Iosip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AclPermissionRepresentation {

    private Long id;

    private List<NodePermissionRepresentation> nodePermissions;

    private UserDetailsRepresentation targetUser;

    private GroupRepresentation targetGroup;

    private NodeRepresentation node;

    public AclPermissionRepresentation() {
    }

    public AclPermissionRepresentation(AclPermission aclPermission) {
        this.id = aclPermission.getId();

        for (NodePermission nodePermission : aclPermission.getNodePermissions()) {
            this.nodePermissions.add(new NodePermissionRepresentation(nodePermission));
        }

        //TODO: avoid != null check
        if (aclPermission.getTargetUser() != null) {
            this.targetUser = new UserDetailsRepresentation(aclPermission.getTargetUser());
        }

        if (aclPermission.getTargetGroup() != null) {
            this.targetGroup = new GroupRepresentation(aclPermission.getTargetGroup());
        }

        this.node = new NodeRepresentation(aclPermission.getNode());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<NodePermissionRepresentation> getNodePermissions() {
        return nodePermissions;
    }

    public void setNodePermissions(List<NodePermissionRepresentation> nodePermissions) {
        this.nodePermissions = nodePermissions;
    }

    public UserDetailsRepresentation getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(UserDetailsRepresentation targetUser) {
        this.targetUser = targetUser;
    }

    public GroupRepresentation getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(GroupRepresentation targetGroup) {
        this.targetGroup = targetGroup;
    }

    public NodeRepresentation getNode() {
        return node;
    }

    public void setNode(NodeRepresentation node) {
        this.node = node;
    }
}
