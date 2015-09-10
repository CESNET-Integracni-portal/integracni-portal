package cz.cvut.fel.integracniportal.domain.organizationalunit;

import cz.cvut.fel.integracniportal.domain.organizationalunit.events.*;
import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@NoArgsConstructor
@Getter
public class OrganizationalUnit extends AbstractAnnotatedAggregateRoot<OrganizationalUnitId> {

    @AggregateIdentifier
    private OrganizationalUnitId id;

    private String name;

    private long quota;

    private Set<UserId> admins = new HashSet<UserId>();

    public OrganizationalUnit(OrganizationalUnitId id, String name, long quota) {
        apply(new OrganizationalUnitCreatedEvent(id, name, quota));
    }

    public void rename(String newName) {
        if (name.equals(newName)) {
            return;
        }
        apply(new OrganizationalUnitRenamedEvent(id, newName));
    }

    public void delete() {
        apply(new OrganizationalUnitDeletedEvent(id));
    }

    public void setQuota(long newQuota) {
        if (this.quota == newQuota) {
            return;
        }
        apply(new OrganizationalUnitQuotaChangedEvent(id, newQuota, quota));
    }

    public void addAdmin(UserId admin) {
        if (admins.contains(admin)) {
            return;
        }

        apply(new OrganizationalUnitAdminAssignedEvent(id, admin));
    }

    public void removeAdmin(UserId admin) {
        if (admins.contains(admin) == false) {
            return;
        }

        apply(new OrganizationalUnitAdminUnassignedEvent(id, admin));
    }

    @EventSourcingHandler
    public void onQuotaCreated(OrganizationalUnitCreatedEvent event) {
        id = event.getId();
        name = event.getName();
        quota = event.getQuotaSize();
    }

    @EventSourcingHandler
    public void onQuotaRenamed(OrganizationalUnitRenamedEvent event) {
        name = event.getNewName();
    }

    @EventSourcingHandler
    public void onQuotaDeleted(OrganizationalUnitDeletedEvent event) {
        markDeleted();
    }

    @EventSourcingHandler
    public void onQuotaChanged(OrganizationalUnitQuotaChangedEvent event) {
        markDeleted();
    }

    @EventSourcingHandler
    public void onAdminAssined(OrganizationalUnitAdminAssignedEvent event) {
        admins.add(event.getAssignedAdmin());
    }

    @EventSourcingHandler
    public void onAdminUnassigned(OrganizationalUnitAdminUnassignedEvent event) {
        admins.add(event.getUnassignedAdmin());
    }
}
