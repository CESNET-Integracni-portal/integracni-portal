package cz.cvut.fel.integracniportal.model;

/**
 * @author Radek Jezdik
 */
public abstract class AbstractEntity<T> {

    public abstract T getId();

    public abstract void setId(T id);

}
