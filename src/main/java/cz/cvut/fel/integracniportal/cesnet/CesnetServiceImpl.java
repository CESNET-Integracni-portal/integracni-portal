package cz.cvut.fel.integracniportal.cesnet;

import com.jcraft.jsch.SftpException;
import cz.cvut.fel.integracniportal.exceptions.FileAccessException;
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CesnetServiceImpl implements CesnetService {

    private static final Logger logger = Logger.getLogger(CesnetServiceImpl.class);

    private final String rootDir = "VO_storage-cache_tape";

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Provider<SshChannel> sshResourceProvider;

    @Autowired
    Provider<SftpChannel> sftpChannelChannelProvider;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public List<CesnetFileMetadata> getFileList() throws FileAccessException, ServiceAccessException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("cd ");
        stringBuilder.append(rootDir);
        stringBuilder.append("; ");
        stringBuilder.append("dmls -l $(find . -type f) | sed 's, [^ ]*'`pwd -P`\\/', ,g'");

        return getFileListForCommand(stringBuilder.toString());
    }

    @Override
    public List<CesnetFileMetadata> getFileListByType(FileState fileState) throws FileAccessException, ServiceAccessException {
        StringBuilder stringBuilder = new StringBuilder();
        // Go to root directory
        stringBuilder.append("cd ");
        stringBuilder.append(rootDir);
        stringBuilder.append("; ");
        // Get the list of files
        stringBuilder.append("FILELIST=$(dmfind . -state ");
        stringBuilder.append(fileState);
        stringBuilder.append("); ");
        // Check whether the list is empty
        stringBuilder.append("[ \"$FILELIST\" ] && ");
        // Get the metadata
        stringBuilder.append("dmls -l $(find $FILELIST -maxdepth 0 -type f) | sed 's, [^ ]*'`pwd -P`\\/', ,g'");

        return getFileListForCommand(stringBuilder.toString());
    }

    private List<CesnetFileMetadata> getFileListForCommand(String command) throws FileAccessException, ServiceAccessException {
        SshChannel sshChannel = sshResourceProvider.get();
        List<String> lsOutput = sshChannel.sendCommand(command);
        if (lsOutput.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<CesnetFileMetadata> fileMetadataList = new ArrayList<CesnetFileMetadata>(lsOutput.size());
            for (int i = 0; i < lsOutput.size(); ++i) {
                fileMetadataList.add(parseFileMetadata(lsOutput.get(i)));
            }
            return fileMetadataList;
        }
    }

    @Override
    public InputStream getFile(String filename) throws SftpException, IOException {
        SftpChannel sftpChannel = sftpChannelChannelProvider.get();
        sftpChannel.cd(rootDir);
        return sftpChannel.getFile(filename);
    }

    @Override
    public void deleteFile(String filename) throws SftpException {
        SftpChannel sftpChannel = sftpChannelChannelProvider.get();
        sftpChannel.cd(rootDir);
        sftpChannel.deleteFile(filename);
    }

    @Override
    public CesnetFileMetadata getFileMetadata(String filename) throws FileAccessException, ServiceAccessException, FileNotFoundException {
        SshChannel sshChannel = sshResourceProvider.get();

        List<String> lsOutput = sshChannel.sendCommand("dmls -l " + rootDir + "/" + filename);
        if (lsOutput.size() != 1) {
            throw new FileNotFoundException();
        }

        CesnetFileMetadata fileMetadata = parseFileMetadata(lsOutput.get(0));
        return fileMetadata;
    }

    @Override
    public void moveFileOffline(String filename) throws FileNotFoundException, ServiceAccessException {
        SshChannel sshChannel = sshResourceProvider.get();

        List<String> response = sshChannel.sendCommand("dmput -r " + rootDir + "/" + filename);
        if (response.size() > 0) {
            throw new FileNotFoundException(response.get(0));
        }
    }

    @Override
    public void moveFileOnline(String filename) throws ServiceAccessException, FileNotFoundException {
        SshChannel sshChannel = sshResourceProvider.get();

        List<String> response = sshChannel.sendCommand("dmget " + rootDir + "/" + filename);
        if (response.size() > 0) {
            throw new FileNotFoundException(response.get(0));
        }
    }

    @Override
    public void uploadFile(InputStream fileStream, String filename) throws SftpException {
        SftpChannel sftpChannel = sftpChannelChannelProvider.get();
        sftpChannel.cd(rootDir);
        sftpChannel.uploadFile(fileStream, filename);
    }

    private CesnetFileMetadata parseFileMetadata(String lsOutput) throws FileAccessException {
        String[] parts = lsOutput.split("\\s+");
        if (parts == null || parts.length < 9) {
            throw new FileAccessException("cesnet.parseError");
        }

        CesnetFileMetadata fileMetadata = new CesnetFileMetadata();
        fileMetadata.setFilename(parts[8]);
        fileMetadata.setFilesize(Long.parseLong(parts[4]));
        fileMetadata.setState(FileState.valueOf(parts[7].substring(1, parts[7].length()-1)));
        try {
            fileMetadata.setCreated(dateFormat.parse(parts[5]+" "+parts[6]));
        } catch (ParseException e) {
            logger.error("Unable to parse date " + parts[5] + " and time " + parts[6]);
        }

        return fileMetadata;
    }
}
