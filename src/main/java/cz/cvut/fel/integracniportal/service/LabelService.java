package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.Label;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.LabelRepresentation;

import java.util.List;

/**
 * Service for Label credentials.
 */
public interface LabelService {

    public List<Label> getUserLabels(UserDetails owner);

    public Label getLabelById(String labelId);

    public Label createLabel(LabelRepresentation labelRepresentation, UserDetails owner);

    public Label updateLabel(String labelId, LabelRepresentation labelRepresentation);

    public void deleteLabel(String label);

    public void addLabelToFile(String fileId, String labelId);

    public void removeLabelFromFile(String fileId, String labelId);

    public void addLabelToFolder(String folderId, String labelId);

    public void removeLabelFromFolder(String folderId, String labelId);
}
