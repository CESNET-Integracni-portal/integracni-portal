package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Label;

import java.util.List;

/**
 * Created by Vavat on 7. 3. 2015.
 */
public interface LabelDao {

    public List<Label> getAllLabels();

    public List<Label> getUserLabels(long id);

    public Label getLabelByName(String name);

    public Label getLabelById(long id);

    public void save(Label label);

    public void delete(Label label);
}
