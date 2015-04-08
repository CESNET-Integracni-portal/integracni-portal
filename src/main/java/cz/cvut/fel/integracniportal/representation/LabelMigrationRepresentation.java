package cz.cvut.fel.integracniportal.representation;

import cz.cvut.fel.integracniportal.model.Label;

/**
 * Created by Vavat on 7. 4. 2015.
 */

public class LabelMigrationRepresentation {

    private Long labelId;

    public LabelMigrationRepresentation(Label label) {
        this.labelId = label.getLabelId();
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }
}
