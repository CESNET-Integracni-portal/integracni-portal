package cz.cvut.fel.integracniportal.command.organizationalunit;

import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class AssignOrganizationalUnitAdminCommand {

    private final OrganizationalUnitId id;

    private final UserId admin;

}
