package cz.cvut.fel.integracniportal.domain.label;

import cz.cvut.fel.integracniportal.command.label.*;
import cz.cvut.fel.integracniportal.dao.LabelDao;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Radek Jezdik
 */
@Component
public class LabelCommandHandler {

    @Resource(name = "labelAggregateRepository")
    private Repository<Label> repository;

    @Autowired
    private LabelDao labelDao;

    @CommandHandler
    public void handle(CreateLabelCommand command) {
        createLabel(command);
    }

    @CommandHandler
    public void handle(EditLabelCommand command) {
        editLabel(command);
    }

    @CommandHandler
    public void handle(DeleteLabelCommand command) {
        deleteLabel(command);
    }

    @CommandHandler
    public void handle(AddLabelToNodeCommand command) {
        addLabelToNode(command);
    }

    @CommandHandler
    public void handle(RemoveLabelFromNodeCommand command) {
        removeLabelFromNode(command);
    }

    private void createLabel(CreateLabelCommand command) {
        checkUnique(command.getSentBy(), command.getName(), command.getColor());

        Label label = new Label(
                command.getId(),
                command.getName(),
                command.getColor(),
                command.getSentBy()
        );

        repository.add(label);
    }

    private void editLabel(EditLabelCommand command) {
        Label label = repository.load(command.getId());

        checkUnique(label.getOwner(), command.getName(), command.getColor());

        label.edit(command.getName(), command.getColor());
    }

    private void deleteLabel(DeleteLabelCommand command) {
        Label label = repository.load(command.getId());
        label.delete();
    }

    private void addLabelToNode(AddLabelToNodeCommand command) {
        Label label = repository.load(command.getId());
        label.addToNode(command.getNodeId());
    }

    private void removeLabelFromNode(RemoveLabelFromNodeCommand command) {
        Label label = repository.load(command.getId());
        label.removeFromNode(command.getNodeId());
    }

    private void checkUnique(UserId owner, String name, String color) {
        if (labelDao.labelExists(owner.getId(), name, color)) {
            throw new AlreadyExistsException("label.alreadyExists");
        }
    }

}
