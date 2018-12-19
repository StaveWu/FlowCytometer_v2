package application.channel;

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
//@EnableJpaRepositories(entityManagerFactoryRef = "channelInfoEntityManagerFactory",
//        transactionManagerRef = "channelInfoTransactionManager")
public class ChannelInfosConfig {

//    @Bean
//        PlatformTransactionManager channelInfoTransactionManager() {
//        return new JpaTransactionManager(channelInfoEntityManagerFactory().getObject());
//    }
//
//    @Bean
//    LocalContainerEntityManagerFactoryBean channelInfoEntityManagerFactory() {
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setGenerateDdl(true);
//
//        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
//
//        factoryBean.setDataSource(channelInfoDatasource());
//        factoryBean.setJpaVendorAdapter(vendorAdapter);
//        factoryBean.setPackagesToScan(ChannelInfosConfig.class.getPackage().getName());
//
//        return factoryBean;
//    }
//
//    @Bean
//    DataSource channelInfoDatasource() {
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .setName("channelInfos")
//                .build();
//    }
}
