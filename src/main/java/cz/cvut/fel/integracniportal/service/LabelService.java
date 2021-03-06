package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.Label;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.LabelIdRepresentation;
import cz.cvut.fel.integracniportal.representation.LabelRepresentation;

import java.util.List;

/**
 * Created by Vavat on 21. 3. 2015.
 */
/**
 * Service for Label credentials.
 */
public interface LabelService {

    public List<Label> getAllLabels();

    public List<Label> getUserLabels(UserDetails userId);

    public Label getLabelById(long labelId);

    public Label getLabelByName(String name);

    public Label createLabel(LabelRepresentation labelRepresentation, UserDetails owner);

    public Label updateLabel(Long labelId, LabelRepresentation labelRepresentation);

    public void saveLabel(Label label);

    public void removeLabel(Label label);

    public void addLabelToFile(String fileUuid, LabelIdRepresentation representation, UserDetails currentUser);

    public void removeLabelFromFile(String fileUuid, LabelIdRepresentation representation, UserDetails currentUser);

    public void addLabelToFolder(Long folderId, LabelIdRepresentation representation, UserDetails currentUser);

    public void removeLabelFromFolder(Long folderId, LabelIdRepresentation representation, UserDetails currentUser);
}
