package cz.cvut.fel.integracniportal.cesnet;

import com.jcraft.jsch.SftpException;
import cz.cvut.fel.integracniportal.exceptions.FileAccessException;
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public interface CesnetService {
    public List<CesnetFileMetadata> getFileList() throws FileAccessException, ServiceAccessException;

    public List<CesnetFileMetadata> getFileListByType(FileState fileState) throws FileAccessException, ServiceAccessException;

    public InputStream getFile(String filename) throws SftpException, IOException;

    public void deleteFile(String filename) throws SftpException;

    public CesnetFileMetadata getFileMetadata(String filename) throws FileAccessException, ServiceAccessException, FileNotFoundException;

    public void moveFileOffline(String filename) throws ServiceAccessException, FileNotFoundException;

    public void moveFileOnline(String filename) throws ServiceAccessException, FileNotFoundException;

    public void uploadFile(InputStream fileStream, String filename) throws SftpException;
}
