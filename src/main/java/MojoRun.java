import core.Generator;
import core.TableCreator;
import datatable.Table;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Mojo(name = "generate")
public class MojoRun extends AbstractMojo {

    @Parameter(property = "gr.host", defaultValue = "127.0.0.1")
    private String host;
    @Parameter(property = "gr.port", defaultValue = "3306")
    private String port;
    @Parameter(property = "gr.database", required = true)
    private String database;
    @Parameter(property = "gr.user", required = true)
    private String user;
    @Parameter(property = "gr.password", required = true)
    private String password;
    @Parameter(defaultValue = "${project.build.sourceDirectory}", readonly = true, required = true)
    private String sourceDirectory;
    @Parameter(property = "gr.targetPackage", required = true)
    private String targetPackage;
    @Parameter(property = "gr.table", required = true)
    private String table;
    @Parameter(property = "gr.overwrite", required = true)
    private boolean overwrite;


    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("host:" + host);
        getLog().info("port:" + port);
        getLog().info("database:" + database);
        getLog().info("user:" + user);
        getLog().info("password:" + password);
        getLog().info("sourceDirectory:" + sourceDirectory);
        getLog().info("targetPackage:" + targetPackage);
        getLog().info("table:" + table);
        getLog().info("overwrite:" + overwrite);
        try {
            startCreation();
        } catch (SQLException | IOException e) {
            throw new MojoExecutionException("error generating data!", e);
        }
    }

    private Connection createConnection(String host, String schema, String user, String password) throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + host + "/" + schema + "?serverTimezone=UTC", user, password);
    }

    private void startCreation() throws SQLException, IOException {
        Connection connection = null;
        connection = createConnection(host, database, user, password);
        Table tableInfo = TableCreator.getInstance().createTable(connection, table);
        Generator generator = Generator.getInstance();
        generator.create(sourceDirectory, tableInfo, targetPackage, targetPackage, "", "model", "model.vm", overwrite);
        generator.create(sourceDirectory, tableInfo, targetPackage, targetPackage, "Mapper", "mapper", "mapper.vm", overwrite);
        if (connection != null) {
            connection.close();
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }
}