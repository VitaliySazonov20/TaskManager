package com.PetProject.Vitaliy.TaskManager;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.sql.*;

@Component
@Transactional
public class DatabaseSchemaInitializer {

    private static final String CHECK_TABLE = "users";

    private static final String[] REQUIRED_TABLES = {"users","user_credentials","tasks","task_comments","audit_log"};

    private static final String[] REQUIRED_TYPES = {"task_priority","task_status","user_role"};

    private static final String[] REQUIRED_FUNCTIONS = {"text_to_task_status"};

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DatabaseDataInitializer databaseDataInitializer;


    public void initializeSchema(){
        try (Connection connection = dataSource.getConnection()){
            if(!isSchemaComplete(connection)){
//                String sql = new String(Files.readAllBytes(
//                        new ClassPathResource("schema_dump.sql").getFile().toPath()));
                ClassPathResource resource =new ClassPathResource("schema_dump.sql");
                String sql = new String (resource.getInputStream().readAllBytes());
                try (Statement statement = connection.createStatement()){
                    statement.execute(sql);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private boolean isSchemaComplete(Connection connection) throws SQLException{
        for (String table : REQUIRED_TABLES){
            if(!tableExists(connection,table)){
                return false;
            }
        }
        for (String type : REQUIRED_TYPES){
            if(!typeExists(connection,type)){
                return false;
            }
        }

        for (String function : REQUIRED_FUNCTIONS){
            if(!functionExists(connection,function)){
                return false;
            }
        }
        return true;
    }

    private boolean functionExists(Connection connection, String functionName) throws SQLException {
        String sql = "SELECT 1 FROM pg_proc p " +
                "JOIN pg_namespace n ON p.pronamespace = n.oid " +
                "WHERE n.nspname = 'public' AND p.proname = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, functionName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean tableExists(Connection connection, String name) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();

        try (ResultSet rs = meta.getTables(null,null,name,new String[]{"TABLE"})){
            return rs.next();
        }
    }

    private boolean typeExists(Connection connection, String typeName) throws SQLException {
        String sql = "SELECT 1 FROM pg_type WHERE typname = ? AND typnamespace = " +
                "(SELECT oid FROM pg_namespace WHERE nspname = 'public')";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, typeName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

}
