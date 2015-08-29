package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;

/**
 * Created by Vavat on 1. 3. 2015.
 */
@Entity
@Table(name = "label", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "color", "owner"})
})
public class Label extends AbstractEntity<String> {

    @Id
    @Column(name = "label_id")
    private String labelId;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "user_id", nullable = false)
    private UserDetails owner;

    @Override
    public String getId() {
        return labelId;
    }

    @Override
    public void setId(String id) {
        this.labelId = id;
    }

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public UserDetails getOwner() {
        return owner;
    }

    public void setOwner(UserDetails owner) {
        this.owner = owner;
    }
}
