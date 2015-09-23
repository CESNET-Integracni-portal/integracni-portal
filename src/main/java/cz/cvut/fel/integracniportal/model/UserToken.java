package cz.cvut.fel.integracniportal.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for user roles used in Spring authorisation.
 */
@Entity
@Table(name = "user_email_verification")
public class UserToken extends AbstractEntity<String> {

    @Id
    @Column(name = "user_id")
    private String userId;

    private String token;

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public void setId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
