package cz.cvut.fel.integracniportal.domain.organizationalunit.events;

import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class OrganizationalUnitDeletedEvent {

    private final OrganizationalUnitId id;

}
