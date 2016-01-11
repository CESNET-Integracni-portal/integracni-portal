package cz.cvut.fel.integracniportal

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import cz.cvut.fel.integracniportal.command.node.CreateFileCommand
import cz.cvut.fel.integracniportal.command.node.CreateFolderCommand
import cz.cvut.fel.integracniportal.dao.UserDetailsDao
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserPassword
import cz.cvut.fel.integracniportal.model.UserDetails
import cz.cvut.fel.integracniportal.service.FileUpload
import org.apache.commons.io.IOUtils
import org.axonframework.commandhandling.gateway.CommandGateway
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.After
import org.junit.Before
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.mock.web.MockHttpSession
import org.springframework.orm.hibernate3.SessionFactoryUtils
import org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import javax.sql.DataSource

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

/**
 * @author Radek Jezdik
 */
@WebAppConfiguration
@ContextConfiguration(locations = [
        "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml",
        "classpath:config/test-applicationContext.xml"
])
@TestExecutionListeners([
        TransactionDbUnitTestExecutionListener,
        TransactionalTestExecutionListener,
        DependencyInjectionTestExecutionListener,
        DirtiesContextTestExecutionListener
])
@DbUnitConfiguration(dataSetLoader = XmlDataSetLoader)
@DirtiesContext
abstract class AbstractIntegrationSpecification extends Specification {

    protected MockMvc mockMvc

    @Autowired
    protected WebApplicationContext wac

    @Autowired
    protected MethodSecurityInterceptor securityInterceptor

    @Autowired
    private UserDetailsDao userDao

    @Autowired
    private DataSource dataSource

    @Autowired
    protected CommandGateway commandGateway

    @Autowired
    private SessionFactory sessionFactory

    private Session session

    @Before
    public void setupIntegrationTest() {
        MockitoAnnotations.initMocks(this)

        Authentication authentication = new UsernamePasswordAuthenticationToken(new User("test", "password", []), "test", [])

        SecurityContext securityContext = SecurityContextHolder.getContext()
        securityContext.setAuthentication(authentication)

        MockHttpSession session = new MockHttpSession()
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext)

        def defaultRequest = get("dummy").session(session)
        this.mockMvc = webAppContextSetup(this.wac).defaultRequest(defaultRequest).build()
    }

    @After
    public void drop() {
        if (dataSource) {
            def template = new JdbcTemplate(dataSource)
            template.execute("TRUNCATE SCHEMA public AND COMMIT")
        }
        if (session) {
            session.close()
            session = null
        }
    }

    public <T> T get(Class<T> clas, Serializable id) {
        if (session) {
            session.close()
        }
        session = SessionFactoryUtils.getNewSession(sessionFactory)
        return (T) session.get(clas, id)
    }

    public UserDetails getUser(id) {
        return this.get(UserDetails, id);
    }

    public ResultActions apiGet(String urlTemplate) throws Exception {
        return mockMvc.perform(
                get(fromApi(urlTemplate))
        )
    }

    public ResultActions apiPost(String urlTemplate) throws Exception {
        return mockMvc.perform(
                post(fromApi(urlTemplate))
        )
    }

    public ResultActions apiPost(String urlTemplate, String content) throws Exception {
        return mockMvc.perform(
                post(fromApi(urlTemplate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        )
    }

    public ResultActions apiPut(String urlTemplate, String content) throws Exception {
        return mockMvc.perform(
                put(fromApi(urlTemplate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        )
    }

    public ResultActions apiDelete(String urlTemplate) throws Exception {
        return mockMvc.perform(
                delete(fromApi(urlTemplate))
        )
    }

    public static String fromApi(String urlTemplate) {
        return "/rest/v0.2/" + urlTemplate
    }

    public InputStream getResource(String name) {
        return getClass().getResourceAsStream(name)
    }

    public String getResourceAsString(String name) {
        return IOUtils.toString(getResource(name));
    }

    public void dispatch(Object command) {
        commandGateway.sendAndWait(command)
    }

    public void createFolder(String id, String name, String parentId, String ownerId = "1", String space = "cesnet") {
        dispatch(new CreateFolderCommand(
                FolderId.of(id),
                name,
                parentId == null ? null : FolderId.of(parentId),
                UserId.of(ownerId),
                space
        ))
    }

    public void createFile(String id, String name, String parentId, String ownerId = "1", String space = "cesnet",
                           String mimetype = "application/json", String content = "{}") {
        dispatch(new CreateFileCommand(
                FileId.of(id),
                new FileUpload(name, mimetype, IOUtils.toInputStream(content)),
                parentId == null ? null : FolderId.of(parentId),
                UserId.of(ownerId),
                space,
                Optional.empty()
        ))
    }

    UserPassword password(String s) {
        return new UserPassword() {
            @Override
            String getHashedUserPassword() {
                return s
            }
        }
    }

}
