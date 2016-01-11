package cz.cvut.fel.integracniportal.domain.organizationalunit;

import cz.cvut.fel.integracniportal.command.organizationalunit.*;
import cz.cvut.fel.integracniportal.dao.OrganizationalUnitDao;
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Radek Jezdik
 */
@Component
public class OrganizationalUnitCommandHandler {

    @Resource(name = "orgUnitAggregateRepository")
    private Repository<OrganizationalUnit> repository;

    @Autowired
    private OrganizationalUnitDao unitDao;

    @CommandHandler
    public void handle(CreateOrganizationalUnitCommand command) {
        checkUnique(command.getName());

        OrganizationalUnit unit = new OrganizationalUnit(
                command.getId(),
                command.getName(),
                command.getQuotaSize()
        );

        repository.add(unit);
    }

    @CommandHandler
    public void handle(RenameOrganizationalUnitCommand command) {
        OrganizationalUnit unit = repository.load(command.getId());

        checkUnique(command.getNewName());

        unit.rename(command.getNewName());
    }

    @CommandHandler
    public void handle(DeleteOrganizationalUnitCommand command) {
        OrganizationalUnit unit = repository.load(command.getId());
        unit.delete();
    }

    @CommandHandler
    public void handle(ChangeOrganizationalUnitQuotaCommand command) {
        OrganizationalUnit unit = repository.load(command.getId());
        unit.setQuota(command.getQuota());
    }

    @CommandHandler
    public void handle(AssignOrganizationalUnitAdminCommand command) {
        OrganizationalUnit unit = repository.load(command.getId());
        unit.addAdmin(command.getAdmin());
    }

    @CommandHandler
    public void handle(UnassignOrganizationalUnitAdminCommand command) {
        OrganizationalUnit unit = repository.load(command.getId());
        unit.removeAdmin(command.getAdmin());
    }

    private void checkUnique(String name) {
        if (unitDao.unitExists(name)) {
            throw new AlreadyExistsException("unit.alreadyExists");
        }
    }

}
