package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.LabelDao;
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.Label;
import cz.cvut.fel.integracniportal.representation.LabelRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Vavat on 24. 3. 2015.
 */
@Service
@Transactional
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelDao labelDao;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public List<Label> getAllLabels() {
        return labelDao.getAllLabels();
    }

    @Override
    public List<Label> getUserLabels(long userId) {
        List<Label> result = labelDao.getUserLabels(userId);
        if(result == null){
            throw new NotFoundException("labels.notFound.user.id", userId);
        }
        return result;
    }

    @Override
    public Label getLabelByName(String name) {
        return labelDao.getLabelByName(name);
    }

    @Override
    public Label getLabelById(long labelId) {
        Label label = labelDao.getLabelById(labelId);
        if(label == null){
            throw new NotFoundException("label.notFound.id", labelId);
        }
        return label;
    }

    @Override
    public Label createLabel(LabelRepresentation labelRepresentation) {
        if(getLabelByName(labelRepresentation.getName()) != null){
            throw new AlreadyExistsException("label.alreadyExists");
        }
        Label label = new Label();
        updateLabelFromRepresentation(label, labelRepresentation);
        labelDao.save(label);
        return label;
    }

    @Override
    public Label updateLabel(Long labelId, LabelRepresentation labelRepresentation) {
        Label label = getLabelById(labelId);
        updateLabelFromRepresentation(label, labelRepresentation);
        labelDao.save(label);
        return label;
    }

    private void updateLabelFromRepresentation(Label label, LabelRepresentation labelRepresentation){
        if(labelRepresentation.getId() != null){
            label.setId(labelRepresentation.getId());
        }
        if(labelRepresentation.getName() != null){
            label.setName(labelRepresentation.getName());
        }
        if(labelRepresentation.getColor() != null){
            label.setColor(labelRepresentation.getColor());
        }
        if(labelRepresentation.getOwner() != null){
            label.setOwner(userDetailsService.getUserById(labelRepresentation.getOwner()));
        }
    }

    @Override
    public void saveLabel(Label label) {
        labelDao.save(label);
    }

    @Override
    public void removeLabel(Label label) {
        labelDao.delete(label);
    }

    public void setLabelDao(LabelDao labelDao) {
        this.labelDao = labelDao;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
