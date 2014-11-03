package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.UserRoleDao;
import cz.cvut.fel.integracniportal.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return userRoleDao.getRoleById(id);
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
    public void saveRole(UserRole role) {
        userRoleDao.saveRole(role);
    }

    @Override
    public void deleteRole(UserRole role) {
        userRoleDao.deleteRole(role);
    }
}
