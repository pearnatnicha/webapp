package io.muzoo.ssc.hw4.security;

import com.zaxxer.hikari.HikariDataSource;
import io.muzoo.ssc.hw4.config.ConfigProperties;
import io.muzoo.ssc.hw4.config.ConfigurationLoader;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnectionService {

    private static DatabaseConnectionService service;

    private final HikariDataSource ds;


    /**
     * Database connection pool using hikari library.
     * The secret and variables are loaded from disk.
     * The file config.properties is not committed to git repository.
     */

    DatabaseConnectionService(){
        ds = new HikariDataSource();
        ds.setMaximumPoolSize(20);
        ConfigProperties configProperties = ConfigurationLoader.load();
        if (configProperties == null){
            throw new RuntimeException("Unable to read the config.properties.");
        }
        ds.setDriverClassName(configProperties.getDatabaseDriverClassName());
        ds.setJdbcUrl(configProperties.getDatabaseConnectionUrl());
        ds.addDataSourceProperty("user", configProperties.getDatabaseDatabaseUsername());
        ds.addDataSourceProperty("password", configProperties.getDatabasePassword());
        ds.setAutoCommit(false);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static DatabaseConnectionService getInstance() {
        if (service == null) {
            service = new DatabaseConnectionService();
        }
        return service;
    }

//    public static void main(String[] args) {
//        final HikariDataSource ds = new HikariDataSource();
//        ds.setMaximumPoolSize(20);
//        ds.setDriverClassName("org.mariadb.jdbc.Driver");
//        ds.setJdbcUrl("jdbc:mariadb://localhost:3301/login_webapp");
//        ds.addDataSourceProperty("user", "root");
//        ds.addDataSourceProperty("password", "hardpass");
//        ds.setAutoCommit(false);
//
//        try {
//            Connection connection = ds.getConnection();
//            String sql = "INSERT INTO tbl_user (username, password, display_name) VALUES (?, ?, ?);";
//            PreparedStatement ps = connection.prepareStatement(sql);
//            // setting username column 1
//            ps.setString(1, "my_username");
//            // setting password column 1
//            ps.setString(2, "my_password");
//            // setting display name column 1
//            ps.setString(3, "my_display_name");
//            ps.executeUpdate();
//            connection.commit();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
