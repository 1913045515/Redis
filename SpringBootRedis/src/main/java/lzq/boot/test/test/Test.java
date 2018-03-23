package lzq.boot.test.test;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class Test implements ApplicationListener<ApplicationEvent> {
    public Test() {
        System.out.println("A");
    }

    @PostConstruct
    public void PostConstruct() {
        System.out.println("B");
    }

    @PreDestroy
    public void PreDestroy() {
        System.out.println("C");
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            System.out.println("D");
        } else if (event instanceof ContextClosedEvent) {
            System.out.println("E");
        }
    }

//	public static void main(String[] args) {
//		new Test();
//	}
}
