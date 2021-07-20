package io.muzoo.ssc.hw4.config;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigurationLoader {
    // added static method for loading configuration from disk
    // default location is 'config.properties' in the same folder.
    public static ConfigProperties load() {
        String configFilename = "config.properties";
        try(FileInputStream fin = new FileInputStream(configFilename)) {
            Properties prop = new Properties();
            prop.load(fin);
            // get the property value and print it out
            String driverClassName = prop.getProperty("databases.driverClassName");
            String connectionUrl = prop.getProperty("databases.connection");
            String username = prop.getProperty("databases.username");
            String password = prop.getProperty("databases.password");

            return new ConfigProperties.ConfigPropertiesBuilder()
                    .databaseDriverClassName(driverClassName)
                    .databaseConnectionUrl(connectionUrl)
                    .databaseDatabaseUsername(username)
                    .databasePassword(password)
                    .build();

        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        load();
    }
}
