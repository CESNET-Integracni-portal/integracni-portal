package cz.cvut.fel.integracniportal.cesnet;

/**
 * Callback interface for a session operation.
 *
 * @author sso
 */
public interface SessionCallback<T> {

    String getName();

    T execute() throws Exception;

}
