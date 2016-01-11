package cz.cvut.fel.integracniportal.domain.node.saga;

import cz.cvut.fel.integracniportal.command.node.MoveFileOfflineCommand;
import cz.cvut.fel.integracniportal.command.node.MoveFileOnlineCommand;
import cz.cvut.fel.integracniportal.command.node.MoveFolderOfflineCommand;
import cz.cvut.fel.integracniportal.command.node.MoveFolderOnlineCommand;
import cz.cvut.fel.integracniportal.dao.NodeNameDao;
import cz.cvut.fel.integracniportal.domain.node.events.FolderMovedOfflineEvent;
import cz.cvut.fel.integracniportal.domain.node.events.FolderMovedOnlineEvent;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.model.NodeName;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * A saga that recursively sets online/offline status of the file.
 *
 * @author Radek Jezdik
 */
public class FolderMigrationSaga extends AbstractAnnotatedSaga {

    /**
     * Sends the actual commands. Implementing classes are responsible for
     * sending correct commands.
     */
    private interface Migrator {

        void migrateFolder(FolderId folderId);

        void migrateFile(FileId fileId);

    }

    @Autowired
    private NodeNameDao nodeNameDao;

    @Autowired
    private CommandGateway gateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(FolderMovedOnlineEvent event) {
        migrate(event.getId(), onlineMigrator());
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(FolderMovedOfflineEvent event) {
        migrate(event.getId(), offlineMigrator());
    }

    private void migrate(FolderId folderId, Migrator migrator) {
        List<NodeName> childNodes = nodeNameDao.getChildNodes(folderId);

        for (NodeName node : childNodes) {
            if (node.isFolder()) {
                FolderId id = FolderId.of(node.getId());
                migrator.migrateFolder(id);

            } else {
                FileId id = FileId.of(node.getId());
                migrator.migrateFile(id);
            }
        }

        end();
    }

    private Migrator onlineMigrator() {
        return new Migrator() {
            @Override
            public void migrateFolder(FolderId folderId) {
                gateway.send(new MoveFolderOnlineCommand(folderId));
            }

            @Override
            public void migrateFile(FileId fileId) {
                gateway.send(new MoveFileOnlineCommand(fileId));
            }
        };
    }

    private Migrator offlineMigrator() {
        return new Migrator() {
            @Override
            public void migrateFolder(FolderId folderId) {
                gateway.send(new MoveFolderOfflineCommand(folderId));
            }

            @Override
            public void migrateFile(FileId fileId) {
                gateway.send(new MoveFileOfflineCommand(fileId));
            }
        };
    }

    public void setNodeNameDao(NodeNameDao nodeNameDao) {
        this.nodeNameDao = nodeNameDao;
    }

    public void setGateway(CommandGateway gateway) {
        this.gateway = gateway;
    }

}
