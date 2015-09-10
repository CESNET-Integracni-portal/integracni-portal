package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.OrganizationalUnit;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vavat on 28. 3. 2015.
 */
public interface OrganizationalUnitDao {

    public OrganizationalUnit load(Serializable id);

    public List<OrganizationalUnit> getAllUnits();

    public OrganizationalUnit getOrgUnitById(String id);

    public OrganizationalUnit getOrgUnitByName(String name);

    public void save(OrganizationalUnit organizationalUnit);

    public void delete(OrganizationalUnit organizationalUnit);

    public boolean unitExists(String name);
}
