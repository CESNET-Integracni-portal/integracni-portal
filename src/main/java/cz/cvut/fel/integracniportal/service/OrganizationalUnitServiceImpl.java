package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.command.organizationalunit.*;
import cz.cvut.fel.integracniportal.dao.OrganizationalUnitDao;
import cz.cvut.fel.integracniportal.domain.organizationalunit.valueobjects.OrganizationalUnitId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.OrganizationalUnit;
import cz.cvut.fel.integracniportal.representation.OrganizationalUnitRepresentation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizationalUnitServiceImpl implements OrganizationalUnitService {

    @Autowired
    private OrganizationalUnitDao organizationalUnitDao;

    @Autowired
    private CommandGateway commandGateway;

    @Transactional(readOnly = true)
    @Override
    public List<OrganizationalUnit> getAllOrganizationalUnits() {
        return organizationalUnitDao.getAllUnits();
    }

    @Transactional(readOnly = true)
    @Override
    public OrganizationalUnit getOrganizationalUnitById(String id) {
        OrganizationalUnit unit = organizationalUnitDao.getOrgUnitById(id);
        if(unit == null){
            throw new NotFoundException("organizationUnit.notFound.id", id);
        }
        return unit;
    }

    @Transactional(readOnly = true)
    @Override
    public OrganizationalUnit getOrganizationUnitByName(String name) {
        return organizationalUnitDao.getOrgUnitByName(name);
    }

    @Override
    public OrganizationalUnit createUnit(OrganizationalUnitRepresentation unit) {
        String id = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new CreateOrganizationalUnitCommand(
                OrganizationalUnitId.of(id),
                unit.getName(),
                unit.getSize()
        ));

        return getOrganizationalUnitById(id);
    }

    @Override
    public OrganizationalUnit renameUnit(String unitId, String newName) {
        commandGateway.sendAndWait(new RenameOrganizationalUnitCommand(
                OrganizationalUnitId.of(unitId),
                newName
        ));

        return getOrganizationalUnitById(unitId);
    }

    @Override
    public void updateUnitQuota(String unitId, Long size) {
        commandGateway.sendAndWait(new ChangeOrganizationalUnitQuotaCommand(
                OrganizationalUnitId.of(unitId),
                size
        ));
    }

    @Override
    public void addAdmin(String unitId, String userId) {
        commandGateway.sendAndWait(new AssignOrganizationalUnitAdminCommand(
                OrganizationalUnitId.of(unitId),
                UserId.of(userId)
        ));
    }

    @Override
    public void removeAdmin(String unitId, String userId) {
        commandGateway.sendAndWait(new UnassignOrganizationalUnitAdminCommand(
                OrganizationalUnitId.of(unitId),
                UserId.of(userId)
        ));
    }

}
