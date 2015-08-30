package cz.cvut.fel.integracniportal.domain.group.valueobjects;

import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value(staticConstructor = "of")
public class GroupId {

    private final String id;

    public GroupId(String id) {
        this.id = id;
    }

    public static GroupId of(String id) {
        return new GroupId(id);
    }

}
