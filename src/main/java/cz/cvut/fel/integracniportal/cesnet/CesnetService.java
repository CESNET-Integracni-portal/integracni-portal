package cz.cvut.fel.integracniportal.cesnet;

import com.jcraft.jsch.SftpException;
import resourceitems.CesnetFileMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public interface CesnetService {
    public List<CesnetFileMetadata> getFileList() throws FileAccessException;

    public InputStream getFile(String filename) throws SftpException, IOException;

    public void deleteFile(String filename) throws SftpException;

    public CesnetFileMetadata getFileMetadata(String filename) throws FileAccessException;

    public void moveFileOffline(String filename) throws FileAccessException;

    public void moveFileOnline(String filename) throws FileAccessException;

    public void uploadFile(InputStream fileStream, String filename) throws SftpException;
}
