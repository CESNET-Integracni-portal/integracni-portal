package cz.cvut.fel.integracniportal.form;

/**
 * Form for registering a new user. Used in {@link cz.cvut.fel.integracniportal.controller.LoginController}.
 */
public class RegisterForm {

    private String username;
    private String password;
    private String passwordRepeat;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }
    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }
}
