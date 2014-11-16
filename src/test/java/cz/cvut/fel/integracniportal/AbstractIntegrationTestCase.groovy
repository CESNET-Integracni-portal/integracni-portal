package cz.cvut.fel.integracniportal

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import org.junit.Before
import org.junit.runner.RunWith
import org.kubek2k.springockito.annotations.experimental.junit.AbstractJUnit4SpringockitoContextTests
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.web.context.WebApplicationContext

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
@TestExecutionListeners(TransactionDbUnitTestExecutionListener)
@DbUnitConfiguration(dataSetLoader = XmlDataSetLoader)
public abstract class AbstractIntegrationTestCase extends AbstractJUnit4SpringockitoContextTests {

    protected MockMvc mockMvc

    @Autowired
	protected WebApplicationContext wac

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this)
		this.mockMvc = webAppContextSetup(this.wac).build()
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
		return "/rest/v0.1/" + urlTemplate
	}

}
