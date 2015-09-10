package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.OrganizationalUnit;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cz.cvut.fel.integracniportal.model.QOrganizationalUnit.organizationalUnit;

@Repository
public class OrganizationalUnitDaoImpl extends GenericHibernateDao<OrganizationalUnit> implements OrganizationalUnitDao {

    public OrganizationalUnitDaoImpl() {
        super(OrganizationalUnit.class);
    }

    @Override
    public List<OrganizationalUnit> getAllUnits() {
        return from(organizationalUnit)
                .list(organizationalUnit);
    }

    @Override
    public OrganizationalUnit getOrgUnitById(String id) {
        return get(id);
    }

    @Override
    public OrganizationalUnit getOrgUnitByName(String name) {
        return from(organizationalUnit)
                .where(organizationalUnit.name.eq(name))
                .uniqueResult(organizationalUnit);
    }

    @Override
    public boolean unitExists(String name) {
        return from(organizationalUnit)
                .where(organizationalUnit.name.eq(name))
                .exists();
    }

}
