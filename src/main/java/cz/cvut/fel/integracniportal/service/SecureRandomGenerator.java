package cz.cvut.fel.integracniportal.service;

/**
 * A generator of secure random strings.
 *
 * @author Radek Jezdik
 */
public interface SecureRandomGenerator {

    /**
     * Generates the secure random string.
     * @return random string
     */
    public String generate();

}
