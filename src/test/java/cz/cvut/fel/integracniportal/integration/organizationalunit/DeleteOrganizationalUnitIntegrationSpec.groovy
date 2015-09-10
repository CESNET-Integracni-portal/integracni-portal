package cz.cvut.fel.integracniportal.integration.organizationalunit

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.group.DeleteGroupCommand
import cz.cvut.fel.integracniportal.command.organizationalunit.CreateOrganizationalUnitCommand
import cz.cvut.fel.integracniportal.command.organizationalunit.DeleteOrganizationalUnitCommand
import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId
import cz.cvut.fel.integracniportal.model.OrganizationalUnit

/**
 * Integration test for {@link DeleteGroupCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class DeleteOrganizationalUnitIntegrationSpec extends AbstractIntegrationSpecification {

    def id = "2"

    def "should delete a group"() {
        given:
            dispatch new CreateOrganizationalUnitCommand(OrganizationalUnitId.of(id), "newUnit", 1000)

            assert get(OrganizationalUnit, id) != null

        when:
            dispatch new DeleteOrganizationalUnitCommand(OrganizationalUnitId.of(id))

        then:
            get(OrganizationalUnit, id) == null
    }

}
