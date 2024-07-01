package ru.izebit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.izebit.db.JdbcTemplateFactory;
import ru.izebit.db.Tables;

abstract class AbstractDatabaseFunction {
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String DB_USERNAME = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_DATABASE = System.getenv("DB_DATABASE");
    protected static final JdbcTemplate JDBC_TEMPLATE = JdbcTemplateFactory.create(DB_URL, DB_DATABASE, DB_USERNAME, DB_PASSWORD);

    protected AbstractDatabaseFunction(String ddlScript) {
        JDBC_TEMPLATE.execute(ddlScript);
    }
}
