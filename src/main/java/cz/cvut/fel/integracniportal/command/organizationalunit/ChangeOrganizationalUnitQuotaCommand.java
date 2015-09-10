package cz.cvut.fel.integracniportal.command.organizationalunit;

import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class ChangeOrganizationalUnitQuotaCommand {

    private final OrganizationalUnitId id;

    private final long quota;

}
