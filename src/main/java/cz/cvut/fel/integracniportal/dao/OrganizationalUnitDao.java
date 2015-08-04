package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.OrganizationalUnit;

import java.util.List;

/**
 * Created by Vavat on 28. 3. 2015.
 */
public interface OrganizationalUnitDao {

    public List<OrganizationalUnit> getAllUnits();

    public OrganizationalUnit getOrgUnitById(Long id);

    public OrganizationalUnit getOrgUnitByName(String name);

    public void save(OrganizationalUnit organizationalUnit);

    public void delete(OrganizationalUnit organizationalUnit);
}
