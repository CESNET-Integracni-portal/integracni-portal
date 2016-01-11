package cz.cvut.fel.integracniportal.unit.node.saga

import cz.cvut.fel.integracniportal.command.node.MoveFileOfflineCommand
import cz.cvut.fel.integracniportal.command.node.MoveFileOnlineCommand
import cz.cvut.fel.integracniportal.command.node.MoveFolderOfflineCommand
import cz.cvut.fel.integracniportal.command.node.MoveFolderOnlineCommand
import cz.cvut.fel.integracniportal.dao.NodeNameDao
import cz.cvut.fel.integracniportal.domain.node.events.FolderMovedOfflineEvent
import cz.cvut.fel.integracniportal.domain.node.events.FolderMovedOnlineEvent
import cz.cvut.fel.integracniportal.domain.node.saga.FolderMigrationSaga
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.model.NodeName
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.test.saga.AnnotatedSagaTestFixture
import spock.lang.Specification

/**
 * @author Radek Jezdik
 */
class FolderMigrationSagaTest extends Specification {

    AnnotatedSagaTestFixture fixture

    NodeNameDao nodeNameDaoMock

    final folderToMigrate = FolderId.of("1")

    def setup() {
        nodeNameDaoMock = Mock(NodeNameDao)

        fixture = new AnnotatedSagaTestFixture(FolderMigrationSaga)
        fixture.registerResource(nodeNameDaoMock)
        fixture.registerCommandGateway(CommandGateway)
    }

    def "sends commands to migrate folder's children online"() {
        when:
            def then = fixture
                    .whenAggregate(folderToMigrate)
                    .publishes(new FolderMovedOnlineEvent(folderToMigrate))

        then:
            1 * nodeNameDaoMock.getChildNodes(folderToMigrate) >> [
                    new NodeName(id: "2", isFolder: true),
                    new NodeName(id: "3", isFolder: false)
            ]

            then.expectDispatchedCommandsEqualTo(
                    new MoveFolderOnlineCommand(FolderId.of("2")),
                    new MoveFileOnlineCommand(FileId.of("3"))
            )
    }

    def "sends commands to migrate folder's children offline"() {
        when:
            def then = fixture
                    .whenAggregate(folderToMigrate)
                    .publishes(new FolderMovedOfflineEvent(folderToMigrate))

        then:
            1 * nodeNameDaoMock.getChildNodes(folderToMigrate) >> [
                    new NodeName(id: "2", isFolder: true),
                    new NodeName(id: "3", isFolder: false)
            ]

            then.expectDispatchedCommandsEqualTo(
                    new MoveFolderOfflineCommand(FolderId.of("2")),
                    new MoveFileOfflineCommand(FileId.of("3"))
            )
    }

}
