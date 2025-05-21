package utils;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariCputil {
	
    // 建立單一連線池
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        // 連線到資料庫 DatabaseName 要改
        config.setJdbcUrl("jdbc:sqlserver://localhost:1433;DatabaseName=movie1;encrypt=false");
        config.setUsername("ryan");
        config.setPassword("9426");
        
        dataSource = new HikariDataSource(config);
    }

    // 全程共用同一個 DataSource，且不會重複 new 
    public static DataSource getDataSource() {
        return dataSource;
    }

    // 用完會自動歸還
    public static void shutdown() {
        if (!dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
