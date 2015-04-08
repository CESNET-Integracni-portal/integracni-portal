package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.OrganizationalUnit;
import cz.cvut.fel.integracniportal.model.UserDetails;

import java.util.Set;

/**
 * Representation class for organizational units.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationalUnitRepresentation {

    private Long id;

    private String name;

    private Long size;

    private Set<String> admins;

    private Set<String> members;

    public OrganizationalUnitRepresentation() {
    }

    public OrganizationalUnitRepresentation(OrganizationalUnit organizationalUnit) {
        this.id = organizationalUnit.getId();
        this.name = organizationalUnit.getName();
        this.size = organizationalUnit.getSize();
        if(organizationalUnit.getAdmins() != null){
            for(UserDetails userDetails: organizationalUnit.getAdmins()){
                admins.add(userDetails.getUsername());
            }
        }
        if(organizationalUnit.getMembers() != null){
            for(UserDetails userDetails: organizationalUnit.getMembers()){
                members.add(userDetails.getUsername());
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

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Set<String> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<String> admins) {
        this.admins = admins;
    }

    public Set<String> getMembers() {
        return members;
    }

    public void setMembers(Set<String> members) {
        this.members = members;
    }

}
