package cz.cvut.fel.integracniportal.representation;

/**
 * @author Radek Jezdik
 */
public class UserPasswordRepresentation {

    private String password;

    private String oldPassword;

    public String getPassword() {
        return password;
    }

    public void setNewPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
