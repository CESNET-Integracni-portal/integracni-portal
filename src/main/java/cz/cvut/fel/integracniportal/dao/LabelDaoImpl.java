package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Label;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cz.cvut.fel.integracniportal.model.QLabel.label;

/**
 *
 */
@Repository
public class LabelDaoImpl extends GenericHibernateDao<Label> implements LabelDao {

    public LabelDaoImpl() {
        super(Label.class);
    }

    @Override
    public List<Label> getAllLabels() {
        return from(label)
                .list(label);
    }

    @Override
    public List<Label> getUserLabels(Long ownerId) {
        return from(label)
                .where(label.owner.userId.eq(ownerId))
                .list(label);
    }

    @Override
    public boolean labelExists(Long ownerId, String name, String color) {
        return from(label)
                .where(label.owner.userId.eq(ownerId))
                .where(label.name.eq(name))
                .where(label.color.eq(color))
                .exists();
    }

}
