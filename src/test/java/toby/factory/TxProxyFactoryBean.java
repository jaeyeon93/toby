package toby.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import toby.service.TransactionHandler;

import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean<Object> {
    private Object target;
    private PlatformTransactionManager transactionManager;
    private String pattern;
    private Class<?> serviceInterface;

    @Override
    public Object getObject() throws Exception {
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern(pattern);
        txHandler.setTarget(target);
        return Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[] {serviceInterface}, txHandler
        );
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
