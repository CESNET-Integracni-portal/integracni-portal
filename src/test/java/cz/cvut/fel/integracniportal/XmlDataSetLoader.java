package cz.cvut.fel.integracniportal;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * @author Radek Jezdik
 */
public class XmlDataSetLoader extends AbstractDataSetLoader {

	@Override
	protected IDataSet createDataSet(Resource resource) throws Exception {
		InputStream inputStream = null;
		try {
			inputStream = resource.getInputStream();
			return new XmlDataSet(inputStream);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
}
