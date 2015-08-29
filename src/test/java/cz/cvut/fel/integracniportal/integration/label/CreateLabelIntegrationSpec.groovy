package cz.cvut.fel.integracniportal.integration.label

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.label.CreateLabelCommand
import cz.cvut.fel.integracniportal.command.node.MoveFileCommand
import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException
import cz.cvut.fel.integracniportal.model.Label

/**
 * Integration test for {@link MoveFileCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class CreateLabelIntegrationSpec extends AbstractIntegrationSpecification {

    def "should create a label"() {
        when:
            dispatch new CreateLabelCommand(LabelId.of("1"), "work", "red")

        then:
            def label = get(Label, "1")
            label.id == "1"
            label.name == "work"
            label.color == "red"
            label.getOwner().getId() == 1
    }

    def "duplicate label (name, color, owner) results in error"() {
        when:
            dispatch new CreateLabelCommand(LabelId.of("1"), "work", "red")
            dispatch new CreateLabelCommand(LabelId.of("2"), "work", "red")

        then:
            thrown(AlreadyExistsException)
    }

}
