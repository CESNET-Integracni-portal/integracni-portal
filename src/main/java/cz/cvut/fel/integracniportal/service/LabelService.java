package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.Label;
import cz.cvut.fel.integracniportal.representation.LabelMigrationRepresentation;
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

    public List<Label> getUserLabels(long userId);

    public Label getLabelById(long labelId);

    public Label getLabelByName(String name);

    public Label createLabel(LabelRepresentation labelRepresentation);

    public Label updateLabel(Long labelId, LabelRepresentation labelRepresentation);

    public void saveLabel(Label label);

    public void removeLabel(Label label);

    public void addLabelToFile(String fileUuid, LabelMigrationRepresentation representation);

    public void removeLabelFromFile(String fileUuid, LabelMigrationRepresentation representation);

    public void addLabelToFolder(Long folderId, LabelMigrationRepresentation representation);

    public void removeLabelFromFolder(Long folderId, LabelMigrationRepresentation representation);
}