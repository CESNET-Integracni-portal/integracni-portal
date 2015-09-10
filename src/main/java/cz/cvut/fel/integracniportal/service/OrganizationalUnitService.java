package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.OrganizationalUnit;
import cz.cvut.fel.integracniportal.representation.OrganizationalUnitRepresentation;

import java.util.List;

public interface OrganizationalUnitService {

    public List<OrganizationalUnit> getAllOrganizationalUnits();

    public OrganizationalUnit getOrganizationalUnitById(String id);

    public OrganizationalUnit getOrganizationUnitByName(String name);

    public OrganizationalUnit createUnit(OrganizationalUnitRepresentation unit);

    public OrganizationalUnit renameUnit(String unitId, String newName);

    public void addAdmin(String unitId, Long userId);

    public void removeAdmin(String unitId, Long userId);

    public void updateUnitQuota(String unitId, Long size);

}
