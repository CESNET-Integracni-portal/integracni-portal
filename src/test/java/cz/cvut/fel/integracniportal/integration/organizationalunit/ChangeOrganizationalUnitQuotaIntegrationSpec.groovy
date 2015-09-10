package cz.cvut.fel.integracniportal.integration.organizationalunit

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.organizationalunit.ChangeOrganizationalUnitQuotaCommand
import cz.cvut.fel.integracniportal.command.organizationalunit.CreateOrganizationalUnitCommand
import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId
import cz.cvut.fel.integracniportal.model.OrganizationalUnit

/**
 * Integration test for {@link ChangeOrganizationalUnitQuotaCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class ChangeOrganizationalUnitQuotaIntegrationSpec extends AbstractIntegrationSpecification {

    def "should rename a unit"() {
        given:
            dispatch new CreateOrganizationalUnitCommand(OrganizationalUnitId.of("2"), "newUnit", 1000)

        when:
            dispatch new ChangeOrganizationalUnitQuotaCommand(OrganizationalUnitId.of("2"), 5000)

        then:
            def unit = get(OrganizationalUnit, "2")
            unit.size == 5000
    }

}
