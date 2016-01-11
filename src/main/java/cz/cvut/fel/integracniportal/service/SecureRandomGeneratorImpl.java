package cz.cvut.fel.integracniportal.service;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Implementation of the secure random generator. Uses BigInteger with {@link SecureRandom} to
 * create secure random strings.
 *
 * @author Radek Jezdik
 */
@Component
public class SecureRandomGeneratorImpl implements SecureRandomGenerator {

    private SecureRandom random = new SecureRandom();

    @Override
    public String generate() {
        return new BigInteger(130, random).toString(32);
    }

}
