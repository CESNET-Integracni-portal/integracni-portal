package cz.cvut.fel.integracniportal.controller

import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.cmis.AlfrescoService
import org.apache.chemistry.opencmis.client.api.CmisObject
import org.apache.chemistry.opencmis.client.api.Document
import org.apache.chemistry.opencmis.client.api.Folder
import org.apache.chemistry.opencmis.client.api.ItemIterable
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId
import org.junit.Before
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * @author Petr Strnad
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class AbstractHomeControllerTest extends AbstractIntegrationTestCase {

    @InjectMocks
    protected HomeController homeController = new HomeController();

    @Mock
    protected AlfrescoService alfrescoService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();

        prepareMockedData();
    }

    @Mock
    private Folder mockedHomeFolder;

    @Mock
    private Folder mockedSubFolder;

    @Mock
    private Document mockedDocument;

    @Mock
    private ItemIterable<CmisObject> mockedHomeChildren;

    @Mock
    private ItemIterable<CmisObject> mockedEmptyChildren;

    protected prepareMockedData() {
        GregorianCalendar homeCreated = new GregorianCalendar(2000, 1, 1);
        GregorianCalendar homeModified = new GregorianCalendar(2000, 1, 2);
        GregorianCalendar subFolderCreated = new GregorianCalendar(2000, 1, 3);
        GregorianCalendar subFolderModified = new GregorianCalendar(2000, 1, 4);
        GregorianCalendar documentCreated = new GregorianCalendar(2000, 1, 5);
        GregorianCalendar documentModified = new GregorianCalendar(2000, 1, 6);

        when(mockedHomeChildren.iterator()).thenReturn(Arrays.asList(mockedSubFolder, mockedDocument).iterator());
        when(mockedEmptyChildren.iterator()).thenReturn(Collections.emptyList().iterator());

        when(mockedHomeFolder.getId()).thenReturn("workspace://SpacesStore/00000000-0000-0000-0000-000000000001");
        when(mockedHomeFolder.getBaseTypeId()).thenReturn(BaseTypeId.CMIS_FOLDER);
        when(mockedHomeFolder.getName()).thenReturn("home");
        when(mockedHomeFolder.getCreationDate()).thenReturn(homeCreated);
        when(mockedHomeFolder.getLastModificationDate()).thenReturn(homeModified);
        when(mockedHomeFolder.getChildren()).thenReturn(mockedHomeChildren);

        when(mockedSubFolder.getId()).thenReturn("workspace://SpacesStore/00000000-0000-0000-0000-000000000002");
        when(mockedSubFolder.getBaseTypeId()).thenReturn(BaseTypeId.CMIS_FOLDER);
        when(mockedSubFolder.getName()).thenReturn("subfolder");
        when(mockedSubFolder.getCreationDate()).thenReturn(subFolderCreated);
        when(mockedSubFolder.getLastModificationDate()).thenReturn(subFolderModified);
        when(mockedSubFolder.getChildren()).thenReturn(mockedEmptyChildren);

        when(mockedDocument.getId()).thenReturn("workspace://SpacesStore/00000000-0000-0000-0000-000000000003");
        when(mockedDocument.getBaseTypeId()).thenReturn(BaseTypeId.CMIS_DOCUMENT);
        when(mockedDocument.getName()).thenReturn("document");
        when(mockedDocument.getContentStreamMimeType()).thenReturn("text/plain");
        when(mockedDocument.getContentStreamLength()).thenReturn(1000L);
        when(mockedDocument.getCreationDate()).thenReturn(documentCreated);
        when(mockedDocument.getLastModificationDate()).thenReturn(documentModified);

        doReturn(mockedHomeFolder).when(alfrescoService).getHomeFolderForCurrentUser();
    }

}
