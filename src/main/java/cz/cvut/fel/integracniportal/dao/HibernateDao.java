package cz.cvut.fel.integracniportal.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Parent class for all Data Access Object classes. Provides access to Hibernate template and session.
 */
@Transactional
public abstract class HibernateDao {
    HibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    /**
     * @return Hibernate template.
     */
    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    /**
     * @return Current Hibernate session.
     */
    protected Session getSession() {
        return hibernateTemplate.getSessionFactory().getCurrentSession();
    }

    /**
     * Creates criteria object for a database search.
     * @param clazz    Entity class (which table should be searched).
     * @param alias    Alias, under which the table can be referenced in the search criteria.
     * @return Criteria object for the specified Entity class.
     */
    protected Criteria getCriteria(Class clazz, String alias) {
        return getSession().createCriteria(clazz, alias);
    }
}
