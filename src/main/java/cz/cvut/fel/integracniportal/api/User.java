package cz.cvut.fel.integracniportal.api;

/**
 * @author Radek Jezdik
 */
public class User {

    private Long id;

    private String username;

    private Long organizationalUnitId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setOrganizationalUnitId(Long organizationalUnitId) {
        this.organizationalUnitId = organizationalUnitId;
    }

    public Long getOrganizationalUnitId() {
        return organizationalUnitId;
    }
}
