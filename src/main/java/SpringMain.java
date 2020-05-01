import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class SpringMain {
    public static void main(String[] args) {
        ApplicationContext context = new GenericXmlApplicationContext("classpath:applicationContext.xml");
        String [] beans = context.getBeanDefinitionNames();
        for (String bean : beans)
            System.out.println(bean);
    }
}
