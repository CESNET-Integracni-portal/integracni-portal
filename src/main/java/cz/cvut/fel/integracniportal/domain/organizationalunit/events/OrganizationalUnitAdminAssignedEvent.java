package cz.cvut.fel.integracniportal.domain.organizationalunit.events;

import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class OrganizationalUnitAdminAssignedEvent {

    private final OrganizationalUnitId id;

    private final UserId assignedAdmin;

}
