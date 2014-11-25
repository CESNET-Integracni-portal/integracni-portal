package cz.cvut.fel.integracniportal.cmis;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;

public class AlfrescoUtils {

    public static String parseId(Folder folder) {
        String[] idParts = folder.getId().split("/");
        return idParts[idParts.length-1];
    }

    public static String parseId(Document document) {
        String[] idParts = document.getId().split("/");
        idParts = idParts[idParts.length-1].split(";");
        return idParts[0];
    }

}
