package utils;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHandler {
    public static Connection getConnection(ServletContext context) throws UnavailableException
    {
        Connection conn = null;
        try
        {
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if(e instanceof UnavailableException)
                throw new UnavailableException("Can't open database driver");
            if(e instanceof SQLException)
              throw new UnavailableException("Can't connect with database");
        }
        return conn;
    }

    public static void closeConnection(Connection conn) throws SQLException{
        if(conn != null)
        {
            conn.close();
        }
    }
}
