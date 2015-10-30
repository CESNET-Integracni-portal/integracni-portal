package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.*;

import java.util.List;

/**
 * Representation class for acl permission.
 *
 * @author Eldar Iosip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AclPermissionRepresentation {

    private Long id;

    private List<NodePermission> nodePermissions;

    private AbstractUserRepresentation targetUser;

    public AclPermissionRepresentation() {
    }

    public AclPermissionRepresentation(AclPermission aclPermission) {
        this.id = aclPermission.getId();

        for (NodePermission nodePermission : aclPermission.getNodePermissions()) {
            this.nodePermissions.add(nodePermission);
        }

        if (aclPermission.getTargetUser() != null) {
            this.targetUser = new AbstractUserRepresentation(aclPermission.getTargetUser());
        }
    }

    public Long getId() {
        return id;
    }

    public List<NodePermission> getNodePermissions() {
        return nodePermissions;
    }

    public AbstractUserRepresentation getTargetUser() {
        return targetUser;
    }
}
