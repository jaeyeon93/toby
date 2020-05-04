package toby.proxy;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import java.lang.reflect.Method;

public class ReflectionTest {
    @Test
    public void intvokeMethod() throws Exception {
        String name = "spring";

        // length();
        assertThat(name.length(), is(6));

        Method lengMethod = String.class.getMethod("length");
        assertThat((Integer)lengMethod.invoke(name), is(6));

        // charAt
        assertThat(name.charAt(0), is("s"));

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertThat((Character)charAtMethod.invoke(name, 0), is("s"));
    }
}
