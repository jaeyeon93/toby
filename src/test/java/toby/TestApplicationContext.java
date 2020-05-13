package domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.mail.MailSender;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import toby.dao.UserDao;
import toby.dao.UserDaoJdbc;
import toby.service.DummyMailSender;
import toby.service.UserService;
import toby.service.UserServiceImpl;
import toby.service.UserServiceTest;
import toby.sqlservice.OxmSqlService;
import toby.sqlservice.SqlRegistry;
import toby.sqlservice.SqlService;
import toby.sqlservice.updatable.EmbeddedDbSqlRegistry;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.bind.Unmarshaller;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

@Configuration
@ImportResource(locations = "/test-applicationContext.xml")
public class TestApplicationContext {
    @Autowired
    private SqlService sqlService;

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/test?characterEncoding=UTF-8&useSSL=false");
        dataSource.setUsername("jimmy");
        dataSource.setPassword("12345");

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(dataSource());
        return tm;
    }

    @Bean
    public DummyMailSender mailSender() {
        return new DummyMailSender();
    }

    @Bean
    public UserDao userDao() {
        UserDaoJdbc userDao = new UserDaoJdbc();
        userDao.setDataSource(dataSource());
        userDao.setSqlService(this.sqlService);
        return userDao;
    }

    @Bean
    public UserService testUserService() {
        UserServiceTest.TestUserServiceImpl testUserService = new UserServiceTest.TestUserServiceImpl();
        testUserService.setUserDao(userDao());
        testUserService.setMailSender(mailSender());
        return testUserService;
    }
    //    sql service
    @Bean
    public SqlService sqlService() {
        OxmSqlService sqlService = new OxmSqlService();
        sqlService.setUnmarshaller(unmarshaller());
        sqlService.setSqlRegistry(sqlRegistry());
        return sqlService;
    }

    @Bean
    public SqlRegistry sqlRegistry() {
        EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
        sqlRegistry.setDataSource(dataSource());
        return sqlRegistry();
    }

    @Bean
    public Unmarshaller unmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("toby.user.sqlservice.jaxb");
        return (Unmarshaller) marshaller;
    }

    @Bean
    public DataSource embeddedDatabase() {
        return new EmbeddedDatabaseBuilder()
                .setName("embeddedDatabase")
                .setType(HSQL)
                .addScript("classpath:toby/sqlservice/updatable/sqlRegistrySchema.sql")
                .build();
    }
}
