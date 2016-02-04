package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Radek Jezdik
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewPasswordRepresentation {

    private String password;

    private String password2;

    private String oldPassword;


    public String getPassword() {
        return password;
    }

    public void setNewPassword(String password) {
        this.password = password;
    }

    public String getPassword2(){
        return(password2);
    }

    public void setPassword2(String password2){
        this.password2 = password2;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
