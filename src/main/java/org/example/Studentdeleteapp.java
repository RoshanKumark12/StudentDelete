package org.example;

        import com.amazonaws.services.lambda.runtime.Context;
        import com.amazonaws.services.lambda.runtime.RequestHandler;
        import org.example.entity.Request;
        import org.example.entity.Response;

        import java.sql.*;

/**
 * Insert data using lambda function and aws rds mysql!
 * mvn clean package shade:shade
 * {
 *     "rollNumber" : "1",
 * }
 */
public class Studentdeleteapp implements RequestHandler<Request, Response>
{
    @Override
    public Response handleRequest(Request request, Context context) {
        Response response = new Response();
        try {
            if (getValidate(request, response)){
                deleteData(request, response);
            }
        } catch (SQLException sqlException) {
            response.setMessageId("999");
            response.setMessage("Unable to Delete "+sqlException);
        }
        return response;
    }

    private void deleteData(Request request, Response response) throws SQLException{
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String query = getQuery(request);
        int responseCode = statement.executeUpdate(query);
        if (1 == responseCode){
            response.setMessageId(String.valueOf(responseCode));
            response.setMessage("Successful delete data");
        }
    }

    private Boolean getValidate(Request request, Response response) throws SQLException{
        Boolean status = false;
        String query = "SELECT * FROM `studentINFO` WHERE `studentINFO`.`rollno` = ";
        if (request != null){
            query = query.concat(request.getRollNumber());
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                status = true;
            } else {
                status = false;
                response.setMessageId(String.valueOf(rs));
                response.setMessage("Data not Found");
            }
        } else {
            status = false;
            response.setMessageId("999");
            response.setMessage("Invalid request");
        }
        return status;
    }

    private String getQuery(Request request){
        String deleteQuery = "DELETE FROM `studentINFO` WHERE `studentINFO`.`rollno` = ";
        if (request != null){
            deleteQuery = deleteQuery.concat("'"+request.getRollNumber()+"'");
        }
        return deleteQuery;
    }

    private Connection getConnection() throws SQLException{
        String url = "jdbc:mysql://localhost:3306/studentdatabase";
        String username = "admin";
        String password = "";
        Connection con = DriverManager.getConnection(url,username,password);
        return con;
    }
}