package cz.cvut.fel.integracniportal.dao;

import com.mysema.query.jpa.hibernate.HibernateDeleteClause;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.mysema.query.jpa.hibernate.HibernateSubQuery;
import com.mysema.query.jpa.hibernate.HibernateUpdateClause;
import com.mysema.query.types.EntityPath;
import cz.cvut.fel.integracniportal.model.AbstractEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * @author Radek Jezdik
 */
public class GenericHibernateDao<ENTITY extends AbstractEntity> {

    private Class entityClass;

    @Autowired
    private SessionFactory sessionFactory;

    private HibernateTemplate hibernateTemplate;

    public GenericHibernateDao(Class entityClass) {
        this.entityClass = entityClass;
    }

    @PostConstruct
    public void init() {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @SuppressWarnings("unchecked")
    public ENTITY get(Serializable id) {
        return (ENTITY) hibernateTemplate.get(entityClass, id);
    }

    @SuppressWarnings("unchecked")
    public ENTITY load(Serializable id) {
        return (ENTITY) hibernateTemplate.load(entityClass, id);
    }

    public void save(ENTITY entity) {
        hibernateTemplate.save(entity);
    }

    public void update(ENTITY entity) {
        hibernateTemplate.update(entity);
    }

    public void delete(ENTITY entity) {
        hibernateTemplate.delete(entity);
    }

    public HibernateQuery query() {
        return new HibernateQuery(sessionFactory.getCurrentSession());
    }

    public HibernateQuery from(EntityPath<?> from) {
        return query().from(from);
    }

    public HibernateUpdateClause update(EntityPath<?> path) {
        return new HibernateUpdateClause(sessionFactory.getCurrentSession(), path);
    }

    public HibernateDeleteClause delete(EntityPath<?> path) {
        return new HibernateDeleteClause(sessionFactory.getCurrentSession(), path);
    }

    public HibernateSubQuery subQuery() {
        return new HibernateSubQuery();
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

}
