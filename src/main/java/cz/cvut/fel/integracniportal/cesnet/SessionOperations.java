package cz.cvut.fel.integracniportal.cesnet;

/**
 * Defines the basic set of operations in a Jsch session.
 *
 * @author sso
 */
public interface SessionOperations {

    <T> T execute(SessionCallback<T> sessionCallback) throws Exception;
}
