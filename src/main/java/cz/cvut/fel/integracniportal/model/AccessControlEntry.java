package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Access Control Entry.
 * <p>
 * Holds the Set of AccessControlPermission objects, describing available actions for targetUser on targetNode.
 *
 * @author Eldar Iosip
 */
@Entity
@Table(name = "access_control_entry")
public class AccessControlEntry extends AbstractEntity<Long> {

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

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder targetFolder;

    @Column(name = "node_permissions")
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<AccessControlPermission> accessControlPermissions;

    public AccessControlEntry() {
        this.accessControlPermissions = new HashSet<AccessControlPermission>();
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

    public Folder getTargetFolder() {
        return targetFolder;
    }

    public void setTargetFolder(Folder targetFolder) {
        this.targetFolder = targetFolder;
    }

    public Set<AccessControlPermission> getAccessControlPermissions() {
        return accessControlPermissions;
    }

    public void setAccessControlPermissions(Set<AccessControlPermission> accessControlPermissions) {
        this.accessControlPermissions = accessControlPermissions;
    }

}
