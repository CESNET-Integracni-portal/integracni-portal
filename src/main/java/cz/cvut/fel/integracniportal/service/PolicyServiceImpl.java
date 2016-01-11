package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.PolicyDao;
import cz.cvut.fel.integracniportal.exceptions.AccessDeniedException;
import cz.cvut.fel.integracniportal.exceptions.PolicyNotFoundException;
import cz.cvut.fel.integracniportal.model.*;
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

        updateNodePolicyState(node, policy);

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

        updateNodePolicyState(policy.getNode(), policy);

        return policy;
    }

    @Override
    public void deletePolicy(Long policyId) {
        UserDetails currentUser = userDetailsService.getCurrentUser();
        Policy policy = policyDao.get(policyId);

        this.checkPermissionToNodePolicy(currentUser, policy);

        policyDao.delete(policy);

        policy.getNode().getPolicies().remove(policy);
        nodeService.saveNode(policy.getNode());
    }

    @Override
    public List<Policy> getNodePolicies(Long nodeId) {
        UserDetails currentUser = userDetailsService.getCurrentUser();
        Node node = nodeService.getNodeById(nodeId);

        if (!node.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("policy.Accessdenied");
        }

        return node.getPolicies();
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
                            node.setPolicyState(PolicyState.FAILED_REMOVAL);
                            nodeService.saveNode(node);
                            policy.setProcessed(true);
                        }
                    }
                    break;
                case NOTICATION:
                    node.setPolicyState(PolicyState.AWAITING_NOTIFICATION);
                    nodeService.saveNode(node);
                    break;
            }
        }
    }

    private void updateNodePolicyState(Node node, Policy policy) {
        node.setPolicyState(getPolicyState(policy));
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

    private PolicyState getPolicyState(Policy policy) {
        switch (policy.getType()) {
            case REMOVE:
                return PolicyState.AWAITING_REMOVAL;
            case NOTICATION:
                return PolicyState.AWAITING_NOTIFICATION;
            default:
                return PolicyState.AWAITING;
        }
    }
}
