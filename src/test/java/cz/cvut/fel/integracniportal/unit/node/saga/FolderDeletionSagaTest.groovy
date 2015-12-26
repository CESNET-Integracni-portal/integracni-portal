package cz.cvut.fel.integracniportal.unit.node.saga

import cz.cvut.fel.integracniportal.command.node.DeleteFileCommand
import cz.cvut.fel.integracniportal.command.node.DeleteFolderCommand
import cz.cvut.fel.integracniportal.command.node.DeleteFolderInternalCommand
import cz.cvut.fel.integracniportal.dao.NodeNameDao
import cz.cvut.fel.integracniportal.domain.node.events.FileDeletedEvent
import cz.cvut.fel.integracniportal.domain.node.events.FolderDeletedEvent
import cz.cvut.fel.integracniportal.domain.node.events.FolderDeletionStartedEvent
import cz.cvut.fel.integracniportal.domain.node.saga.FolderDeletionSaga
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.model.NodeName
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.test.saga.AnnotatedSagaTestFixture
import spock.lang.Specification

/**
 * @author Radek Jezdik
 */
class FolderDeletionSagaTest extends Specification {

    AnnotatedSagaTestFixture fixture

    NodeNameDao nodeNameDaoMock

    final folderToDelete = FolderId.of("1")

    def setup() {
        nodeNameDaoMock = Mock(NodeNameDao)

        fixture = new AnnotatedSagaTestFixture(FolderDeletionSaga)
        fixture.registerResource(nodeNameDaoMock)
        fixture.registerCommandGateway(CommandGateway)
    }

    def "sends commands to delete folder's children on start"() {
        when:
            def then = fixture
                    .whenAggregate(folderToDelete)
                    .publishes(new FolderDeletionStartedEvent(folderToDelete))

        then:
            1 * nodeNameDaoMock.getChildNodes(folderToDelete) >> [
                    new NodeName(id: "2", isFolder: true),
                    new NodeName(id: "3", isFolder: false)
            ]

            then.expectDispatchedCommandsEqualTo(
                    new DeleteFolderCommand(FolderId.of("2")),
                    new DeleteFileCommand(FileId.of("3"))
            )
            then.expectAssociationWith("toDelete", FolderId.of("2"))
            then.expectAssociationWith("toDelete", FileId.of("3"))
    }

    def "sends command to delete the original folder after all children were deleted"() {
        when:
            def then = fixture
                    .givenAggregate(folderToDelete)
                    .published(
                        new FolderDeletionStartedEvent(folderToDelete),
                        new FolderDeletedEvent(FolderId.of("2"))
                    )
                    .whenPublishingA(new FileDeletedEvent(FileId.of("3")))

        then:
            1 * nodeNameDaoMock.getChildNodes(folderToDelete) >> [
                    new NodeName(id: "2", isFolder: true),
                    new NodeName(id: "3", isFolder: false)
            ]

            then.expectDispatchedCommandsEqualTo(new DeleteFolderInternalCommand(folderToDelete))
            then.expectActiveSagas(0)
    }

    def "sends command to delete the original folder with no children right on start"() {
        when:
            def then = fixture
                    .whenAggregate(folderToDelete)
                    .publishes(new FolderDeletionStartedEvent(folderToDelete))

        then:
            1 * nodeNameDaoMock.getChildNodes(folderToDelete) >> []

            then.expectDispatchedCommandsEqualTo(new DeleteFolderInternalCommand(folderToDelete))
            then.expectActiveSagas(0)
    }

}
