package cz.cvut.fel.integracniportal.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * FileMetadata and Folder superclass
 *
 * @author Eldar Iosip
 */
@Entity
@Table(name = "PERSON")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class AbstractUser extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", unique = true)
    private Long userId;

    public Long getId() {
        return userId;
    }

    public void setId(Long id) {
        this.userId = id;
    }
}
