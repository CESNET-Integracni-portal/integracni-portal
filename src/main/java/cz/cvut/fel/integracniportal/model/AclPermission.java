package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Holds the Set of NodePermission objects, describing available actions for targetUser on targetNode.
 *
 * @author Eldar Iosip
 */
@Entity
@Table(name = "acl_permission")
public class AclPermission extends AbstractEntity<Long> {

    @Id
    @Column(name = "permission_id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long permissionId;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id")
    private UserDetails owner;

    @ManyToOne
    @JoinColumn(name = "target_user_id", referencedColumnName = "user_id")
    private UserDetails targetUser;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileMetadata targetFile;

    @Column(name = "node_permissions")
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<NodePermission> nodePermissions;

    public AclPermission() {
        this.nodePermissions = new HashSet<NodePermission>();
    }

    @Override
    public Long getId() {
        return permissionId;
    }

    @Override
    public void setId(Long id) {
        this.permissionId = id;
    }

    public UserDetails getOwner() {
        return owner;
    }

    public void setOwner(UserDetails owner) {
        this.owner = owner;
    }

    public UserDetails getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(UserDetails targetUser) {
        this.targetUser = targetUser;
    }

    public FileMetadata getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(FileMetadata targetFile) {
        this.targetFile = targetFile;
    }

    public Set<NodePermission> getNodePermissions() {
        return nodePermissions;
    }

    public void setNodePermissions(Set<NodePermission> nodePermissions) {
        this.nodePermissions = nodePermissions;
    }

}
