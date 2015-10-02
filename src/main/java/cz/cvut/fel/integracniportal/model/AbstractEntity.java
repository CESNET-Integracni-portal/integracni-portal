package cz.cvut.fel.integracniportal.model;

import javax.persistence.MappedSuperclass;

/**
 * @author Radek Jezdik
 */
@MappedSuperclass
public abstract class AbstractEntity<T> {

    public abstract T getId();

    public abstract void setId(T id);

}
