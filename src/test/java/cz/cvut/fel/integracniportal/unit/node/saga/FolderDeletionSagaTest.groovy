package cz.cvut.fel.integracniportal.unit.node.saga

import cz.cvut.fel.integracniportal.command.node.DeleteFileCommand
import cz.cvut.fel.integracniportal.command.node.DeleteFolderCommand
import cz.cvut.fel.integracniportal.command.node.DeleteFolderInternalCommand
import cz.cvut.fel.integracniportal.dao.FolderDao
import cz.cvut.fel.integracniportal.domain.node.events.FileDeletedEvent
import cz.cvut.fel.integracniportal.domain.node.events.FolderDeletedEvent
import cz.cvut.fel.integracniportal.domain.node.events.FolderDeletionStartedEvent
import cz.cvut.fel.integracniportal.domain.node.saga.FolderDeletionSaga
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.model.FileMetadata
import cz.cvut.fel.integracniportal.model.Folder
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.test.saga.AnnotatedSagaTestFixture
import spock.lang.Specification

/**
 * @author Radek Jezdik
 */
class FolderDeletionSagaTest extends Specification {

    AnnotatedSagaTestFixture fixture

    FolderDao folderDaoMock

    def setup() {
        folderDaoMock = Mock(FolderDao)

        fixture = new AnnotatedSagaTestFixture(FolderDeletionSaga)
        fixture.registerResource(folderDaoMock)
        fixture.registerCommandGateway(CommandGateway)
    }

    def "sends commands to delete folder's children on start"() {
        when:
            def then = fixture
                    .whenAggregate(FolderId.of("1"))
                    .publishes(new FolderDeletionStartedEvent(FolderId.of("1")))

        then:
            1 * folderDaoMock.get("1") >> folderWithChildren()

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
                    .givenAggregate(FolderId.of("1"))
                    .published(
                        new FolderDeletionStartedEvent(FolderId.of("1")),
                        new FolderDeletedEvent(FolderId.of("2"))
                    )
                    .whenPublishingA(new FileDeletedEvent(FileId.of("3")))

        then:
            1 * folderDaoMock.get("1") >> folderWithChildren()

            then.expectDispatchedCommandsEqualTo(new DeleteFolderInternalCommand(FolderId.of("1")))
            then.expectActiveSagas(0)
    }

    def "sends command to delete the original folder with no children right on start"() {
        when:
            def then = fixture
                    .whenAggregate(FolderId.of("1"))
                    .publishes(new FolderDeletionStartedEvent(FolderId.of("1")))

        then:
            1 * folderDaoMock.get("1") >> emptyFolder()

            then.expectDispatchedCommandsEqualTo(new DeleteFolderInternalCommand(FolderId.of("1")))
            then.expectActiveSagas(0)
    }

    private static def folderWithChildren() {
        Folder folder = new Folder()
        folder.setChildNodes([
                new Folder(id: "2"),
                new FileMetadata(id: "3")
        ])
        return folder;
    }

    private static def emptyFolder() {
        return new Folder(id: "1")
    }

}
