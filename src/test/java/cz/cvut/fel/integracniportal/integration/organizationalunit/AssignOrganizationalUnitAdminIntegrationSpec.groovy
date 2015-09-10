package cz.cvut.fel.integracniportal.integration.organizationalunit

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.organizationalunit.AssignOrganizationalUnitAdminCommand
import cz.cvut.fel.integracniportal.command.organizationalunit.CreateOrganizationalUnitCommand
import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId
import cz.cvut.fel.integracniportal.model.OrganizationalUnit

/**
 * Integration test for {@link CreateOrganizationalUnitCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class AssignOrganizationalUnitAdminIntegrationSpec extends AbstractIntegrationSpecification {

    def "should assign a new admin"() {
        given:
            dispatch new CreateOrganizationalUnitCommand(OrganizationalUnitId.of("2"), "newUnit", 1000)

            assert get(OrganizationalUnit, "2").getAdmins().isEmpty()

        when:
            dispatch new AssignOrganizationalUnitAdminCommand(OrganizationalUnitId.of("2"), UserId.of(1))

        then:
            def unit = get(OrganizationalUnit, "2")
            unit.getAdmins().size() == 1
            unit.getAdmins().iterator().next().id == 1
    }

    def "assigning duplicate admin does nothing"() {
        given:
            dispatch new CreateOrganizationalUnitCommand(OrganizationalUnitId.of("2"), "newUnit", 1000)
            dispatch new AssignOrganizationalUnitAdminCommand(OrganizationalUnitId.of("2"), UserId.of(1))

            assert get(OrganizationalUnit, "2").getAdmins().size() == 1

        when:
            dispatch new AssignOrganizationalUnitAdminCommand(OrganizationalUnitId.of("2"), UserId.of(1))

        then:
            def unit = get(OrganizationalUnit, "2")
            unit.getAdmins().size() == 1
            unit.getAdmins().iterator().next().id == 1
    }

}
