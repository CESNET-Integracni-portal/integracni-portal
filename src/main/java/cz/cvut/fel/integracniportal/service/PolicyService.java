package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.Policy;
import cz.cvut.fel.integracniportal.model.PolicyType;
import cz.cvut.fel.integracniportal.model.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Eldar Iosip
 */
public interface PolicyService {

    /**
     * Return all the available Node policies.
     *
     * @return Set<AccessControlPermission>
     */
    Set<PolicyType> getPolicyTypes();

    /**
     * Create new policy rule for user on node.
     *
     * @param nodeId      Node affected by the policy
     * @param type        PolicyType type, determining the action initiated in future
     * @param activeAfter Earliest date and time when the action should be processed
     */
    Policy createPolicy(Long nodeId, PolicyType type, Date activeAfter);

    /**
     * Update existing policy
     *
     * @param policyId    identification
     * @param type        new PolicyType
     * @param activeAfter new date
     */
    Policy updatePolicy(Long policyId, PolicyType type, Date activeAfter);

    /**
     * Delete policy.
     *
     * @param policyId Policy to remove
     */
    void deletePolicy(Long policyId);

    /**
     * Return all policies stored on node.
     *
     * @param nodeId Node to query
     */
    List<Policy> getNodePolicies(Long nodeId);

    /**
     * Process policies by provided date.
     *
     * @param date from where to start the policy search
     */
    void processByDate(Date date);
}
