package webServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PhonebookDAO;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class IndexHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        var properties = new Properties();
        var dao = new PhonebookDAO(getConnection(properties));
        System.out.println("Handling: " + t.getRequestURI());

        var file = new File("./public/index.html");
//        var response = Files.readAllBytes(Paths.get(file.getPath()));
        var response1 = Files.readString(Paths.get(file.getPath()));
        var person = dao.findAll();
        var builder = new StringBuilder();
        builder.append("<table class = \"person\">");
        builder.append("<tr class = \"row\">");
        builder.append("<td class = \"row\">FirstName</td>");
        builder.append("<td class = \"row\">LastName</td>");
        builder.append("<td class = \"row\">Age</td>");
        builder.append("<td class = \"row\">Phone Number</td>");
        builder.append("<td class = \"row\">Address</td>");
        builder.append("</tr>");
        person.forEach(x -> {
            builder.append("<tr class = \"pow\">");
            builder.append("<td class = \"pow\">" + x.getFirstname() + "</td>");
            builder.append("<td class = \"pow\">" + x.getLastname() + "</td>");
            builder.append("<td class = \"pow\">" + x.getAge() + "</td>");
            builder.append("<td class = \"pow\">" + x.getPhoneNumber() + "</td>");
            builder.append("<td class = \"pow\">" + x.getAddress() + "</td>");
            builder.append("</tr>");
        });
        builder.append("</table>");
        var response = (response1.replace("{{content}}", builder.toString())).getBytes(StandardCharsets.UTF_8);
        t.sendResponseHeaders(200, response.length);
        OutputStream os = t.getResponseBody();
        os.write(response);
        os.close();
    }

    private static Connection getConnection(Properties properties) {
        Connection connection = null;
        try (var in = Files.newInputStream(Paths.get("database.properties"))) {
            properties.load(in);

            connection = DriverManager.getConnection(
                    properties.getProperty("url"),
                    properties.getProperty("username"),
                    properties.getProperty("password"));


        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();

        }
        return connection;
    }
}
