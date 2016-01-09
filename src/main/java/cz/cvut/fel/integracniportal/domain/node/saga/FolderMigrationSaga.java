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
 * @author Radek Jezdik
 */
public class FolderMigrationSaga extends AbstractAnnotatedSaga {

    @Autowired
    private NodeNameDao nodeNameDao;

    @Autowired
    private CommandGateway gateway;

    private boolean started = false;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(FolderMovedOnlineEvent event) {
        FolderId folderId = event.getId();

        List<NodeName> childNodes = nodeNameDao.getChildNodes(event.getId());

        if (childNodes.isEmpty() == false) {
            for (NodeName node : childNodes) {
                if (node.isFolder()) {
                    FolderId id = FolderId.of(node.getId());
                    gateway.send(new MoveFolderOnlineCommand(id));

                } else {
                    FileId id = FileId.of(node.getId());
                    gateway.send(new MoveFileOnlineCommand(id));
                }
            }
        }
        end();
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(FolderMovedOfflineEvent event) {
        FolderId folderId = event.getId();

        List<NodeName> childNodes = nodeNameDao.getChildNodes(event.getId());

        if (childNodes.isEmpty() == false) {
            for (NodeName node : childNodes) {
                if (node.isFolder()) {
                    FolderId id = FolderId.of(node.getId());
                    gateway.send(new MoveFolderOfflineCommand(id));

                } else {
                    FileId id = FileId.of(node.getId());
                    gateway.send(new MoveFileOfflineCommand(id));
                }
            }
        }
        end();
    }

    public void setNodeNameDao(NodeNameDao nodeNameDao) {
        this.nodeNameDao = nodeNameDao;
    }

    public void setGateway(CommandGateway gateway) {
        this.gateway = gateway;
    }
}
