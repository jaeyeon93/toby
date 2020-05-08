package toby.service;

import toby.exception.SqlRetrievalFailureException;

public interface SqlService {
    String getSql(String key) throws SqlRetrievalFailureException;
}
