package cz.cvut.fel.integracniportal.extension;

import com.jcraft.jsch.SftpException;
import cz.cvut.fel.integracniportal.api.*;
import cz.cvut.fel.integracniportal.exceptions.FileAccessException;
import cz.cvut.fel.integracniportal.exceptions.FileNotFoundException;
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Radek Jezdik
 */
@Component
public class CesnetFileRepository implements FileRepository, OfflinableFileRepository, BinFileRepository {

    private static final Logger logger = Logger.getLogger(CesnetFileRepository.class);

    private static final String ROOT_DIR = "VO_storage-cache_tape";

    private static final String HOME_FOLDER_PREFIX = "home_";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    @Autowired
    private Provider<SshChannel> sshResourceProvider;

    @Autowired
    private Provider<SftpChannel> sftpChannelChannelProvider;

    @Override
    public String getName() {
        return "CESNET";
    }

    @Override
    public String getType() {
        return "cesnet";
    }

    @Override
    public void createFolder(FolderDefinition folder) {
        String path = getUserHomeFolder(folder.getOwner());
        createFolderPath(toArray(path));
    }

    private void createFolderPath(String[] folderPath) {
        SftpChannel sftpChannel = null;
        try {
            sftpChannel = sftpChannelChannelProvider.get();
            String currentFolder = folderPath[0];

            int i = 1;
            do {
                try {
                    sftpChannel.cd(currentFolder);
                } catch (SftpException e1) {
                    logger.debug("Folder " + currentFolder + " does not exist, trying to create it");
                    try {
                        sftpChannel.mkdir(currentFolder);
                        sftpChannel.cd(currentFolder);
                    } catch (SftpException e2) {
                        logger.debug("Could not create folder " + currentFolder, e2);
                        throw new ServiceAccessException("Could not create folder", e2);
                    }
                }
                if (i == folderPath.length) {
                    break;
                }
                currentFolder = folderPath[i++];
            } while (true);
        } finally {
            if (sftpChannel != null) {
                sftpChannel.returnToPool();
            }
        }
    }

    @Override
    public void moveFolder(FolderDefinition from, FolderDefinition to) {
        // no code - files are in one folder anyway
    }

    @Override
    public void moveFolderToBin(FolderDefinition folder) {
        // no code - files are in one folder anyway
    }

    @Override
    public void renameFolder(String newName, FolderDefinition folder) {
        // no code - files are in one folder anyway
    }

    @Override
    public void putFile(FileDefinition file, InputStream stream) {
        SftpChannel sftpChannel = null;
        try {
            sftpChannel = sftpChannelChannelProvider.get();

            String path = getUserHomeFolder(file.getOwner());
            createFolderPath(toArray(path));
            sftpChannel.cd(path);

            sftpChannel.uploadFile(stream, file.getId());
        } catch (SftpException e) {
            throw new ServiceAccessException("Could not upload file", e);
        } finally {
            if (sftpChannel != null) {
                sftpChannel.returnToPool();
            }
        }
    }

    @Override
    public void getFile(FileDefinition file, OutputStream outputStream) {
        SftpChannel sftpChannel = null;
        try {
            sftpChannel = sftpChannelChannelProvider.get();
            sftpChannel.cd(getUserHomeFolder(file.getOwner()));
            sftpChannel.getFile(file.getId(), outputStream);
        } catch (Exception e) {
            throw new FileAccessException("Could not get file", e);
        } finally {
            if (sftpChannel != null) {
                sftpChannel.returnToPool();
            }
        }
    }

    @Override
    public void moveFileToBin(FileDefinition file) {
        // no code - files are in one folder anyway
    }

    @Override
    public void moveFile(FileDefinition file, FolderDefinition to) {
        // no code - files are in one folder anyway
    }

    @Override
    public void renameFile(String newName, FileDefinition file) {
        // no code - file names are IDs
    }

    @Override
    public void deleteFile(FileDefinition file) {
        SftpChannel sftpChannel = null;
        try {
            sftpChannel = sftpChannelChannelProvider.get();
            sftpChannel.cd(getUserHomeFolder(file.getOwner()));
            sftpChannel.deleteFile(file.getId());
        } catch (Exception e) {
            throw new FileAccessException("Could not get file", e);
        } finally {
            if (sftpChannel != null) {
                sftpChannel.returnToPool();
            }
        }
    }

    @Override
    public void deleteFolder(FolderDefinition folderDef) {
        // no code - files are in one folder anyway
    }

    @Override
    public FileDefinition getFileMetadata(FileDefinition file) {
        SshChannel sshChannel = null;
        try {
            sshChannel = sshResourceProvider.get();

            String filePath = getUserHomeFolder(file.getOwner()) + "/" + file.getId();
            List<String> lsOutput = sshChannel.sendCommand("dmls -l " + filePath);
            if (lsOutput.size() != 1) {
                throw new FileNotFoundException("Could not get file metadata from CESNET");
            }

            return parseFileMetadata(lsOutput.get(0));
        } finally {
            if (sshChannel != null) {
                sshChannel.returnToPool();
            }
        }
    }

    private FileDefinition parseFileMetadata(String lsOutput) {
        String[] parts = lsOutput.split("\\s+");
        if (parts.length < 9) {
            throw new FileAccessException("cesnet.parseError");
        }

        FileDefinition fileDefinition = new FileDefinition();
        fileDefinition.setName(parts[8]);
        fileDefinition.setSize(Long.parseLong(parts[4]));
//        fileMetadata.setState(FileState.valueOf(parts[7].substring(1, parts[7].length() - 1)));
        try {
            fileDefinition.setDateCreated(toDate(parts[5] + " " + parts[6]));
        } catch (ParseException e) {
            logger.error("Unable to parse date " + parts[5] + " and time " + parts[6]);
        }

        return fileDefinition;
    }

    @Override
    public void moveFileOffline(FileDefinition file) {
        SshChannel sshChannel = null;
        try {
            sshChannel = sshResourceProvider.get();

            String filePath = getUserHomeFolder(file.getOwner()) + "/" + file.getId();
            List<String> response = sshChannel.sendCommand("dmput -r " + filePath);
            if (response.size() > 0) {
                throw new FileAccessException(response.get(0));
            }
        } finally {
            if (sshChannel != null) {
                sshChannel.returnToPool();
            }
        }
    }

    @Override
    public void moveFileOnline(FileDefinition file) {
        SshChannel sshChannel = null;
        try {
            sshChannel = sshResourceProvider.get();

            String filePath = getUserHomeFolder(file.getOwner()) + "/" + file.getId();
            List<String> response = sshChannel.sendCommand("dmget " + filePath);
            if (response.size() > 0) {
                throw new FileAccessException(response.get(0));
            }
        } finally {
            if (sshChannel != null) {
                sshChannel.returnToPool();
            }
        }
    }

    private String getUserHomeFolder(User user) {
        String orgUnitId = user.getOrganizationalUnitId();
        if (orgUnitId == null) {
            orgUnitId = "";
        }
        return ROOT_DIR + "/" + orgUnitId + "/" + HOME_FOLDER_PREFIX + user.getId();
    }

    public Date toDate(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.parse(date);
    }

    private String[] toArray(String path) {
        return path.split("/", -1);
    }
}
