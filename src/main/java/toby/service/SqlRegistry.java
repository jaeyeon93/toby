package toby.service;

import toby.exception.SqlNotFoundException;

public interface SqlRegistry {
    void registerSql(String key, String sql);

    String findSql(String key) throws SqlNotFoundException;
}
