package data_access;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import entity.User;
import entity.UserFactory;
import org.bson.Document;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

/**
 * DAO that stores users in MongoDB, one document per user in the {@value #COLLECTION}
 * collection: {@code {"_id": "mia", "password": "..."}}. Replaces FileUserDataAccessObject.
 */
public class MongoUserDataAccessObject implements SignupUserDataAccessInterface,
                                                  LoginUserDataAccessInterface,
                                                  ChangePasswordUserDataAccessInterface,
                                                  LogoutUserDataAccessInterface {

    public static final String COLLECTION = "users";
    private static final String ID = "_id";
    private static final String PASSWORD = "password";

    private final MongoCollection<Document> users;
    private final UserFactory userFactory;

    /** Who is logged in on this computer; not stored in the database. */
    private String currentUsername;

    public MongoUserDataAccessObject(MongoDatabase database, UserFactory userFactory) {
        this.users = database.getCollection(COLLECTION);
        this.userFactory = userFactory;
    }

    @Override
    public boolean existsByName(String username) {
        return username != null && users.countDocuments(Filters.eq(ID, username)) > 0;
    }

    @Override
    public void save(User user) {
        users.replaceOne(Filters.eq(ID, user.getName()),
                new Document(ID, user.getName()).append(PASSWORD, user.getPassword()),
                new ReplaceOptions().upsert(true));
    }

    @Override
    public User get(String username) {
        final Document document = users.find(Filters.eq(ID, username)).first();
        if (document == null) {
            return null;
        }
        return userFactory.create(document.getString(ID), document.getString(PASSWORD));
    }

    @Override
    public void changePassword(User user) {
        save(user);
    }

    @Override
    public void setCurrentUsername(String name) {
        currentUsername = name;
    }

    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }
}
