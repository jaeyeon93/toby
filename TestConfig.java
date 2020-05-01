//package user;
//
//import dao.UserDao;
//import dao.UserDaoJdbc;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.SimpleDriverDataSource;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class TestConfig {
//    @Bean
//    public DataSource dataSource() {
//        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//
//        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
//        dataSource.setUrl("jdbc:mysql://localhost/test?characterEncoding=UTF-8&useSSL=false");
//        dataSource.setUsername("jimmy");
//        dataSource.setPassword("12345");
//
//        return dataSource;
//    }
//
//    @Bean
//    public UserDaoJdbc userDao() {
//        UserDaoJdbc userDao = new UserDaoJdbc();
//        userDao.setDataSource(dataSource());
//        return userDao;
//    }
//}
