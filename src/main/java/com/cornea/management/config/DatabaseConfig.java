package com.cornea.management.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    public DataSource dataSource() {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new RuntimeException(
                    "application.properties not found in classpath. " +
                    "Please create src/main/resources/application.properties");
            }
            Properties props = new Properties();
            props.load(in);

            String host = props.getProperty("db.host", "127.0.0.1");
            String port = props.getProperty("db.port", "3306");
            String name = props.getProperty("db.name", "cornea_patient_management");
            String user = props.getProperty("db.user", "root");
            String password = props.getProperty("db.password", "");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(String.format(
                    "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8",
                    host, port, name));
            config.setUsername(user);
            config.setPassword(password);
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.max_pool_size", "10")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.connection_timeout", "30000")));
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            log.info("Database connection pool initialized: {}:{}/{}", host, port, name);
            return new HikariDataSource(config);
        } catch (Exception e) {
            log.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
