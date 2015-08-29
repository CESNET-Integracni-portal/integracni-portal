package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.command.label.*;
import cz.cvut.fel.integracniportal.dao.LabelDao;
import cz.cvut.fel.integracniportal.domain.label.valueobjects.LabelId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.NodeId;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.Label;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.LabelRepresentation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 *
 */
@Service
@Transactional
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelDao labelDao;

    @Autowired
    private CommandGateway commandGateway;

    @Override
    public List<Label> getUserLabels(UserDetails owner) {
        List<Label> result = labelDao.getUserLabels(owner.getId());
        if (result == null) {
            throw new NotFoundException("labels.notFound.user.id", owner);
        }
        return result;
    }

    @Override
    public Label getLabelById(String labelId) {
        Label label = labelDao.get(labelId);
        if (label == null) {
            throw new NotFoundException("label.notFound.id", labelId);
        }
        return label;
    }

    @Override
    public Label createLabel(LabelRepresentation labelRepresentation, UserDetails owner) {
        String id = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new CreateLabelCommand(
                LabelId.of(id),
                labelRepresentation.getName(),
                labelRepresentation.getColor()
        ));

        return labelDao.get(id);
    }

    @Override
    public Label updateLabel(String labelId, LabelRepresentation labelRepresentation) {
        commandGateway.sendAndWait(new UpdateLabelCommand(
                LabelId.of(labelId),
                labelRepresentation.getName(),
                labelRepresentation.getColor()
        ));

        return labelDao.get(labelId);
    }

    @Override
    public void deleteLabel(String labelId) {
        commandGateway.sendAndWait(new DeleteLabelCommand(
                LabelId.of(labelId)
        ));
    }

    @Override
    public void addLabelToFile(String fileId, String labelId) {
        addLabelToNode(FileId.of(fileId), labelId);
    }

    @Override
    public void removeLabelFromFile(String fileId, String labelId) {
        removeLabelToNode(FileId.of(fileId), labelId);
    }

    @Override
    public void addLabelToFolder(String folderId, String labelId) {
        addLabelToNode(FolderId.of(folderId), labelId);
    }

    @Override
    public void removeLabelFromFolder(String folderId, String labelId) {
        removeLabelToNode(FolderId.of(folderId), labelId);
    }

    private void addLabelToNode(NodeId nodeId, String labelId) {
        commandGateway.sendAndWait(new AddLabelToNodeCommand(
                LabelId.of(labelId),
                nodeId
        ));
    }

    private void removeLabelToNode(NodeId nodeId, String labelId) {
        commandGateway.sendAndWait(new RemoveLabelFromNodeCommand(
                LabelId.of(labelId),
                nodeId
        ));
    }

}
