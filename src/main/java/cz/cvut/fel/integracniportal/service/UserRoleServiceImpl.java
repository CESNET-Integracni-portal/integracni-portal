package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.UserRoleDao;
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the {@link cz.cvut.fel.integracniportal.service.UserRoleService}.
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    UserRoleDao userRoleDao;


    @Override
    public UserRole getRoleById(long id) throws NotFoundException {
        UserRole userRole = userRoleDao.getRoleById(id);
        if (userRole == null) {
            throw new NotFoundException("role.notFound", id);
        }
        return userRole;
    }

    @Override
    public UserRole getRoleByName(String name) {
        return userRoleDao.getRoleByName(name);
    }

    @Override
    public List<UserRole> getAllRoles() {
        return userRoleDao.getAllRoles();
    }

    @Override
    public void createRole(UserRole role) throws AlreadyExistsException {
        // Check whether a different role with the same name exists
        UserRole existingRole = userRoleDao.getRoleByName(role.getName());
        if (existingRole != null) {
            throw new AlreadyExistsException("role.alreadyExists", role.getName());
        }
        saveRole(role);
    }

    @Override
    @Transactional
    public void saveRole(UserRole role) {
        userRoleDao.saveRole(role);
    }

    @Override
    public void deleteRole(UserRole role) {
        userRoleDao.deleteRole(role);
    }
}
