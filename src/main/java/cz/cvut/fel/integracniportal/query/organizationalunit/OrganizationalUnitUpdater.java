package cz.cvut.fel.integracniportal.query.organizationalunit;

import cz.cvut.fel.integracniportal.dao.OrganizationalUnitDao;
import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.domain.organizationalunit.events.*;
import cz.cvut.fel.integracniportal.model.OrganizationalUnit;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@Component
public class OrganizationalUnitUpdater {

    @Autowired
    private OrganizationalUnitDao unitDao;

    @Autowired
    private UserDetailsDao userDao;

    @EventHandler
    public void createUnit(OrganizationalUnitCreatedEvent event) {
        OrganizationalUnit unit = new OrganizationalUnit();
        unit.setId(event.getId().getId());
        unit.setName(event.getName());
        unit.setSize(event.getQuotaSize());

        unitDao.save(unit);
    }

    @EventHandler
    public void renameUnit(OrganizationalUnitRenamedEvent event) {
        OrganizationalUnit unit = unitDao.load(event.getId().getId());
        unit.setName(event.getNewName());
    }

    @EventHandler
    public void deleteUnit(OrganizationalUnitDeletedEvent event) {
        OrganizationalUnit unit = unitDao.load(event.getId().getId());
        unitDao.delete(unit);
    }

    @EventHandler
    public void changeQuota(OrganizationalUnitQuotaChangedEvent event) {
        OrganizationalUnit unit = unitDao.load(event.getId().getId());
        unit.setSize(event.getNewQuota());
    }

    @EventHandler
    public void addAdmin(OrganizationalUnitAdminAssignedEvent event) {
        UserDetails user = userDao.getReference(event.getAssignedAdmin().getId());
        OrganizationalUnit unit = unitDao.load(event.getId().getId());
        Set<UserDetails> admins = unit.getAdmins();
        if (admins == null) {
            admins = new HashSet<UserDetails>();
        }
        admins.add(user);
    }

    @EventHandler
    public void removeAdmin(OrganizationalUnitAdminUnassignedEvent event) {
        UserDetails user = userDao.getReference(event.getUnassignedAdmin().getId());
        OrganizationalUnit unit = unitDao.load(event.getId().getId());
        Set<UserDetails> admins = unit.getAdmins();
        if (admins != null) {
            admins.remove(user);
        }
    }

}
