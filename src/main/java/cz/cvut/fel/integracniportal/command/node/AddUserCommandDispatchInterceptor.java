package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.axonframework.commandhandling.CommandDispatchInterceptor;
import org.axonframework.commandhandling.CommandMessage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Radek Jezdik
 */
public class AddUserCommandDispatchInterceptor implements CommandDispatchInterceptor {

    @Autowired
    private UserDetailsService userService;

    @Override
    public CommandMessage<?> handle(CommandMessage<?> commandMessage) {
        if (commandMessage.getPayload() instanceof UserAwareCommand) {
            addUser((UserAwareCommand) commandMessage.getPayload());
        }

        return commandMessage;
    }

    private void addUser(UserAwareCommand command) {
        UserDetails currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("There is no current user");
        }
        command.setSendBy(UserId.of(currentUser.getId()));
    }
}
