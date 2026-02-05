package main.caballo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbUtil {
    private static final String URL = System.getenv().getOrDefault(
            "DB_URL",
            "jdbc:mysql://avnadmin:AVNS_CKrPgbVfJGrItEt9POo@caballo-mysql-size-mrs.d.aivencloud.com:10451/caballo?ssl-mode=REQUIRED&allowPublicKeyRetrieval=true&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8"
    );
    private static final String USER = System.getenv().getOrDefault("DB_USER", "avnadmin");
    private static final String PASS = System.getenv().getOrDefault("DB_PASS", "AVNS_CKrPgbVfJGrItEt9POo");

    private DbUtil() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
