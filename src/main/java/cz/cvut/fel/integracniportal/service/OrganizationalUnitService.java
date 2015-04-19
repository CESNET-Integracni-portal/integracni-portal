package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.OrganizationalUnit;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.OrganizationalUnitRepresentation;

import java.util.List;
import java.util.Set;

public interface OrganizationalUnitService {

    public List<OrganizationalUnit> getAllOrganizationalUnits();

    public OrganizationalUnit getOrganizationalUnitById(Long id);

    public OrganizationalUnit getOrganizationUnitByName(String name);

    public void setAdmins(Long id, OrganizationalUnitRepresentation unit);

    public void setMembers(Long id, OrganizationalUnitRepresentation unit);

    public OrganizationalUnit createUnit(OrganizationalUnitRepresentation unit);

    public OrganizationalUnit updateUnit(Long unitId, OrganizationalUnitRepresentation unit);

    public OrganizationalUnit addAdmin(Long unitId, UserDetails admin);

    public OrganizationalUnit removeAdmin(Long unitId, UserDetails admin);

    public void saveUnit(OrganizationalUnit unit);

    public void removeUnit(OrganizationalUnit unit);
}
