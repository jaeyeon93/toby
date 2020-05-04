package toby.proxy;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

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

}
