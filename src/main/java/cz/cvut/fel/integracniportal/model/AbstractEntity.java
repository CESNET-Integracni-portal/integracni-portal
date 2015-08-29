package cz.cvut.fel.integracniportal.model;

/**
 * @author Radek Jezdik
 */
public abstract class AbstractEntity<T> {

    public abstract T getId();

    public abstract void setId(T id);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractEntity)) {
            return false;
        }

        AbstractEntity that = (AbstractEntity) o;

        if (this.getId() == null || that.getId() == null) {
            return false;
        }

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

}
