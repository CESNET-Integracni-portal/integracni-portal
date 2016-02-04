package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.exceptions.ZipStreamException;
import cz.cvut.fel.integracniportal.model.FileMetadata;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileCompressorImpl implements FileCompressor {

    private FileMetadataService fileMetadataService;
    private ZipOutputStream zipOutputStream;
    private boolean finished = false;

    public ZipFileCompressorImpl(OutputStream outputStream, FileMetadataService fileMetadataService){
        this.zipOutputStream = new ZipOutputStream(outputStream);
        this.fileMetadataService = fileMetadataService;
    }

    public OutputStream getOutputStream() {
        return this.zipOutputStream;
    }

    public void putFile(FileMetadata fileMetadata) {
        if(!this.finished) {
            this.putFile(fileMetadata, fileMetadata.getFilename());
        }else{
            throw new ZipStreamException("Trying to add file to already finished archive.");
        }
    }

    public void finish() {
        try {
            this.zipOutputStream.finish();
        } catch (IOException e) {
            throw new ZipStreamException("ZipStream failed to finish", e);
        }
        this.finished = true;
    }

    public void putFile(FileMetadata fileMetadata, String path) {
        try {
            this.zipOutputStream.putNextEntry(new ZipEntry(path));
            fileMetadataService.copyFileToOutputStream(fileMetadata.getId(), this.zipOutputStream);
            this.zipOutputStream.closeEntry();
            this.flush();
        } catch (IOException e) {
            throw new ZipStreamException("ZipoutputStream failed to put/close entry.");
        }

    }

    public String getExtension(){
        return(new String(".zip"));
    }

    public void flush() {
        try {
            this.zipOutputStream.flush();
        } catch (IOException e) {
            new ZipStreamException("ZipOutputStream failed to flush.", e);
        }
    }
}
