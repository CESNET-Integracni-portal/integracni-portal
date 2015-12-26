package cz.cvut.fel.integracniportal.domain.node.saga;

import cz.cvut.fel.integracniportal.command.node.DeleteFileCommand;
import cz.cvut.fel.integracniportal.command.node.DeleteFolderCommand;
import cz.cvut.fel.integracniportal.command.node.DeleteFolderInternalCommand;
import cz.cvut.fel.integracniportal.dao.NodeNameDao;
import cz.cvut.fel.integracniportal.domain.node.events.FolderDeletionStartedEvent;
import cz.cvut.fel.integracniportal.domain.node.events.NodeDeletedEvent;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.NodeId;
import cz.cvut.fel.integracniportal.exceptions.IllegalOperationException;
import cz.cvut.fel.integracniportal.model.NodeName;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
public class FolderDeletionSaga extends AbstractAnnotatedSaga {

    @Autowired
    private NodeNameDao nodeNameDao;

    @Autowired
    private CommandGateway gateway;

    private Set<NodeId> nodesToDelete = new HashSet<NodeId>();

    private boolean started = false;

    private FolderId folderId;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(FolderDeletionStartedEvent event) {
        if (started) {
            if (folderId.equals(event.getId())) {
                throw new IllegalOperationException("Folder is already being deleted");
            } else {
                return;
            }
        }

        started = true;
        folderId = event.getId();

        List<NodeName> childNodes = nodeNameDao.getChildNodes(event.getId());

        if (childNodes.isEmpty() == false) {
            for (NodeName node : childNodes) {
                NodeId nodeId;
                Object command;

                if (node.isFolder()) {
                    nodeId = FolderId.of(node.getId());
                    command = new DeleteFolderCommand((FolderId) nodeId);
                } else {
                    nodeId = FileId.of(node.getId());
                    command = new DeleteFileCommand((FileId) nodeId);
                }
                associateWith("toDelete", nodeId.toString());
                nodesToDelete.add(nodeId);
                gateway.send(command);
            }

        } else {
            deleteTargetFolder();
        }
    }

    @SagaEventHandler(associationProperty = "id", keyName = "toDelete")
    public void handle(NodeDeletedEvent event) {
        nodesToDelete.remove(event.getId());

        if (nodesToDelete.isEmpty()) {
            deleteTargetFolder();
        }
    }

    private void deleteTargetFolder() {
        gateway.send(new DeleteFolderInternalCommand(folderId));
        end();
    }

    public void setNodeNameDao(NodeNameDao nodeNameDao) {
        this.nodeNameDao = nodeNameDao;
    }

    public void setGateway(CommandGateway gateway) {
        this.gateway = gateway;
    }
}
