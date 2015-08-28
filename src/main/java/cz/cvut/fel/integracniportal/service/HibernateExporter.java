package cz.cvut.fel.integracniportal.service;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.jdbc.util.FormatStyle;
import org.hibernate.jdbc.util.Formatter;

import java.io.*;

/**
 * @author Radek Jezdik
 */
public class HibernateExporter {

    private String dialect;
    private String entityPackage;

    private boolean generateCreateQueries = true;
    private boolean generateDropQueries = false;

    private Configuration hibernateConfiguration;

    public HibernateExporter(String dialect, String entityPackage) {
        this.dialect = dialect;
        this.entityPackage = entityPackage;

        hibernateConfiguration = createHibernateConfig();
    }

    public void export(OutputStream out, boolean generateCreateQueries, boolean generateDropQueries) {

        Dialect hibDialect = Dialect.getDialect(hibernateConfiguration.getProperties());
        PrintWriter writer = new PrintWriter(out);

            if (generateCreateQueries) {
                String[] createSQL = hibernateConfiguration.generateSchemaCreationScript(hibDialect);
                write(writer, createSQL, FormatStyle.DDL.getFormatter());
            }
            if (generateDropQueries) {
                String[] dropSQL = hibernateConfiguration.generateDropSchemaScript(hibDialect);
                write(writer, dropSQL, FormatStyle.DDL.getFormatter());
            }
        writer.flush();
        writer.close();
    }

    public void export(File exportFile) throws FileNotFoundException {

        export(new FileOutputStream(exportFile), generateCreateQueries, generateDropQueries);
    }

    public void exportToConsole() {

        export(System.out, generateCreateQueries, generateDropQueries);
    }

    private void write(PrintWriter writer, String[] lines, Formatter formatter) {

        for (String string : lines)
            writer.println(formatter.format(string) + ";");
    }

    private Configuration createHibernateConfig() {

        hibernateConfiguration = new Configuration();

            hibernateConfiguration.addAnnotatedClass(cz.cvut.fel.integracniportal.model.Folder.class);
            hibernateConfiguration.addAnnotatedClass(cz.cvut.fel.integracniportal.model.FileMetadata.class);
            hibernateConfiguration.addAnnotatedClass(cz.cvut.fel.integracniportal.model.Node.class);
            hibernateConfiguration.addAnnotatedClass(cz.cvut.fel.integracniportal.model.Group.class);
            hibernateConfiguration.addAnnotatedClass(cz.cvut.fel.integracniportal.model.Label.class);
            hibernateConfiguration.addAnnotatedClass(cz.cvut.fel.integracniportal.model.OrganizationalUnit.class);
            hibernateConfiguration.addAnnotatedClass(cz.cvut.fel.integracniportal.model.Permission.class);
            hibernateConfiguration.addAnnotatedClass(cz.cvut.fel.integracniportal.model.UserDetails.class);
            hibernateConfiguration.addAnnotatedClass(cz.cvut.fel.integracniportal.model.UserRole.class);
        hibernateConfiguration.setProperty("hibernate.dialect", dialect);
        return hibernateConfiguration;
    }

    public boolean isGenerateDropQueries() {
        return generateDropQueries;
    }

    public void setGenerateDropQueries(boolean generateDropQueries) {
        this.generateDropQueries = generateDropQueries;
    }

    public Configuration getHibernateConfiguration() {
        return hibernateConfiguration;
    }

    public void setHibernateConfiguration(Configuration hibernateConfiguration) {
        this.hibernateConfiguration = hibernateConfiguration;
    }

    public static void main(String[] args) {
      HibernateExporter exporter = new HibernateExporter("org.hibernate.dialect.PostgreSQLDialect", "com.geowarin.model");
//        HibernateExporter exporter = new HibernateExporter("org.hibernate.dialect.MySQL5Dialect", "com.geowarin.model");
        exporter.exportToConsole();
    }
}
