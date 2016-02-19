package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.exceptions.ZipStreamException;
import cz.cvut.fel.integracniportal.model.FileMetadata;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileCompressorImpl implements FileCompressor {

    private FileMetadataService fileMetadataService;
    private ZipOutputStream zipOutputStream;
    private boolean finished = false;
    Map<FileMetadata, String> filePathMap = null;

    public ZipFileCompressorImpl(OutputStream outputStream, FileMetadataService fileMetadataService){
        super();
        this.zipOutputStream = new ZipOutputStream(outputStream);
        this.fileMetadataService = fileMetadataService;
    }

    @Override
    public void run(){

        for(FileMetadata fileMetadata: this.filePathMap.keySet()){
            this.putFile(fileMetadata, this.filePathMap.get(fileMetadata));
        }

        this.finish();
        this.flush();

    }

    public void addFiles(Map<FileMetadata, String> map){
        if(this.filePathMap == null){
            this.filePathMap = map;
        }
        else {
            this.filePathMap.putAll(map);
        }
    }

    public void addFile(FileMetadata fileMetadata, String path){
        if(this.filePathMap == null){
            this.filePathMap = new HashMap<FileMetadata, String>();
        }

        this.filePathMap.put(fileMetadata, path);

    }
    /*
        Adds a file signature into collection, which will then be compressed into archive
     */
    public void addFile(FileMetadata fileMetadata){
        this.addFile(fileMetadata, fileMetadata.getFilename());
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

    @Override
    public String convertFileName(FileMetadata fileMetadata) {
        int dotIdx;
        String fileName;

        fileName = fileMetadata.getFilename();
        dotIdx = fileName.lastIndexOf(".");
        if(dotIdx == -1){
            fileName = fileName.concat(this.getExtension());
        }
        else{
            fileName = fileName.substring(0,dotIdx).concat(this.getExtension());
        }

        return fileName;
    }
}
