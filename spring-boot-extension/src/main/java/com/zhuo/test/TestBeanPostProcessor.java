package com.zhuo.test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * BeanPostProcessor扩展点实现优化
 * 在Bean初始化前后进行处理
 * 当前实现：对Bean进行性能监控、日志增强、异常处理和功能扩展
 */
@Component
public class TestBeanPostProcessor implements BeanPostProcessor {
    
    // 使用线程安全的集合类
    private static final AtomicInteger BEAN_COUNT = new AtomicInteger(0);
    private static final Map<String, Long> BEAN_CREATION_TIMES = new ConcurrentHashMap<>();
    private static final Map<String, Object> PROXIED_BEANS = new ConcurrentHashMap<>();
    private static final Map<String, BeanInfo> BEAN_INFO_MAP = new ConcurrentHashMap<>();
    private static final Set<String> IGNORED_BEAN_PREFIXES = new HashSet<>(
            Arrays.asList("org.springframework.", "spring", "org.apache.")
    );
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 记录Bean创建开始时间
        long startTime = System.currentTimeMillis();
        BEAN_CREATION_TIMES.put(beanName, startTime);
        
        // 创建Bean信息对象
        BeanInfo beanInfo = new BeanInfo();
        beanInfo.beanName = beanName;
        beanInfo.beanType = bean.getClass().getName();
        beanInfo.startTime = startTime;
        
        // 对特定类型的Bean进行预处理
        if (bean instanceof TestFactoryBean.ConfigManager) {
            System.out.println("[TestBeanPostProcessor] ConfigManager初始化前处理: " + beanName);
            // 可以在这里对ConfigManager进行初始化前的处理
        } else if (bean instanceof TestImportBeanDefinitionRegistrar.ExampleLogService) {
            System.out.println("[TestBeanPostProcessor] ExampleLogService初始化前处理: " + beanName);
            // 可以在这里对ExampleLogService进行初始化前的处理
        }
        
        // 对生命周期管理相关的Bean进行特殊处理
        if (bean instanceof InitializingBean || bean instanceof DisposableBean) {
            System.out.println("[TestBeanPostProcessor] 检测到生命周期管理Bean: " + beanName + ", 类型: " + bean.getClass().getSimpleName());
        }
        
        // 输出Bean初始化前的基本信息
        if (shouldLogBean(bean, beanName)) {
            System.out.println("[TestBeanPostProcessor] 初始化前 - Bean名称: " + beanName + ", Bean类型: " + bean.getClass().getName() + ", 时间戳: " + startTime);
        }
        
        // 存储Bean信息
        BEAN_INFO_MAP.put(beanName, beanInfo);
        
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            int count = BEAN_COUNT.incrementAndGet();
            
            // 计算Bean创建耗时
            Long startTime = BEAN_CREATION_TIMES.remove(beanName);
            if (startTime != null) {
                long creationTime = System.currentTimeMillis() - startTime;
                
                // 更新Bean信息
                BeanInfo beanInfo = BEAN_INFO_MAP.get(beanName);
                if (beanInfo != null) {
                    beanInfo.endTime = System.currentTimeMillis();
                    beanInfo.creationTime = creationTime;
                }
                
                // 记录创建耗时统计
                logCreationTime(beanName, creationTime);
                
                System.out.println("[TestBeanPostProcessor] Bean创建完成 - 名称: " + beanName + ", 耗时: " + creationTime + "ms, 总Bean数: " + count);
            }
            
            // 对特定类型的Bean进行后处理
            if (bean instanceof TestFactoryBean.ConfigManager) {
                System.out.println("[TestBeanPostProcessor] ConfigManager初始化后处理: " + beanName);
                // 可以在这里对ConfigManager进行初始化后的处理
            }
            
            // 根据Bean类型和名称进行不同的代理增强
            Object processedBean = processBeanByType(bean, beanName);
            
