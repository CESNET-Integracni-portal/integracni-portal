package cz.cvut.fel.integracniportal.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.cvut.fel.integracniportal.model.PolicyType;

import java.util.List;

/**
 * Common representation for Folder and FileMetadata.
 *
 * @author Eldar Iosip
 */
public class NodeRepresentation {

    protected Long id;

    protected String name;

    protected PolicyType currentPolicy;

    protected List<LabelRepresentation> labels;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PolicyType getCurrentPolicy() {
        return currentPolicy;
    }

    public void setCurrentPolicy(PolicyType currentPolicy) {
        this.currentPolicy = currentPolicy;
    }

    public List<LabelRepresentation> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelRepresentation> labels) {
        this.labels = labels;
    }
}
