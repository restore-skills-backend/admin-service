package com.restore.adminservice.service.Impl;

import com.restore.adminservice.service.SchemaService;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class SchemaServiceImpl extends AppService implements SchemaService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void createSchemaAndTables(String schemaName) throws RestoreSkillsException {
        schemaName = schemaName.toLowerCase();

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        ) {
            // Create the new schema
            jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);

            // Create tables in schema
            database.setDefaultSchemaName(schemaName);
            new Liquibase("db/tenant/master.yaml", new ClassLoaderResourceAccessor(), database).update("");
        } catch (Exception e) {
            throwError(ResponseCode.DB_ERROR, "Failed to create Schema and Tables for tenant : " + schemaName);
        }
    }
}
