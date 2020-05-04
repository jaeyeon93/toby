package toby.proxy;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class simpleProxy {

    @Test
    public void target() {
        Hello hello = new HelloTarget();
        assertThat(hello.sayHello("toby"), is("hello toby"));
    }

    @Test
    public void proxy() {
        Hello proxiedHello = new HelloUppercase(new HelloTarget());
        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
    }

    @Test
    public void dynamicProxy() {
        Hello proxyHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(), // 동적으로 생성되는 다이나믹 프록시 클래스 로딩에 사용할 클래스로더
                new Class[] {Hello.class},  // 구현할 인터페이스
                new UppercaseHandler(new HelloTarget())); // 부가기능과 위임코드를 담은 InvocationHandler
    }

    @Test
    public void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvice());
        Hello proxieHello = (Hello)pfBean.getObject();
        assertThat(proxieHello.sayHello("Toby"), is("HELLO TOBY"));
    }

    static class UppercaseAdvice implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            String ret = (String)methodInvocation.proceed();
            return ret.toUpperCase();
        }
    }

    @Test
    public void pointCutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello)pfBean.getObject();

        assertThat(proxiedHello.sayHello("point"), is("HELLO POINT"));
        assertThat(proxiedHello.sayHi("point"), is("HI POINT"));
        assertThat(proxiedHello.sayThankYou("point"), is("thank you point"));
    }
}
