package cz.cvut.fel.integracniportal.query.label;

import cz.cvut.fel.integracniportal.dao.LabelDao;
import cz.cvut.fel.integracniportal.dao.NodeDaoImpl;
import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.domain.label.events.*;
import cz.cvut.fel.integracniportal.model.Label;
import cz.cvut.fel.integracniportal.model.Node;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author Radek Jezdik
 */
@Component
public class LabelUpdater {

    @Autowired
    private LabelDao labelDao;

    @Autowired
    private UserDetailsDao userDao;

    @Autowired
    private NodeDaoImpl nodeDao;

    @EventHandler
    public void createLabel(LabelCreatedEvent event) {
        UserDetails owner = userDao.getReference(event.getOwner().getId());

        Label label = new Label();
        label.setId(event.getId().getId());
        label.setOwner(owner);
        label.setName(event.getName());
        label.setColor(event.getColor());

        labelDao.save(label);
    }

    @EventHandler
    public void updateLabel(LabelUpdatedEvent event) {
        Label label = labelDao.load(event.getId().getId());
        label.setName(event.getName());
        label.setColor(event.getColor());
    }

    @EventHandler
    public void updateLabel(LabelDeletedEvent event) {
        Label label = labelDao.load(event.getId().getId());
        labelDao.delete(label);
    }

    @EventHandler
    public void addLabel(LabelAddedToNodeEvent event) {
        Label label = labelDao.load(event.getId().getId());
        Node node = nodeDao.get(event.getNodeId().getId());

        if (node.getLabels() == null) {
            node.setLabels(new ArrayList<Label>());
        }

        node.getLabels().add(label);

    }

    @EventHandler
    public void removeLabel(LabelRemovedFromNodeEvent event) {
        Label label = labelDao.load(event.getId().getId());
        Node node = nodeDao.get(event.getNodeId().getId());

        if (node.getLabels() == null) {
            return;
        }

        node.getLabels().remove(label);

    }

}
