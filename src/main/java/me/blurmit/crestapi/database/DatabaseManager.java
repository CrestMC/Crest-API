package me.blurmit.crestapi.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DatabaseManager {

    private static Database database;

    private static final String CREATE_API_KEY_TABLE = "CREATE TABLE IF NOT EXISTS `crest_api_keys` (`owner` CHAR(36) NOT NULL PRIMARY KEY, `key` CHAR(36) NOT NULL, `admin` BOOLEAN NOT NULL)";

    private static final String CREATE_API_KEY = "INSERT IGNORE INTO `crest_api_keys` (`owner`, `key`, `admin`) VALUES (?, ?, ?)";
    private static final String DELETE_API_KEY = "DELETE FROM `crest_api_keys` WHERE `key` = ?";
    private static final String SET_ADMIN_KEY = "UPDATE `crest_api_keys` SET `admin` = ? WHERE `owner` = ?";

    private static final String SELECT_API_KEY = "SELECT * FROM `crest_api_keys` WHERE `key` = ?";
    private static final String SELECT_API_KEY_OWNER = "SELECT * FROM `crest_api_keys` WHERE `owner` = ?";

    public DatabaseManager() {
        database = new Database(
                System.getenv("CREST_API_DATABASE_HOST"),
                System.getenv("CREST_API_DATABASE_USERNAME"),
                System.getenv("CREST_API_DATABASE_PASSWORD"),
                System.getenv("CREST_API_DATABASE")
        );

        database.useConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(CREATE_API_KEY_TABLE);
            statement.execute();
        });
    }

    public Database getDatabase() {
        return database;
    }

    public void createAPIKey(String admin_key, String minecraft_uuid) {
        database.useConnection(connection -> {
            if (!isAPIKeyAdmin(admin_key)) {
                return;
            }

            PreparedStatement statement = connection.prepareStatement(CREATE_API_KEY);
            statement.setString(1, minecraft_uuid);
            statement.setString(2, UUID.randomUUID() + "");
            statement.setBoolean(3, false);
            statement.execute();
        });
    }

    public void deleteAPIKey(String admin_key, String key) {
        database.useAsynchronousConnection(connection -> {
            if (!isAPIKeyAdmin(admin_key)) {
                return;
            }

            PreparedStatement statement = connection.prepareStatement(DELETE_API_KEY);
            statement.setString(1, key);
            statement.execute();
        });
    }

    public void setAdminKey(String admin_key, String minecraft_uuid, boolean value) {
        database.useAsynchronousConnection(connection -> {
            if (!isAPIKeyAdmin(admin_key)) {
                return;
            }

            PreparedStatement statement = connection.prepareStatement(SET_ADMIN_KEY);
            statement.setBoolean(1, value);
            statement.setString(2, minecraft_uuid);
            statement.execute();
        });
    }

    public boolean isAPIKeyValid(String key) {
        AtomicBoolean isAPIKeyValid = new AtomicBoolean(false);

        database.useConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(SELECT_API_KEY);
            statement.setString(1, key);

            ResultSet results = statement.executeQuery();
            isAPIKeyValid.set(results.next());
        });

        return isAPIKeyValid.get();
    }

    public boolean isAPIKeyAdmin(String key) {
        AtomicBoolean isAPIKeyAdmin = new AtomicBoolean(false);

        database.useConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(SELECT_API_KEY);
            statement.setString(1, key);

            ResultSet results = statement.executeQuery();
            isAPIKeyAdmin.set(results.next() && results.getBoolean("admin"));
        });

        return isAPIKeyAdmin.get();
    }

    public String getAPIKey(String owner) {
        AtomicReference<String> APIKey = new AtomicReference<>();

        database.useConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(SELECT_API_KEY_OWNER);
            statement.setString(1, owner.trim());

            ResultSet results = statement.executeQuery();
            APIKey.set(results.next() ? results.getString("key") : "null");
        });

        return APIKey.get();
    }

    public String getAPIKeyOwner(String key) {
        AtomicReference<String> APIKeyOwner = new AtomicReference<>();

        database.useConnection(connection -> {
            PreparedStatement statement = connection.prepareStatement(SELECT_API_KEY);
            statement.setString(1, key);

            ResultSet results = statement.executeQuery();
            APIKeyOwner.set(results.next() ? results.getString("owner") : "null");
        });

        return APIKeyOwner.get();
    }

}
