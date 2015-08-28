package cz.cvut.fel.integracniportal.command.node;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Radek Jezdik
 */
@Getter
@AllArgsConstructor
abstract public class AbstractNodeCommand<T> extends UserAwareCommand {

    protected final T id;

    protected final String name;

    protected final FolderId parentFolder;

    protected final UserId owner;

    protected final String space;

}
