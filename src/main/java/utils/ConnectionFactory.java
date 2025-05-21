package utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class ConnectionFactory {
    // 共用 HikariCP pool
    private static final DataSource ds = HikariCputil.getDataSource();

    // 所有 DB API 都使用這個方法連線
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
