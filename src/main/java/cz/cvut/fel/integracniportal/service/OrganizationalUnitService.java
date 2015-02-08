package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.OrganizationalUnit;

import java.util.List;
import java.util.Set;

public interface OrganizationalUnitService {

    public List<OrganizationalUnit> getAllOrganizationalUnits();

    public OrganizationalUnit getOrganizationalUnitById(Long id);

    public void setAdmins(OrganizationalUnit unit, Set<String> admins);

}
