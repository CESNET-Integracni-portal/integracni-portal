package cz.cvut.fel.integracniportal

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import cz.cvut.fel.integracniportal.dao.UserDetailsDao
import cz.cvut.fel.integracniportal.model.UserDetails
import org.apache.commons.io.IOUtils
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.kubek2k.springockito.annotations.experimental.junit.AbstractJUnit4SpringockitoContextTests
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.mock.web.MockHttpSession
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.web.context.WebApplicationContext

import javax.sql.DataSource

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

/**
 * @author Radek Jezdik
 */
@RunWith(SpringJUnit4ClassRunner)
@WebAppConfiguration
@ContextConfiguration(locations = [
        "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml",
        "classpath:config/test-applicationContext.xml"
])
@TestExecutionListeners([TransactionDbUnitTestExecutionListener, TransactionalTestExecutionListener])
@DbUnitConfiguration(dataSetLoader = XmlDataSetLoader)
@DirtiesContext
public abstract class AbstractIntegrationTestCase extends AbstractJUnit4SpringockitoContextTests {

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

	@Before
	public void setup() {
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
        def template = new JdbcTemplate(dataSource)
        template.execute("TRUNCATE SCHEMA public AND COMMIT")
    }

    public UserDetails getUser(id) {
        return userDao.getUserById(id);
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

}
