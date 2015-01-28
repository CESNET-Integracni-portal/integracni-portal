package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.OrganizationalUnit;
import cz.cvut.fel.integracniportal.model.Permission;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class OrganizationalUnitServiceMock implements OrganizationalUnitService {

    @Autowired
    private UserDetailsService userDetailsService;

    private Map<Long, OrganizationalUnit> organizationalUnitMocks;

    public void setOrganizationalUnitMocks(Map<Long, OrganizationalUnit> organizationalUnitMocks) {
        this.organizationalUnitMocks = organizationalUnitMocks;
    }

    @Override
    public List<OrganizationalUnit> getAllOrganizationalUnits() {
        List<OrganizationalUnit> units = new ArrayList<OrganizationalUnit>();
        for (OrganizationalUnit unit : organizationalUnitMocks.values()) {
            OrganizationalUnit copiedUnit = createCopy(unit);
            addExternistsToUnit(copiedUnit);
            units.add(copiedUnit);
        }
        return units;
    }

    @Override
    public OrganizationalUnit getOrganizationalUnitById(Long id) {
        OrganizationalUnit unit = organizationalUnitMocks.get(id);
        if (unit == null) {
            return null;
        }
        OrganizationalUnit copiedUnit = createCopy(unit);
        addExternistsToUnit(copiedUnit);
        return copiedUnit;
    }

    @Override
    public void setAdmins(OrganizationalUnit unit, Set<String> admins) {
        // Remove admins who are not in the new list
        for (String oldAdmin : unit.getAdmins()) {
            if (!admins.contains(oldAdmin)) {
                UserDetails oldAdminDetails = userDetailsService.getUserByUsername(oldAdmin);
                oldAdminDetails.getPermissions().remove(Permission.EDIT_ORGANIZATIONAL_UNITS);
                userDetailsService.saveUser(oldAdminDetails);
            }
        }
        // Add new admins
        for (String newAdmin : admins) {
            if (!unit.getAdmins().contains(newAdmin)) {
                UserDetails newAdminDetails = userDetailsService.getUserByUsername(newAdmin);
                newAdminDetails.getPermissions().add(Permission.EDIT_ORGANIZATIONAL_UNITS);
                userDetailsService.saveUser(newAdminDetails);
            }
        }
    }

    private void addExternistsToUnit(OrganizationalUnit organizationalUnit) {
        if (organizationalUnit.getAdmins() == null) {
            organizationalUnit.setAdmins(new HashSet<String>());
        }
        if (organizationalUnit.getMembers() == null) {
            organizationalUnit.setMembers(new HashSet<String>());
        }

        List<UserDetails> externistsInUnit = userDetailsService.getAllUsersInOrganizationalUnit(organizationalUnit.getUnitId());
        for (UserDetails externist : externistsInUnit) {
            organizationalUnit.getMembers().add(externist.getUsername());
            if (externist.getPermissions().contains(Permission.EDIT_ORGANIZATIONAL_UNITS)) {
                organizationalUnit.getAdmins().add(externist.getUsername());
            }
        }

    }

    private OrganizationalUnit createCopy(OrganizationalUnit unit) {
        OrganizationalUnit newUnit = new OrganizationalUnit();
        newUnit.setUnitId(unit.getUnitId());
        newUnit.setName(unit.getName());
        newUnit.setSize(unit.getSize());
        newUnit.setAdmins(new HashSet<String>(unit.getAdmins()));
        newUnit.setMembers(new HashSet<String>(unit.getMembers()));
        return newUnit;
    }
}
