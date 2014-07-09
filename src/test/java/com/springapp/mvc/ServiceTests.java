package com.springapp.mvc;

import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@WebAppConfiguration
@ContextConfiguration("file:src/main/webapp/WEB-INF/applicationContext.xml")
public class ServiceTests {

    @Autowired
    UserDetailsService userService;

    @Test
    public void testUserService() throws Exception {
        // Create a test user
        String username = "test-user-12345";
        String password = "password";
        UserDetails user = new UserDetails();
        user.setUsername(username);
        user.setPassword(password);

        // Save the user
        userService.saveUser(user);
        // Find the user by his id
        UserDetails userConfirmation = userService.getUserById(user.getUserId());
        assertNotNull("Unable to find saved user in the database by his id.", userConfirmation);
        assertEquals("User in the database is different than the one that was saved.", user, userConfirmation);
        // Find the user by his username
        userConfirmation = userService.getUserByUsername(username);
        assertNotNull("Unable to find saved user in the database by his username.", userConfirmation);
        assertEquals("User in the database is different than the one that was saved.", user, userConfirmation);

        // Update the user
        String newUsername = "test-user-54321";
        user.setUsername(newUsername);
        userService.saveUser(user);
        // Find the user by his new username
        userConfirmation = userService.getUserByUsername(newUsername);
        assertNotNull("Unable to find the updated user in the database.", userConfirmation);
        assertEquals("User in the database is different than the one that was updated.", user, userConfirmation);
        // Confirm that the user is no longer present in the database under his old name
        userConfirmation = userService.getUserByUsername(username);
        assertNull("User is still present in the database under his old name.", userConfirmation);

        // Remove the user
        userService.removeUser(user);
        // Confirm that the user is no longer present in the database
        userConfirmation = userService.getUserByUsername(newUsername);
        assertNull("User is still present in the database after his deletion.", userConfirmation);
    }
}
