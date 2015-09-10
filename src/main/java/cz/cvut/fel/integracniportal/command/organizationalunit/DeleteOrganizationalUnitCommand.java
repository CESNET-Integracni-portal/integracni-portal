package cz.cvut.fel.integracniportal.command.organizationalunit;

import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class DeleteOrganizationalUnitCommand {

    private final OrganizationalUnitId id;

}
