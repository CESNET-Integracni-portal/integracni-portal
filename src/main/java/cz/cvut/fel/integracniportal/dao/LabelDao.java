package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Label;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public interface LabelDao {

    public Label load(Serializable id);

    public List<Label> getAllLabels();

    public List<Label> getUserLabels(String id);

    public Label get(Serializable id);

    public void save(Label label);

    public void delete(Label label);

    public boolean labelExists(String userId, String name, String color);

}
