package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.PolicyDao;
import cz.cvut.fel.integracniportal.exceptions.AccessDeniedException;
import cz.cvut.fel.integracniportal.exceptions.PolicyNotFoundException;
import cz.cvut.fel.integracniportal.model.Node;
import cz.cvut.fel.integracniportal.model.Policy;
import cz.cvut.fel.integracniportal.model.PolicyType;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Eldar Iosip
 */
@Service
@Transactional
public class PolicyServiceImpl implements PolicyService {

    public final static int MAXIMUM_CRON_ATTEMPTS = 3;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PolicyDao policyDao;

    @Override
    public Set<PolicyType> getPolicyTypes() {
        return new HashSet<PolicyType>(Arrays.asList(PolicyType.values()));
    }

    @Override
    public Policy createPolicy(Long nodeId, PolicyType type, Date activeAfter) {
        UserDetails currentUser = userDetailsService.getCurrentUser();
        Node node = nodeService.getNodeById(nodeId);

        Policy policy = new Policy();
        policy.setNode(node);

        this.checkPermissionToNodePolicy(currentUser, policy);

        //Create policy
        policy.setActiveAfter(activeAfter);
        policy.setPolicyType(type);
        policy.setOwner(currentUser);

        policyDao.save(policy);

        this.updateNodePolicyState(policy.getNode(), type);

        return policy;
    }

    @Override
    public Policy updatePolicy(Long policyId, PolicyType type, Date activeAfter) {
        UserDetails currentUser = userDetailsService.getCurrentUser();
        Policy policy = policyDao.get(policyId);

        this.checkPermissionToNodePolicy(currentUser, policy);

        //Update policy
        policy.setActiveAfter(activeAfter);
        policy.setPolicyType(type);
        policyDao.save(policy);

        this.updateNodePolicyState(policy.getNode(), type);

        return policy;
    }

    @Override
    public void deletePolicy(Long policyId) {
        UserDetails currentUser = userDetailsService.getCurrentUser();
        Policy policy = policyDao.get(policyId);

        this.checkPermissionToNodePolicy(currentUser, policy);

        policyDao.delete(policy);

        this.updateNodePolicyState(policy.getNode(), null);
    }

    @Override
    public void processByDate(Date date) {
        List<Policy> policyList = policyDao.findByActiveAfter(date);

        for (Policy policy : policyList) {
            Node node = policy.getNode();
            switch (policy.getType()) {
                case REMOVE:
                    try {
                        nodeService.removeNode(node, true);
                    } catch (Exception e) {
                        policy.increaseAttempts();
                        if (policy.getAttempts() == MAXIMUM_CRON_ATTEMPTS) {
                            node.setCurrentPolicy(PolicyType.FAILED_REMOVAL);
                            nodeService.saveNode(node);
                            policy.setProcessed(true);
                        }
                    }
                    break;
                case NOTICATION:
                    node.setCurrentPolicy(PolicyType.NOTICATION);
                    nodeService.saveNode(node);
                    break;
            }
        }
    }

    /**
     * Update node policy state.
     * <p>
     * Informative state about the a process, the selected policy will initiate
     *
     * @param node affected by the policy
     * @param type of the policy
     */
    private void updateNodePolicyState(Node node, PolicyType type) {
        PolicyType semiType;

        switch (type) {
            case REMOVE:
                semiType = PolicyType.AWAITING_REMOVAL;
                break;
            default:
                semiType = null;
        }

        node.setCurrentPolicy(semiType);
        nodeService.saveNode(node);
    }

    private void checkPermissionToNodePolicy(UserDetails user, Policy policy)
            throws AccessDeniedException, PolicyNotFoundException {
        if (policy == null) {
            throw new PolicyNotFoundException("Requested policy is not found.");
        }

        if (!user.equals(policy.getNode().getOwner())) {
            throw new AccessDeniedException("Only the owner is able to set the policies.");
        }
    }
}
