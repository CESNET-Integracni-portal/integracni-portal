package cz.cvut.fel.integracniportal.extension;

import com.jcraft.jsch.SftpException;
import cz.cvut.fel.integracniportal.api.*;
import cz.cvut.fel.integracniportal.exceptions.FileAccessException;
import cz.cvut.fel.integracniportal.exceptions.FileNotFoundException;
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Radek Jezdik
 */
@Component
public class CesnetFileRepository implements FileRepository, OfflinableFileRepository, BinFileRepository, EncryptableRepository {

    private static final Logger logger = Logger.getLogger(CesnetFileRepository.class);

    private static final String ROOT_DIR = "VO_storage-cache_tape";

    private static final String HOME_FOLDER_PREFIX = "home_";

    private static final String BIN_FOLDER_PREFIX = "bin_";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private String encryptionPassword;

    @Autowired
    private Provider<SshChannel> sshResourceProvider;

    @Autowired
    private Provider<SftpChannel> sftpChannelChannelProvider;

    public void setEncryptionPassword(String encryptionPassword) {
        this.encryptionPassword = encryptionPassword;
    }

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

    }

    @Override
    public void moveFolderToBin(FolderDefinition folder) {
        SftpChannel sftpChannel = null;
        try {
            sftpChannel = sftpChannelChannelProvider.get();
            String path = getUserBinFolder(folder.getOwner()) + "/" + folder.getPath();
            createFolderPath(path.split("/", -1));
            sftpChannel.renameFolder(getHomeFolderPath(folder), getBinFolderPath(folder));
        } catch (SftpException e) {
            throw new ServiceAccessException("Could not move folder to bin", e);
        } finally {
            if (sftpChannel != null) {
                sftpChannel.returnToPool();
            }
        }
    }

    @Override
    public void renameFolder(String newName, FolderDefinition folder) {

    }

    @Override
    public void putFile(FileDefinition file, InputStream stream) {
        SftpChannel sftpChannel = null;
        try {
            sftpChannel = sftpChannelChannelProvider.get();

            String path = getUserHomeFolder(file.getOwner());
            createFolderPath(toArray(path));
            sftpChannel.cd(path);

            sftpChannel.uploadFile(encrypt(stream, file.getSalt()), "" + file.getId());
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
            InputStream encryptedFile = sftpChannel.getFile("" + file.getId(), outputStream);

            IOUtils.copyLarge(decrypt(encryptedFile, file.getSalt()), outputStream);
            outputStream.flush();
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
        SftpChannel sftpChannel = null;
        try {
            sftpChannel = sftpChannelChannelProvider.get();
            createFolderPath(getBinFolderPath(file.getFolder()).split("/"));
            sftpChannel.renameFile(getHomeFolderPath(file.getFolder()) + "/" + file.getId(), getBinFolderPath(file.getFolder()) + "/" + file.getId());
        } catch (SftpException e) {
            throw new ServiceAccessException("Could not move file to bin", e);
        } finally {
            if (sftpChannel != null) {
                sftpChannel.returnToPool();
            }
        }
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
            sftpChannel.deleteFile("" + file.getId());
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

            String filePath = getHomeFolderPath(file.getFolder()) + "/" + file.getId();
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

            String filePath = getHomeFolderPath(file.getFolder()) + "/" + file.getId();
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

            String filePath = getHomeFolderPath(file.getFolder()) + "/" + file.getId();
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
        Long orgUnitId = user.getOrganizationalUnitId();
        if (orgUnitId == null) {
            orgUnitId = 0L;
        }
        return ROOT_DIR + "/" + orgUnitId + "/" + HOME_FOLDER_PREFIX + user.getId();
    }

    private String getUserBinFolder(User user) {
        Long orgUnitId = user.getOrganizationalUnitId();
        if (orgUnitId == null) {
            orgUnitId = 0L;
        }
        return ROOT_DIR + "/" + orgUnitId + "/" + BIN_FOLDER_PREFIX + user.getId();
    }

    private String getHomeFolderPath(FolderDefinition folder) {
        return getUserHomeFolder(folder.getOwner()) + "/" + folder.getPath();
    }

    private String getBinFolderPath(FolderDefinition folder) {
        return getUserBinFolder(folder.getOwner()) + "/" + folder.getPath();
    }

    public Date toDate(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.parse(date);
    }

    private String[] toArray(String path) {
        return path.split("/", -1);
    }

    @Override
    public InputStream encrypt(InputStream file, String salt) {
        BytesEncryptor encryptor = Encryptors.standard(encryptionPassword, salt);
        ByteArrayOutputStream outFile = new ByteArrayOutputStream();

        try {
            System.out.println(javax.crypto.Cipher.getMaxAllowedKeyLength("AES"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            byte[] input = new byte[64];
            int bytesRead;

            while ((bytesRead = file.read(input)) != -1) {
                outFile.write(input);
            }
            outFile.flush();
            outFile.close();
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(encryptor.encrypt(outFile.toByteArray()));
    }

    @Override
    public InputStream decrypt(InputStream file, String salt) {
        return null;
    }
}