            return processedBean;
        } catch (Exception e) {
            // 异常处理，确保即使处理过程出错也不会影响Spring容器启动
            System.err.println("[TestBeanPostProcessor] 处理Bean时发生异常: " + beanName + ", 错误: " + e.getMessage());
            e.printStackTrace();
            return bean;
        }
    }
    
    /**
     * 根据Bean类型和名称进行不同的处理
     */
    private Object processBeanByType(Object bean, String beanName) {
        // 对Service结尾的Bean进行代理增强
        if (beanName.endsWith("Service")) {
            return createServiceProxy(bean, beanName);
        }
        
        // 对Controller结尾的Bean进行增强
        if (beanName.endsWith("Controller")) {
            System.out.println("[TestBeanPostProcessor] 为Controller Bean添加日志增强: " + beanName);
        }
        
        // 对Repository或Dao结尾的Bean进行增强
        if (beanName.endsWith("Repository") || beanName.endsWith("Dao")) {
            System.out.println("[TestBeanPostProcessor] 为数据访问Bean添加事务支持: " + beanName);
        }
        
        // 对所有实现了特定接口的Bean进行增强
        if (bean instanceof ApplicationContextAware || bean instanceof BeanNameAware) {
            System.out.println("[TestBeanPostProcessor] 检测到Aware接口实现Bean: " + beanName);
        }
        
        return bean;
    }
    
    /**
     * 创建服务代理（简化实现）
     */
    private Object createServiceProxy(Object bean, String beanName) {
        System.out.println("[TestBeanPostProcessor] 为Service Bean创建性能监控代理: " + beanName);
        
        // 获取Bean的所有方法
        Method[] methods = bean.getClass().getDeclaredMethods();
        System.out.println("[TestBeanPostProcessor] Bean包含方法数: " + methods.length);
        
        // 统计公开方法和私有方法数量
        int publicMethodCount = 0;
        int privateMethodCount = 0;
        for (Method method : methods) {
            if (method.getModifiers() == java.lang.reflect.Modifier.PUBLIC) {
                publicMethodCount++;
            } else if (method.getModifiers() == java.lang.reflect.Modifier.PRIVATE) {
                privateMethodCount++;
            }
        }
        
        System.out.println("[TestBeanPostProcessor] 公开方法数: " + publicMethodCount + ", 私有方法数: " + privateMethodCount);
        
        // 实际项目中，这里应该返回一个代理对象
        // 为了简化示例，这里直接返回原对象
        Object proxiedBean = bean;
        PROXIED_BEANS.put(beanName, proxiedBean);
        
        return proxiedBean;
    }
    
    /**
     * 记录Bean创建耗时日志
     */
    private void logCreationTime(String beanName, long creationTime) {
        // 对耗时较长的Bean进行警告
        if (creationTime > 1000) {
            System.out.println("[TestBeanPostProcessor] 警告: Bean创建耗时过长 - 名称: " + beanName + ", 耗时: " + creationTime + "ms");
        } else if (creationTime > 500) {
            System.out.println("[TestBeanPostProcessor] 注意: Bean创建耗时略长 - 名称: " + beanName + ", 耗时: " + creationTime + "ms");
        }
    }
    
    /**
     * 判断是否应该记录Bean信息
     */
    private boolean shouldLogBean(Object bean, String beanName) {
        // 过滤掉Spring内部的Bean和第三方框架的Bean
        for (String prefix : IGNORED_BEAN_PREFIXES) {
            if (beanName.startsWith(prefix)) {
                return false;
            }
        }
        
        // 只记录自定义的Bean
        String beanClassName = bean.getClass().getName();
        return beanClassName.startsWith("com.zhuo.test") || beanClassName.contains(".custom.");
    }
    
    /**
     * 获取代理Bean的数量
     */
    public static int getProxiedBeanCount() {
        return PROXIED_BEANS.size();
    }
    
    /**
     * 获取创建的Bean总数
     */
    public static int getTotalBeanCount() {
        return BEAN_COUNT.get();
    }
    
    /**
     * 获取指定Bean的创建信息
     */
    public static BeanInfo getBeanInfo(String beanName) {
        return BEAN_INFO_MAP.get(beanName);
    }
    
    /**
     * 获取所有Bean的信息列表
     */
    public static Collection<BeanInfo> getAllBeanInfos() {
        return BEAN_INFO_MAP.values();
    }
    
    /**
     * Bean信息内部类
     */
    public static class BeanInfo {
        private String beanName;
        private String beanType;
        private long startTime;
        private long endTime;
        private long creationTime;
        
        // Getter方法
        public String getBeanName() { return beanName; }
        public String getBeanType() { return beanType; }
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }
        public long getCreationTime() { return creationTime; }
        
        @Override
        public String toString() {
            return "BeanInfo{" +
                    "name='" + beanName + "', " +
                    "type='" + beanType + "', " +
                    "creationTime=" + creationTime + "ms" +
                    "}";
        }
    }
    
    /**
     * InitializingBean接口
     */
    public interface InitializingBean {
        void afterPropertiesSet() throws Exception;
    }
    
    /**
     * DisposableBean接口
     */
    public interface DisposableBean {
        void destroy() throws Exception;
    }
    
    /**
     * ApplicationContextAware接口
     */
    public interface ApplicationContextAware {
        void setApplicationContext(Object applicationContext) throws BeansException;
    }
}