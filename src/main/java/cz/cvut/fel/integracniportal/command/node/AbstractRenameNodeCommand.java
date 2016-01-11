package cz.cvut.fel.integracniportal.command.node;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An abstract class for Rename commands of nodes (files and folders).
 *
 * @author Radek Jezdik
 */
@Getter
@AllArgsConstructor
abstract public class AbstractRenameNodeCommand<T> extends UserAwareCommand {

    private final T id;

    private final String newName;

}
