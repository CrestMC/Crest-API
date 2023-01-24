package me.blurmit.crestapi.database;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {

    private final HikariDataSource dataSource;
    private final ExecutorService executorService;


    public Database(String host, String username, String password, String database) {
        this.executorService = Executors.newFixedThreadPool(4);

        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" + host + "/" + database + "?useUnicode=yes&characterEncoding=UTF-8");
        dataSource.setPoolName("Basics" + "-" + database);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setLeakDetectionThreshold(60 * 1000L);
        dataSource.addDataSourceProperty("cachePrepStmts" , "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize" , "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit" , "2048");

        try (Connection ignored = dataSource.getConnection()) {
            System.out.println("Connected to database successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to database.");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        dataSource.close();
    }

    public void useConnection(SQLConsumer<Connection> consumer) {
        try (Connection connection = getConnection()) {
            consumer.accept(connection);
        } catch (SQLException e) {
            System.out.println("An error occurred whilst attempting to get database connection");
            e.printStackTrace();
        }
    }

    public void useAsynchronousConnection(SQLConsumer<Connection> consumer) {
        executorService.submit(() -> useConnection(consumer));
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
