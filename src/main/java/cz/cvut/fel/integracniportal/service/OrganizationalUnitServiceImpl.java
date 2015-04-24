package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.OrganizationalUnitDao;
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.OrganizationalUnit;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.OrganizationalUnitRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OrganizationalUnitServiceImpl implements OrganizationalUnitService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private OrganizationalUnitDao organizationalUnitDao;

    @Override
    public List<OrganizationalUnit> getAllOrganizationalUnits() {
        return organizationalUnitDao.getAllUnits();
    }

    @Override
    public OrganizationalUnit getOrganizationalUnitById(Long id) {
        OrganizationalUnit unit = organizationalUnitDao.getOrgUnitById(id);
        if(unit == null){
            throw new NotFoundException("organizationUnit.notFound.id", id);
        }
        return unit;
    }

    @Override
    public OrganizationalUnit getOrganizationUnitByName(String name) {
        return organizationalUnitDao.getOrgUnitByName(name);
    }

    @Override
    public void setAdmins(Long id, OrganizationalUnitRepresentation unit) {
        OrganizationalUnit res = getOrganizationalUnitById(id);
        updateUnitFromRepresentation(res,unit);
        organizationalUnitDao.save(res);
    }

    @Override
    public void setMembers(Long id, OrganizationalUnitRepresentation unit) {
        OrganizationalUnit res = getOrganizationalUnitById(id);
        updateUnitFromRepresentation(res,unit);
        organizationalUnitDao.save(res);
    }

    @Override
    public OrganizationalUnit createUnit(OrganizationalUnitRepresentation unit) {
        if(getOrganizationUnitByName(unit.getName()) != null){
            throw new AlreadyExistsException("organizationalUnit.alreadyExists");
        }
        OrganizationalUnit result = new OrganizationalUnit();
        updateUnitFromRepresentation(result, unit);
        organizationalUnitDao.save(result);
        return result;
    }

    @Override
    public OrganizationalUnit updateUnit(Long unitId, OrganizationalUnitRepresentation unit) {
        OrganizationalUnit result = getOrganizationalUnitById(unitId);
        updateUnitFromRepresentation(result, unit);
        result.setId(unitId);
        organizationalUnitDao.save(result);
        return result;
    }

    @Override
    public OrganizationalUnit addAdmin(Long unitId, UserDetails admin) {
        OrganizationalUnit unit = getOrganizationalUnitById(unitId);
        unit.getAdmins().add(admin);
        organizationalUnitDao.save(unit);
        return unit;
    }

    @Override
    public OrganizationalUnit removeAdmin(Long unitId, UserDetails admin) {
        OrganizationalUnit unit = getOrganizationalUnitById(unitId);
        unit.getAdmins().remove(admin);
        organizationalUnitDao.save(unit);
        return unit;
    }

    @Override
    public void saveUnit(OrganizationalUnit unit) {
        organizationalUnitDao.save(unit);
    }

    @Override
    public void removeUnit(OrganizationalUnit unit) {
        organizationalUnitDao.delete(unit);
    }

    private void updateUnitFromRepresentation(OrganizationalUnit unit, OrganizationalUnitRepresentation representation){
        if(representation.getId() != null){
            unit.setId(representation.getId());
        }
        if(representation.getName() != null){
            unit.setName(representation.getName());
        }
        if(representation.getSize() != null){
            unit.setSize(representation.getSize());
        }
        if(representation.getAdmins() != null){
            Set<UserDetails> tmp = new HashSet<UserDetails>();
            for (String name: representation.getAdmins()){
                tmp.add(userDetailsService.getUserByUsername(name));
            }
            unit.setAdmins(tmp);
        }
        if(representation.getMembers() != null){
            Set<UserDetails> tmp = new HashSet<UserDetails>();
            for (String name: representation.getMembers()){
                tmp.add(userDetailsService.getUserByUsername(name));
            }
            unit.setMembers(tmp);
        }
    }
}
