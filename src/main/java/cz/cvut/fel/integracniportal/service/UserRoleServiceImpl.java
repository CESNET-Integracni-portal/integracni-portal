package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.UserRoleDao;
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.exceptions.PermissionNotAssignableToRoleException;
import cz.cvut.fel.integracniportal.model.Permission;
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
    public UserRole getRoleById(long id) {
        UserRole userRole = userRoleDao.get(id);
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
    @Transactional(rollbackFor = PermissionNotAssignableToRoleException.class)
    public void createRole(UserRole role) {
        // Check whether a different role with the same name exists
        UserRole existingRole = userRoleDao.getRoleByName(role.getName());
        if (existingRole != null) {
            throw new AlreadyExistsException("role.alreadyExists", role.getName());
        }
        saveRole(role);
    }

    @Override
    @Transactional(rollbackFor = PermissionNotAssignableToRoleException.class)
    public void saveRole(UserRole role) {
        for (Permission permission : role.getPermissions()) {
            if (!permission.isRoleAssignable()) {
                throw new PermissionNotAssignableToRoleException("permission.notAssignableToRole", permission.toString());
            }
        }
        userRoleDao.save(role);
    }

    @Override
    public void deleteRole(UserRole role) {
        userRoleDao.delete(role);
    }
}
