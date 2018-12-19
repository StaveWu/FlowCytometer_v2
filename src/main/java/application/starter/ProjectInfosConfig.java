package application.starter;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

//@Configuration
//@EnableJpaRepositories(entityManagerFactoryRef = "projectInfoEntityManagerFactory",
//        transactionManagerRef = "projectInfoTransactionManager")
public class ProjectInfosConfig {

//    @Bean
//    PlatformTransactionManager projectInfoTransactionManager() {
//        return new JpaTransactionManager(projectInfoEntityManagerFactory().getObject());
//    }
//
//    @Bean
//    LocalContainerEntityManagerFactoryBean projectInfoEntityManagerFactory() {
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setGenerateDdl(true);
//
//        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
//
//        factoryBean.setDataSource(projectInfoDatasource());
//        factoryBean.setJpaVendorAdapter(vendorAdapter);
//        factoryBean.setPackagesToScan(ProjectInfosConfig.class.getPackage().getName());
//
//        return factoryBean;
//    }
//
//    @Bean
//    DataSource projectInfoDatasource() {
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .setName("projectInfos")
//                .build();
//    }
}
