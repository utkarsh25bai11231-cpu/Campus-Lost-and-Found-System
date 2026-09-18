package campus.lostfound.database;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Properties properties = new Properties();
    private static boolean dbAvailable = false;
    private static boolean checkedConnection = false;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try {
            // First try loading from classpath
            InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties");
            if (is == null) {
                // Fallback to project root
                is = new FileInputStream("db.properties");
            }
            try (InputStream configStream = is) {
                properties.load(configStream);
            }

            // Register driver
            Class.forName(properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (Exception e) {
            // Defaults if file not found
            properties.setProperty("db.url", "jdbc:mysql://localhost:3306/campus_lostfound?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
            properties.setProperty("db.user", "");
            properties.setProperty("db.password", "");
        }
    }

    public static Connection getConnection() {
        try {
            String url = properties.getProperty("db.url");
            String user = getConfigurationValue("db.user", "MYSQL_USER");
            String pass = getConfigurationValue("db.password", "MYSQL_PASSWORD");
            Connection conn = DriverManager.getConnection(url, user, pass);
            if (!checkedConnection) {
                System.out.println("[Database] Connected successfully to MySQL database.");
                dbAvailable = true;
                checkedConnection = true;
            }
            return conn;
        } catch (SQLException e) {
            if (!checkedConnection) {
                System.out.println("[Database Notice] MySQL server is not reachable (" + e.getMessage() + ").");
                System.out.println("[Database Notice] Operating in in-memory fallback mode so all features work seamlessly.");
                dbAvailable = false;
                checkedConnection = true;
            }
            return null;
        }
    }

    private static String getConfigurationValue(String propertyName, String environmentName) {
        String configuredValue = properties.getProperty(propertyName, "").trim();
        if (!configuredValue.isEmpty()) {
            return configuredValue;
        }
        String environmentValue = System.getenv(environmentName);
        return environmentValue == null ? "" : environmentValue.trim();
    }

    public static boolean isDbAvailable() {
        if (!checkedConnection) {
            Connection conn = getConnection();
            close(conn);
        }
        return dbAvailable;
    }

    public static void close(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception ignored) {
            }
        }
    }
}
