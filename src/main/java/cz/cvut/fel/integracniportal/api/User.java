package cz.cvut.fel.integracniportal.api;

/**
 * @author Radek Jezdik
 */
public class User {

    private String id;

    private String username;

    private String organizationalUnitId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setOrganizationalUnitId(String organizationalUnitId) {
        this.organizationalUnitId = organizationalUnitId;
    }

    public String getOrganizationalUnitId() {
        return organizationalUnitId;
    }
}
