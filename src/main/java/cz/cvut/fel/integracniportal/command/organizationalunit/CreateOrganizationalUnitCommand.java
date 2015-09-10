package cz.cvut.fel.integracniportal.command.organizationalunit;

import cz.cvut.fel.integracniportal.command.node.UserAwareCommand;
import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class CreateOrganizationalUnitCommand extends UserAwareCommand {

    private final OrganizationalUnitId id;

    private final String name;

    private final long quotaSize;

}
