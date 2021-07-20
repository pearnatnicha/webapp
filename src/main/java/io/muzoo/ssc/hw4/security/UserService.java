package io.muzoo.ssc.hw4.security;

import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
public class UserService {
    private static final String INSERT_USER_SQL = "INSERT INTO tbl_user (username, password, display_name) VALUES (?, ?, ?);";
    private static final String SELECT_USER_SQL = "SELECT * FROM tbl_user WHERE username = ?;";
    private static final String SELECT_ALL_USER_SQL = "SELECT * FROM tbl_user;";
    private static final String UPDATE_USER_SQL = "UPDATE tbl_user SET display_name = ? WHERE username = ?;";
    private static final String DELETE_USER_SQL = "DELETE FROM tbl_user WHERE username = ?;";
    private static final String UPDATE_USER_PASSWORD_SQL = "UPDATE tbl_user SET password = ? WHERE username = ?;";


    private static UserService service;

    private DatabaseConnectionService databaseConnectionService;


    public UserService() {
    }

    public static UserService getInstance() {
        if (service == null) {
            service = new UserService();
            service.setDatabaseConnectionService(DatabaseConnectionService.getInstance());
        }
        return service;
    }

    public void setDatabaseConnectionService(DatabaseConnectionService databaseConnectionService){
        this.databaseConnectionService = databaseConnectionService;
    }

    // create new user
    public void createUser(String username, String password, String displayName) throws UserServiceException {
        try (
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(INSERT_USER_SQL);
        ) {
            ps.setString(1, username);
            // Password needs to be hashed and salted.
            ps.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
            ps.setString(3, displayName);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new UsernameNotUniqueException(String.format("Username %s has already been taken.", username));
        } catch (SQLException e) {
            throw new UserServiceException(e.getMessage());
        }
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

//    private Map<String, User> users = new HashMap<>();
//
//    {
//        users.put("pearnatnicha", new User("pearnatnicha", "12345"));
//        users.put("admin", new User("admin", "12345"));
//    }
//
    public User findByUsername(String username) {
        try (
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(SELECT_USER_SQL);
        ) {
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return new User(
                    resultSet.getLong("id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("display_name")
            );
        } catch (SQLException e) {
            return null;
        }
    }
    /**
     * list all users in the database
     * @return list of users, never return null
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try {
            Connection connection = databaseConnectionService.getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_ALL_USER_SQL);
            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()) {
                users.add(
                        new User(
                            resultSet.getLong("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"), // this is hashed password
                            resultSet.getString("display_name")
                        )
                );
            }

        } catch (SQLException throwables){
            throwables.printStackTrace();
        }
        return users;
    }

    // delete user
    public boolean deleteUserByUsername(String username){
        try (
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(DELETE_USER_SQL);
        ) {
            ps.setString(1, username);
            int deleteCount = ps.executeUpdate();
            connection.commit();
            return deleteCount>0;
        } catch (SQLException throwables) {
            return false;
        }
    }

    //update user by user id

    public void updateUserByUsername(String username, String displayName) throws UserServiceException {
        try (
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_USER_SQL);
        ) {
            ps.setString(1, displayName);
            ps.setString(2, username);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new UserServiceException(e.getMessage());
        }
    }

    // Change password is seperated from update user method because user normally
    // never change password and update profile at the same time
    public void changePassword(String username, String newPassword) throws UserServiceException {
        try (
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_USER_PASSWORD_SQL);
        ) {
            ps.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            ps.setString(2, username);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new UserServiceException(e.getMessage());
        }
    }


//    public boolean checkIfUserExists(String username) {
//        return users.containsKey(username);
//    }

//    public static void main(String[] args) throws UserServiceException {
//        UserService userService = new UserService();
//        userService.setDatabaseConnectionService(new DatabaseConnectionService());
//        userService.createUser("pearnatnicha", "12345", "Natnicha");
//    }

    public static void main(String[] args) {
        UserService userService = UserService.getInstance();
        try {
            userService.createUser("admin", "123456", "Admin");
        } catch (UserServiceException e) {
            e.printStackTrace();
        }
//        List<User> users = userService.findAll();
//        for (User user : users){
//            System.out.println(user.getUsername());
//        }

    }

}
