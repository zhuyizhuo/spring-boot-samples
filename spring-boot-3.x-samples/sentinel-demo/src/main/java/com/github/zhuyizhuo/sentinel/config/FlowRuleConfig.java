package com.github.zhuyizhuo.sentinel.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 流控规则配置类
 * 以编程方式配置Sentinel流控规则
 */
@Configuration
public class FlowRuleConfig {

    private static final Logger logger = LoggerFactory.getLogger(FlowRuleConfig.class);

    /**
     * 初始化流控规则
     */
    @PostConstruct
    public void initFlowRules() {
        logger.info("[Sentinel] 开始初始化流控规则...");
        
        List<FlowRule> rules = new ArrayList<>();
        
        // 1. 为helloService配置流控规则 (对应 /api/hello 和 /api/test-limit 接口)
        FlowRule helloServiceRule = createFlowRule("helloService", 5);
        rules.add(helloServiceRule);
        
        // 2. 为demoService配置流控规则 (对应 /api/demo 接口)
        FlowRule demoServiceRule = createFlowRule("demoService", 5);
        rules.add(demoServiceRule);
        
        // 3. 为codingStyleResource配置流控规则 (对应 /api/coding-style 接口)
        FlowRule codingStyleRule = createFlowRule("codingStyleResource", 5);
        rules.add(codingStyleRule);
        
        // 4. 为hotParamService配置流控规则 (对应 /api/hot 接口)
        FlowRule hotParamRule = createFlowRule("hotParamService", 5);
        rules.add(hotParamRule);
        
        // 5. 为systemProtectionService配置流控规则 (对应 /api/system 接口)
        FlowRule systemProtectionRule = createFlowRule("systemProtectionService", 3); // 系统保护接口设置较低阈值
        rules.add(systemProtectionRule);
        
        // 加载规则
        FlowRuleManager.loadRules(rules);
        
        logger.info("[Sentinel] 流控规则初始化完成，已配置 {} 条规则", rules.size());
    }
    
    /**
     * 创建流控规则的辅助方法
     * @param resourceName 资源名称
     * @param count QPS阈值
     * @return 流控规则对象
     */
    private FlowRule createFlowRule(String resourceName, int count) {
        FlowRule rule = new FlowRule();
        rule.setResource(resourceName);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(count);
        rule.setLimitApp("default");
        rule.setStrategy(RuleConstant.STRATEGY_DIRECT);
        rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        return rule;
    }
}