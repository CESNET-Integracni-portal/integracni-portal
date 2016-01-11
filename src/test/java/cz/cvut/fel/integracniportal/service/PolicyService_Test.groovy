package cz.cvut.fel.integracniportal.service

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.model.AccessControlPermission
import cz.cvut.fel.integracniportal.model.FileMetadata
import cz.cvut.fel.integracniportal.model.Folder
import cz.cvut.fel.integracniportal.model.Node
import cz.cvut.fel.integracniportal.model.Policy
import cz.cvut.fel.integracniportal.model.PolicyState
import cz.cvut.fel.integracniportal.model.PolicyType
import org.junit.Before
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import static org.junit.Assert.*
import static org.mockito.Mockito.when

/**
 * @author Eldar Iosip
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DatabaseSetup("nodes.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class PolicyService_Test extends AbstractIntegrationTestCase {

    final OWNER_ID = 80L
    final OWNER_USERNAME = "owner"

    @Autowired
    private PolicyService policyService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private UserDetailsService userDetailsService

    @Autowired
    private FileMetadataService fileMetadataService

    @Autowired
    private FolderService folderService

    @Mock
    private AuthenticationService authenticationService

    @Mock
    private Authentication authentication

    @Mock
    private User user

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this)
        userDetailsService.setAuthenticationService(authenticationService)

        when(user.getUsername()).thenReturn(OWNER_USERNAME)
        when(authentication.getPrincipal()).thenReturn(user)
        when(authenticationService.getCurrentAuthentication()).thenReturn(authentication)
    }

    @Test
    void "should create a new policy for a node"() {
        Date activeAfter = new Date()

        Policy policy = policyService.createPolicy(71L, PolicyType.REMOVE, activeAfter)

        assertEquals(OWNER_ID, policy.getOwner().getId())
        assertEquals(activeAfter, policy.getActiveAfter())
        assertEquals(0, policy.getAttempts())
        assertEquals(71L, policy.getNode().getId())
        assertEquals(PolicyType.REMOVE, policy.getType())
    }

    @Test
    void "should update existing policy for a node"() {
        Date activeAfter = new Date()

        Date activeAfter2 = new Date()
        activeAfter2.setTime(123123L)

        Policy createdPolicy = policyService.createPolicy(71L, PolicyType.NOTICATION, activeAfter)

        Node node = nodeService.getNodeById(71L)

        assertEquals(1, node.getPolicies().size())
        assertEquals(PolicyType.NOTICATION, node.getPolicies().first().getType())

        Policy updatedPolicy = policyService.updatePolicy(createdPolicy.getId(), PolicyType.REMOVE, activeAfter2)

        node = nodeService.getNodeById(71L)

        assertEquals(1, node.getPolicies().size())

        assertEquals(OWNER_ID, updatedPolicy.getOwner().getId())
        assertEquals(activeAfter2, updatedPolicy.getActiveAfter())
        assertEquals(0, updatedPolicy.getAttempts())
        assertEquals(71L, updatedPolicy.getNode().getId())
        assertEquals(PolicyType.REMOVE, updatedPolicy.getType())
    }

    @Test
    void "should delete existing policy for a node"() {
        Policy createdPolicy = policyService.createPolicy(71L, PolicyType.NOTICATION, new Date())

        Node node = nodeService.getNodeById(71L)

        assertEquals(1, node.getPolicies().size())
        assertEquals(PolicyType.NOTICATION, node.getPolicies().first().getType())

        policyService.deletePolicy(createdPolicy.getId())

        node = nodeService.getNodeById(71L)

        assertEquals(0, node.getPolicies().size())
    }

    @Test
    void "should get all policies for a node"() {
        policyService.createPolicy(71L, PolicyType.NOTICATION, new Date())
        policyService.createPolicy(71L, PolicyType.REMOVE, new Date())

        List<Policy> storedPolicies = policyService.getNodePolicies(71L)

        assertEquals(2, storedPolicies.size())
    }

    @Test
    void "should get all node policies ordered by a date"() {
        Date activeAfterOlder = new Date()
        activeAfterOlder.setTime(123123L)

        Date activeAfter = new Date()

        policyService.createPolicy(71L, PolicyType.NOTICATION, activeAfter)
        policyService.createPolicy(71L, PolicyType.REMOVE, activeAfterOlder)

        List<Policy> storedPolicies = policyService.getNodePolicies(71L)

        assertEquals(activeAfterOlder, storedPolicies.first().getActiveAfter())
        assertEquals(activeAfter, storedPolicies.last().getActiveAfter())
    }

}
