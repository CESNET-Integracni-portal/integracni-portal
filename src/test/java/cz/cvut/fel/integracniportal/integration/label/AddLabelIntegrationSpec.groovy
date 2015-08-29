package cz.cvut.fel.integracniportal.integration.label

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.label.AddLabelToNodeCommand
import cz.cvut.fel.integracniportal.command.label.CreateLabelCommand
import cz.cvut.fel.integracniportal.command.node.MoveFileCommand
import cz.cvut.fel.integracniportal.dao.LabelDao
import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.model.Folder
import org.springframework.beans.factory.annotation.Autowired

/**
 * Integration test for {@link MoveFileCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class AddLabelIntegrationSpec extends AbstractIntegrationSpecification {

    @Autowired
    LabelDao labelDao

    def "should add a label to a folder"() {
        given:
            dispatch new CreateLabelCommand(LabelId.of("1"), "work", "red")
            dispatch new CreateLabelCommand(LabelId.of("2"), "school", "blue")

            createFolder("f", "foo", null)

        when:
            dispatch new AddLabelToNodeCommand(LabelId.of("1"), FolderId.of("f"))
            dispatch new AddLabelToNodeCommand(LabelId.of("2"), FolderId.of("f"))

        then:
            def labels = get(Folder, "f").getLabels()
            labels.size() == 2

            def label1 = labels.get(0);
            label1.getName() == "work"
            label1.getColor() == "red"
            label1.getOwner().getId() == 1

            def label2 = labels.get(1);
            label2.getName() == "school"
            label2.getColor() == "blue"
            label2.getOwner().getId() == 1
    }

    def "adding the same label twice does nothing"() {
        given:
            dispatch new CreateLabelCommand(LabelId.of("1"), "work", "red")

            createFolder("2", "foo", null)

        when:
            dispatch new AddLabelToNodeCommand(LabelId.of("1"), FolderId.of("2"))
            dispatch new AddLabelToNodeCommand(LabelId.of("1"), FolderId.of("2"))

        then:
            def labels = get(Folder, "2").getLabels()
            labels.size() == 1
            labels.get(0).id == "1"
    }

}
