package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.FileMetadata;

import java.io.OutputStream;
import java.util.Map;


public interface FileCompressor extends Runnable {

    /**
     * Returns outputStream through which compressed data is sent.
     */
    OutputStream getOutputStream();

    /**
     * Inserts a new file into the archive with its default name
     *
     * @param fileMetadata
     */
    void putFile(FileMetadata fileMetadata);

    /**
     * Adds a new file that will later be added to the archive with the provided path.
     * Used in archiving subfolders.
     *
     * @param fileMetadata
     * @param path
     */
    void addFile(FileMetadata fileMetadata, String path);

    /**
     * Adds a new file that will later be added to the archive root with its default filename.
     *
     * @param fileMetadata
     */
    void addFile(FileMetadata fileMetadata);

    /**
     * Adds a map constisting of FileMetadata and its paths to be added to the archive.
     *
     * @param fileMetadataPathMap
     */
    void addFiles(Map<FileMetadata, String> fileMetadataPathMap);

    /**
     * Finishes the archived file. Should be called right after last item was put.
     *
     */
    void finish();


    /**
     * Returns extension used by archiving method. e.g. ".zip".
     *
     * @return
     */
    String getExtension();


    /**
     * Flushes the inner outputStream.
     *
     */
    void flush();

    /**
     * Returns a file name with the compressor extension.
     *
     * @param fileMetadata
     * @return
     */
    String convertFileName(FileMetadata fileMetadata);

}
