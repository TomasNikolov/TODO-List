package database;

import entities.DAO;
import entities.User;
import org.w3c.dom.UserDataHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UserDAO extends DatabaseAccess implements DAO<User> {

    private static final String TABLE_NAME = "users";
    private User user;
    private String username;
    private String password;

    public UserDAO() {}

    public void initialize(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User getUser() throws SQLException {
        if (user == null){
            user = this.get().get(0);
        }
        return user;
    }

    @Override
    synchronized public List<User> get() throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        if (user == null) {
            this.startConnection();

            String strSelect = "SELECT * FROM " + TABLE_NAME + " where username = ? and password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(strSelect);
            System.out.println("[+] The SQL statement is: " + strSelect); // Echo For debugging

            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, this.password);

            ResultSet resultSet = (preparedStatement.execute()) ? preparedStatement.getResultSet() : null;
            if (resultSet != null) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    users.add(new User.UserBuilder(username, password).withId(id).withEmail(email).build());
                }
            }
            this.closeConnection();
        }
        if (users.isEmpty()){
            throw new SQLDataException("[-] No user found");
        }
        if (users.size() > 1){
            throw new SQLDataException("[-] Multiple users found");
        }
        return users;
    }

    @Override
    synchronized public void add(User t) throws SQLException {


        if (!getAllUsernames().contains(t.getUsername())) {
            this.startConnection();
            String strInsert = "INSERT INTO " + DATABASE_NAME + "." + TABLE_NAME + " (username, password, email)" +
                    " values (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(strInsert);

            preparedStatement.setString(1, t.getUsername());
            preparedStatement.setString(2, t.getPassword());
            preparedStatement.setString(3, t.getEmail());
            System.out.println("[+] The SQL statement is: " + strInsert); // Echo For debugging
            ResultSet resultSet = (preparedStatement.execute()) ? preparedStatement.getResultSet() : null;
            System.out.println("[+] User Inserted" + "\n"); // Echo For debugging

            this.closeConnection();
        }
        else{
            throw new SQLDataException("[-] Username already taken");
        }
    }

    synchronized public HashSet<String> getAllUsernames() throws SQLException {
        HashSet<String> usernames = new HashSet<>();

        this.startConnection();

        String strSelect = "SELECT username FROM " + TABLE_NAME;
        PreparedStatement preparedStatement = connection.prepareStatement(strSelect);
        System.out.println("[+] The SQL statement is: " + strSelect); // Echo For debugging

        ResultSet resultSet = (preparedStatement.execute()) ? preparedStatement.getResultSet() : null;
        if (resultSet != null) {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                usernames.add(username);
            }
        }
        this.closeConnection();
        return usernames;
    }

    @Override
    synchronized public void edit(int id, User user) {

    }

    @Override
    synchronized public void remove(int id) {

    }
}