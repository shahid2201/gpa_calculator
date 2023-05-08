import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ControlSQL{
    private Connection conn;

    public void connectSQL() {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:gpa_calc.db";
            conn = DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addSem(String sem) throws SQLException {
        connectSQL();
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO Semester(sem) VALUES ('"+sem+"')");
        stmt.executeUpdate("CREATE TABLE "+sem+"(subId INT AUTO_INCREMENT, Class VARCHAR(255), Description VARCHAR(255), Credit VARCHAR(255), Grade VARCHAR(255), PRIMARY KEY(subId));");
    }

    public void addSub(String sem, String classCode, String description, int credit, int grade) throws SQLException {
        connectSQL();
        Statement stmt = conn.createStatement();
        stmt.executeQuery("INSERT INTO "+sem+"(Class, Description, Credit, Grade) VALUES ('"+classCode+"', '"+description+"', "+credit+", "+grade+")");
    }

    public ArrayList<String> getSem() throws SQLException {
        ArrayList<String> sems = new ArrayList<String>();
        connectSQL();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Semester");
        while (rs.next()) {
            sems.add(rs.getString("sem"));
        }
        return sems;
    }

    public ObservableList<SubjectGrade> getSub(String Sem) throws SQLException {
        ObservableList<SubjectGrade> data = FXCollections.observableArrayList();
        connectSQL();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + Sem);
        while (rs.next()) {
            SubjectGrade sg = new SubjectGrade(rs.getString("Class"), rs.getString("Description"), rs.getInt("Credit"), rs.getInt("Grade"));
            data.add(sg);
        }  
        return data;
    }
}