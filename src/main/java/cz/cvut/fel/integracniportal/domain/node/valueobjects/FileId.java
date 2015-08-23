package cz.cvut.fel.integracniportal.domain.node.valueobjects;

import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value(staticConstructor = "of")
public class FileId implements NodeId {

    private final String id;

    public FileId(String id) {
        this.id = id;
    }

    public static FileId of(String id) {
        return new FileId(id);
    }

}
