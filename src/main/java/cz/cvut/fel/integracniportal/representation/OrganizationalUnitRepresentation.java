package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.OrganizationalUnit;

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

    public OrganizationalUnitRepresentation() {}
    public OrganizationalUnitRepresentation(OrganizationalUnit organizationalUnit) {
        this.id = organizationalUnit.getUnitId();
        this.name = organizationalUnit.getName();
        this.size = organizationalUnit.getSize();
        this.admins = organizationalUnit.getAdmins();
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
}
