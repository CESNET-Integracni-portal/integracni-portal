package cz.cvut.fel.integracniportal.integration.organizationalunit

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.organizationalunit.CreateOrganizationalUnitCommand
import cz.cvut.fel.integracniportal.command.organizationalunit.RenameOrganizationalUnitCommand
import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException
import cz.cvut.fel.integracniportal.model.OrganizationalUnit

/**
 * Integration test for {@link RenameOrganizationalUnitCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class RenameOrganizationalUnitIntegrationSpec extends AbstractIntegrationSpecification {

    def "should rename a unit"() {
        given:
            dispatch new CreateOrganizationalUnitCommand(OrganizationalUnitId.of("2"), "newUnit", 1000)

        when:
            dispatch new RenameOrganizationalUnitCommand(OrganizationalUnitId.of("2"), "renamedUnit")

        then:
            def unit = get(OrganizationalUnit, "2")
            unit.id == "2"
            unit.name == "renamedUnit"
    }

    def "renaming unit resulting in duplicate name throws exception"() {
        given:
            dispatch new CreateOrganizationalUnitCommand(OrganizationalUnitId.of("2"), "foo", 1000)
            dispatch new CreateOrganizationalUnitCommand(OrganizationalUnitId.of("3"), "bar", 1000)

        when:
            dispatch new RenameOrganizationalUnitCommand(OrganizationalUnitId.of("2"), "bar")

        then:
            thrown(AlreadyExistsException)
    }

}
