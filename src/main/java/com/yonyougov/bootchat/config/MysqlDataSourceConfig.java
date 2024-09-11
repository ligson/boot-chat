package com.yonyougov.bootchat.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.yonyougov",
        transactionManagerRef = "myDataTransactionManager",
        entityManagerFactoryRef = "myDataDataEntityManagerFactory"
)
public class MysqlDataSourceConfig {
    private final JpaProperties jpaProperties;

    public MysqlDataSourceConfig(JpaProperties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

    @Primary
    @Bean(name = "myDataDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource secondDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "myDataDataEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean myDataDataEntityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("myDataDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource)
                .packages("com.yonyougov")
                .persistenceUnit("boot_chat")
                .properties(jpaProperties.getProperties())
                .build();
    }

    @Primary
    @Bean(name = "myDataTransactionManager")
    public PlatformTransactionManager myDataTransactionManager(@Qualifier("myDataDataEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Primary
    @Bean(name = "myDataJdbcTemplate")
    public JdbcTemplate myDataJdbcTemplate(@Qualifier("myDataDataSource")
                                                  DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
