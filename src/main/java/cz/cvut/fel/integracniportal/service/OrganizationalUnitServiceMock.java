package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.OrganizationalUnit;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class OrganizationalUnitServiceMock implements OrganizationalUnitService {

    @Autowired
    private UserDetailsService userDetailsService;

    private Map<Long, OrganizationalUnit> organizationalUnitMocks;

    public Map<Long, OrganizationalUnit> getOrganizationalUnitMocks() {
        return organizationalUnitMocks;
    }

    public void setOrganizationalUnitMocks(Map<Long, OrganizationalUnit> organizationalUnitMocks) {
        this.organizationalUnitMocks = organizationalUnitMocks;
    }

    @Override
    public List<OrganizationalUnit> getAllOrganizationalUnits() {
        List<OrganizationalUnit> units = new ArrayList<OrganizationalUnit>();
        for (OrganizationalUnit unit: organizationalUnitMocks.values()) {
            addExternistsToUnit(unit);
            units.add(unit);
        }
        return units;
    }

    @Override
    public OrganizationalUnit getOrganizationalUnitById(Long id) {
        OrganizationalUnit unit = organizationalUnitMocks.get(id);
        if (unit != null) {
            addExternistsToUnit(unit);
        }
        return unit;
    }

    private void addExternistsToUnit(OrganizationalUnit organizationalUnit) {
        if (organizationalUnit.getAdmins() == null) {
            organizationalUnit.setAdmins(new HashSet<String>());
        }
        List<UserDetails> adminsInUnit = userDetailsService.getAdminsForOrganizationalUnit(organizationalUnit.getUnitId());
        for (UserDetails admin: adminsInUnit) {
            organizationalUnit.getAdmins().add(admin.getUsername());
        }

        if (organizationalUnit.getMembers() == null) {
            organizationalUnit.setMembers(new HashSet<String>());
        }
        List<UserDetails> externistsInUnit = userDetailsService.getAllUsersInOrganizationalUnit(organizationalUnit.getUnitId());
        for (UserDetails externist: externistsInUnit) {
            organizationalUnit.getMembers().add(externist.getUsername());
        }

    }
}
