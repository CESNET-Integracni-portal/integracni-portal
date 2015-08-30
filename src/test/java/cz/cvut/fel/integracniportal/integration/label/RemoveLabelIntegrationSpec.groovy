package cz.cvut.fel.integracniportal.integration.label

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.label.AddLabelToNodeCommand
import cz.cvut.fel.integracniportal.command.label.CreateLabelCommand
import cz.cvut.fel.integracniportal.command.label.RemoveLabelFromNodeCommand
import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.model.Folder

/**
 * Integration test for {@link RemoveLabelFromNodeCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class RemoveLabelIntegrationSpec extends AbstractIntegrationSpecification {

    def "should remove a label from a folder"() {
        given:
            dispatch new CreateLabelCommand(LabelId.of("1"), "work", "red")
            dispatch new CreateLabelCommand(LabelId.of("2"), "school", "blue")

            createFolder("3", "foo", null)

            dispatch new AddLabelToNodeCommand(LabelId.of("1"), FolderId.of("3"))
            dispatch new AddLabelToNodeCommand(LabelId.of("2"), FolderId.of("3"))

            assert get(Folder, "3").getLabels().size() == 2

        when:
            dispatch new RemoveLabelFromNodeCommand(LabelId.of("1"), FolderId.of("3"))

        then:
            def labels = get(Folder, "3").getLabels()
            labels.size() == 1

            def label = labels.get(0);
            label.getName() == "school"
            label.getColor() == "blue"
            label.getOwner().getId() == 1
    }

    def "removing label that was not added does nothing"() {
        given:
            dispatch new CreateLabelCommand(LabelId.of("1"), "work", "red")
            dispatch new CreateLabelCommand(LabelId.of("2"), "school", "blue")

            createFolder("3", "foo", null)

            dispatch new AddLabelToNodeCommand(LabelId.of("2"), FolderId.of("3"))

            assert get(Folder, "3").getLabels().size() == 1

        when:
            dispatch new RemoveLabelFromNodeCommand(LabelId.of("1"), FolderId.of("3"))

        then:
            def folder = get(Folder, "3")
            folder.getLabels().size() == 1
            folder.getLabels().get(0).id == "2"
    }

}
