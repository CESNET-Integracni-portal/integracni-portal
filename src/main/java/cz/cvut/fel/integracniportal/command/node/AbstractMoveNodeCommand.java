package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An abstract class for Move commands of nodes (files and folders).
 *
 * @author Radek Jezdik
 */
@Getter
@AllArgsConstructor
abstract public class AbstractMoveNodeCommand<T> extends UserAwareCommand {

    private final T id;

    private final FolderId newParent;

}
