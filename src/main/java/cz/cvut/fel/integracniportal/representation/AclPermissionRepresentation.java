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

    private AbstractUserRepresentation targetUser;

    public AclPermissionRepresentation() {
    }

    public AclPermissionRepresentation(AclPermission aclPermission) {
        this.id = aclPermission.getId();

        for (NodePermission nodePermission : aclPermission.getNodePermissions()) {
            this.nodePermissions.add(new NodePermissionRepresentation(nodePermission));
        }

        if (aclPermission.getTargetUser() != null) {
            this.targetUser = new AbstractUserRepresentation(aclPermission.getTargetUser());
        }
    }

    public Long getId() {
        return id;
    }

    public List<NodePermissionRepresentation> getNodePermissions() {
        return nodePermissions;
    }

    public AbstractUserRepresentation getTargetUser() {
        return targetUser;
    }
}
