package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

import com.mysql.jdbc.MysqlErrorNumbers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.User;
import springbook.user.exception.DuplicateUserIdException;

public class UserDaoJdbc implements UserDao {
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void add(final User user) throws DuplicateUserIdException {
		try {
			this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)",
					user.getId(),
					user.getName(),
					user.getPassword());
		} catch (SQLException e) {
			if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY)
				throw new DuplicateUserIdException(e); // 예외전환
			else
				throw new RuntimeException(e); // 예외포장
		}
	}

	public User get(String id) {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?", new Object[]{id},
				this.userMapper
		);
	}

	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
	}

	private RowMapper<User> userMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			return user;
		}
	};

	public void deleteAll() throws SQLException {
		this.jdbcTemplate.update("delete from users");
	}

	public int getCount() throws SQLException  {
		return this.jdbcTemplate.queryForInt("select (*) from users");
	}
}
