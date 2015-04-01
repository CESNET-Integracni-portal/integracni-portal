package cz.cvut.fel.integracniportal.representation;

import java.util.List;

/**
 * Representation class for share to users.
 *
 * @author Radek Jezdik
 */
public class ShareRepresentation {

    private List<Long> shareWith;

    public List<Long> getShareWith() {
        return shareWith;
    }

    public void setShareWith(List<Long> shareWith) {
        this.shareWith = shareWith;
    }
}
