package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.Group;
import cz.cvut.fel.integracniportal.model.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Radek Jezdik
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupRepresentation {

    private Long id;

    private String name;

    private List<UserDetailsRepresentation> members;

    public GroupRepresentation() {
    }

    public GroupRepresentation(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.members = new ArrayList<UserDetailsRepresentation>();

        if (group.getMembers() != null) {
            for (UserDetails user : group.getMembers()) {
                this.members.add(new UserDetailsRepresentation(user));
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserDetailsRepresentation> getMembers() {
        return members;
    }

    public void setMembers(List<UserDetailsRepresentation> members) {
        this.members = members;
    }
}
