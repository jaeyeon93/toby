package toby.service;

import toby.dao.UserDao;
import toby.exception.SqlRetrievalFailureException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlSqlService implements SqlService {
    private Map<String, String> sqlMap = new HashMap<>();
    private String sqlMapFile;

    public void setSqlMapFile(String sqlMapFile) {
        this.sqlMapFile = sqlMapFile;
    }

    @PostConstruct
    public void localSql() {
        String contextPath = SqlMap.class.getPackage().getName();
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream is = UserDao.class.getResourceAsStream("sqlmap.xml");
            SqlMap sqlMap = (SqlMap)unmarshaller.unmarshal(is);

            for (SqlType sql: sqlMap.getSql())
                sqlMap.put(sql.getKey(), sql.getValue());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

//    @Override
//    public void registerSql(String key, String sql) {
//        sqlMap.put(key, sql);
//    }
//
//    @Override
//    public String findSql(String key) throws SqlNotFoundException {
//        String sql = sqlMap.get(key);
//        if (sql == null) throw new SqlNotFoundException(key+"에 대한 sql을 찾을 수 없습니다.");
//        else return sql;
//    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);
        if (sql == null)
            throw new SqlRetrievalFailureException(key +"에 대한 SQL을 찾을 수 없습니다.");
        else
            return sql;
    }

//    @Override
//    public void read(SqlRegistry sqlRegistry) {
//        return;
//    }
}
