package cz.cvut.fel.integracniportal.cesnet;

import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public interface CesnetService {
    public List<String> getFileList();

    public InputStream getFile(String filename) throws SftpException, IOException;

    public void uploadFile(InputStream fileStream, String filename) throws SftpException;
}
