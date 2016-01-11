package cz.cvut.fel.integracniportal.api;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eldar Iosip
 */
public interface EncryptableRepository {

    public InputStream encrypt(InputStream file, String salt);

    public InputStream decrypt(InputStream file, String salt);

}
