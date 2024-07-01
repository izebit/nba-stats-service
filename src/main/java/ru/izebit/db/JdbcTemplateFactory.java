package ru.izebit.db;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@UtilityClass
public class JdbcTemplateFactory {

    @SneakyThrows
    public static JdbcTemplate create(String url, String database, String username, String password) {
        var dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://" + url + ":3306/" + database);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return new JdbcTemplate(dataSource);
    }
}
