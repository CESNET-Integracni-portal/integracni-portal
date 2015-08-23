package cz.cvut.fel.integracniportal.domain.node.valueobjects;

import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value(staticConstructor = "of")
public class FolderId implements NodeId {

    private final String id;

    public FolderId(String id) {
        this.id = id;
    }

    public static FolderId of(String id) {
        return new FolderId(id);
    }

}
