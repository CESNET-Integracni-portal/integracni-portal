package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Label;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cz.cvut.fel.integracniportal.model.QLabel.label;

/**
 * Created by Vavat on 7. 3. 2015.
 */
@Repository
public class LabelDaoImpl extends GenericHibernateDao<Label> implements LabelDao{

    public LabelDaoImpl() {
        super(Label.class);
    }

    @Override
    public List<Label> getAllLabels() {
        return from(label).list(label);
    }

    @Override
    public List<Label> getUserLabels(long id) {
        return from(label)
                .where(label.owner.userId.eq(id))
                .list(label);
    }

    @Override
    public Label getLabelByName(String name) {
        return from(label)
                .where(label.name.eq(name))
                .uniqueResult(label);
    }

    @Override
    public Label getLabelById(long id) {
        return get(id);
    }
}
