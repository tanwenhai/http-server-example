package com.twh.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

@Configuration
@ConditionalOnBean(RestTemplate.class)
public class HttpServerConfiguration implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware, BeanFactoryAware {
    private final static String ANNOTATION_NAME = EnableHttpServer.class.getName();

    private ClassLoader classLoader;
    private ResourceLoader resourceLoader;
    private Environment environment;
    private BeanFactory beanFactory;
    private String[] basePackages;
    private RestTemplate restTemplate;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        // 获取注解
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(ANNOTATION_NAME, true);
        basePackages = (String[])attributes.get("basePackages").toArray();
        restTemplate = beanFactory.getBean(RestTemplate.class);
        registerHttpServer();
    }

    private void registerHttpServer() {
        // 获取类扫描器
        ClassPathScanningCandidateComponentProvider classScanner = getClassScanner();
        classScanner.setResourceLoader(this.resourceLoader);
        AnnotationTypeFilter typeFilter = new AnnotationTypeFilter(HttpServer.class);
        classScanner.addIncludeFilter(typeFilter);

        Set<BeanDefinition> beanDefinitionSet = new HashSet<>();
        // 包扫描
        for (String basePackage : basePackages) {
            beanDefinitionSet.addAll(classScanner.findCandidateComponents(basePackage));
        }

        for (BeanDefinition beanDefinition : beanDefinitionSet) {
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                registerBean((AnnotatedBeanDefinition) beanDefinition);
            }
        }
    }

    /**
     * jdk动态代理类注册ico
     * @param beanDefinition
     */
    private void registerBean(AnnotatedBeanDefinition beanDefinition) {
        String beanName = beanDefinition.getBeanClassName();
        Object proxy = createProxy(beanDefinition);

    }

    private Object createProxy(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        try {
            Class<?> target = Class.forName(metadata.getClassName());
            AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(target, HttpServer.class);

            String path = attributes.getString("path");
            String host = attributes.getString("host");

            Object proxy = Proxy.newProxyInstance(target.getClassLoader(),
                    new Class[]{target},
                    new HttpInvocationHandler(host, path, restTemplate));

            return proxy;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    public ClassPathScanningCandidateComponentProvider getClassScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isInterface()) {
                    // JDK接口代理
                    try {
                        Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(), classLoader);

                        return !target.isAnnotation();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                return false;
            }
        };
    }
}
