package cz.cvut.fel.integracniportal.cmis;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom SessionFactory for Content Management Interoperability Services
 */
public class CmisSessionFactory {

    private SessionFactory sessionFactory = SessionFactoryImpl.newInstance();

    protected String adminUsername;
    protected String adminPassword;

    protected Map<String, String> defaultParameters;

    protected Session createSession(Map<String, String> parameters) {
        return sessionFactory.getRepositories(parameters).get(0).createSession();
    }

    public Session createSessionForUser(String user, String password) {
        Map<String, String> parameters = new HashMap<String, String>(defaultParameters);
        parameters.put(SessionParameter.USER, user);
        parameters.put(SessionParameter.PASSWORD, password);
        return createSession(parameters);
    }

    public Session createAdminSession() {
        Map<String, String> parameters = new HashMap<String, String>(defaultParameters);
        parameters.put(SessionParameter.USER, adminUsername);
        parameters.put(SessionParameter.PASSWORD, adminPassword);
        return createSession(parameters);
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public Map<String, String> getDefaultParameters() {
        return defaultParameters;
    }

    public void setDefaultParameters(Map<String, String> defaultParameters) {
        this.defaultParameters = defaultParameters;
    }
}
