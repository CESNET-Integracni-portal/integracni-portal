package cz.cvut.fel.integracniportal.integration.label

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.label.CreateLabelCommand
import cz.cvut.fel.integracniportal.command.label.DeleteLabelCommand
import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId
import cz.cvut.fel.integracniportal.model.Label

/**
 * Integration test for {@link DeleteLabelCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class DeleteLabelIntegrationSpec extends AbstractIntegrationSpecification {

    def id = "1"

    def "should delete the label"() {
        given:
            dispatch new CreateLabelCommand(LabelId.of(id), "work", "red")

            assert get(Label, id) != null

        when:
            dispatch new DeleteLabelCommand(LabelId.of(id))

        then:
            get(Label, id) == null
    }

}
