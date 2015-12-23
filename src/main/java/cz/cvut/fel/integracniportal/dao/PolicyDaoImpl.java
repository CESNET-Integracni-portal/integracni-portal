package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Policy;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static cz.cvut.fel.integracniportal.model.QPolicy.policy;

/**
 * @author Eldar Iosip
 */
@Repository
public class PolicyDaoImpl extends GenericHibernateDao<Policy> implements PolicyDao {

    public PolicyDaoImpl() {
        super(Policy.class);
    }

    @Override
    public List<Policy> findByActiveAfter(Date date) {
        return from(policy)
                .where(policy.isProcessed.isFalse().and(policy.activeAfter.after(date)))
                .list(policy);
    }
}
