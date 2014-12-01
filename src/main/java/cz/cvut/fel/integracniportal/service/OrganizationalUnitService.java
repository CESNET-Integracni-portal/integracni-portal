package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.OrganizationalUnit;

import java.util.List;

public interface OrganizationalUnitService {

    public List<OrganizationalUnit> getAllOrganizationalUnits();

    public OrganizationalUnit getOrganizationalUnitById(Long id);
}
