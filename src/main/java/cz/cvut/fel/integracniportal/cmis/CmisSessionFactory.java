package cz.cvut.fel.integracniportal.cmis;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;

import java.util.Map;

/**
 * Custom SessionFactory for Content Management Interoperability Services
 */
public class CmisSessionFactory {

    private SessionFactory sessionFactory = SessionFactoryImpl.newInstance();

    protected Session createSession(Map<String, String> parameters) {
        Session session = sessionFactory.getRepositories(parameters).get(0).createSession();
        return session;
    }
}
