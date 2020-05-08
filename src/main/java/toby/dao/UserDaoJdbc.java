package toby.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import toby.domain.Level;
import toby.domain.User;
import toby.service.SqlService;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc implements UserDao {
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private JdbcTemplate jdbcTemplate;

    private SqlService sqlService;

    private RowMapper<User> userMapper =
            new RowMapper<User>() {
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setLevel(Level.valueOf(rs.getInt("level")));
                    user.setLogin(rs.getInt("login"));
                    user.setRecommend(rs.getInt("recommend"));
                    return user;
                }
            };

    public void add(User user) {
        this.jdbcTemplate.update(
                this.sqlService.getSql("userAdd"),
                user.getId(), user.getName(), user.getPassword(), user.getEmail(),
                user.getLevel().intValue(), user.getLogin(), user.getRecommend());
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGet"),
                new Object[] {id}, this.userMapper);
    }

    public void deleteAll() {
        this.jdbcTemplate.update(this.sqlService.getSql("userDeleteAll"));
    }

    public int getCount() {
        return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGetCount"), Integer.class);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query(this.sqlService.getSql("userGetAll"),this.userMapper);
    }

    public void update(User user) {
        this.jdbcTemplate.update(
                this.sqlService.getSql("userUpdate"), user.getName(), user.getPassword(), user.getEmail(),
                user.getLevel().intValue(), user.getLogin(), user.getRecommend(),
                user.getId());

    }

    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
    }
}
