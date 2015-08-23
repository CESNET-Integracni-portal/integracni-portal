package cz.cvut.fel.integracniportal.command.node;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Radek Jezdik
 */
@Getter
@AllArgsConstructor
abstract public class RenameNodeCommand<T> {

    private final T id;

    private final String newName;

}
