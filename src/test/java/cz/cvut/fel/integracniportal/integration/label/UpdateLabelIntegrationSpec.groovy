package cz.cvut.fel.integracniportal.integration.label

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.label.CreateLabelCommand
import cz.cvut.fel.integracniportal.command.label.UpdateLabelCommand
import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException
import cz.cvut.fel.integracniportal.model.Label

/**
 * Integration test for {@link UpdateLabelCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class UpdateLabelIntegrationSpec extends AbstractIntegrationSpecification {

    def "should update the label"() {
        given:
            dispatch new CreateLabelCommand(LabelId.of("1"), "work", "red")

        when:
            dispatch new UpdateLabelCommand(LabelId.of("1"), "school", "blue")

        then:
            def label = get(Label, "1")
            label.id == "1"
            label.name == "school"
            label.color == "blue"
            label.getOwner().getId() == 1
    }

    def "updating label resulting in duplicate label (name, color, user) throws exception"() {
        given:
            dispatch new CreateLabelCommand(LabelId.of("1"), "work", "red")
            dispatch new CreateLabelCommand(LabelId.of("2"), "school", "blue")

        when:
            dispatch new UpdateLabelCommand(LabelId.of("1"), "school", "blue")

        then:
            thrown(AlreadyExistsException)
    }

}
