package cz.cvut.fel.integracniportal.service;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
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
