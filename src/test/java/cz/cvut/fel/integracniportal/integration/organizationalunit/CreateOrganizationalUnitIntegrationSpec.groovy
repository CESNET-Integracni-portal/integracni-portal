package cz.cvut.fel.integracniportal.integration.organizationalunit

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.organizationalunit.CreateOrganizationalUnitCommand
import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException
import cz.cvut.fel.integracniportal.model.OrganizationalUnit

/**
 * Integration test for {@link CreateOrganizationalUnitCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class CreateOrganizationalUnitIntegrationSpec extends AbstractIntegrationSpecification {

    def "should create a unit"() {
        when:
            dispatch new CreateOrganizationalUnitCommand(OrganizationalUnitId.of("2"), "newUnit", 1000)

        then:
            def unit = get(OrganizationalUnit, "2")
            unit.id == "2"
            unit.name == "newUnit"
    }

    def "creating duplicate unit name results in error"() {
        when:
            dispatch new CreateOrganizationalUnitCommand(OrganizationalUnitId.of("2"), "newUnit", 1000)
            dispatch new CreateOrganizationalUnitCommand(OrganizationalUnitId.of("3"), "newUnit", 1000)

        then:
            thrown(AlreadyExistsException)
    }

}
