package cz.cvut.fel.integracniportal.api;

/**
 * @author Radek Jezdik
 */
public class FolderDefinition {

    public static final FolderDefinition ROOT_FOLDER = new FolderDefinition() {

        @Override
        public boolean isRootFolder() {
            return true;
        }

    };

    private String id;

    private String path;

    private String name;

    private User owner;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isRootFolder() {
        return false;
    }

}
