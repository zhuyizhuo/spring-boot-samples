package com.github.zhuyizhuo.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel 规则配置类
 * 配置限流规则、信号量隔离和熔断策略
 */
@Configuration
public class SentinelRulesConfig {

    /**
     * 初始化规则
     * 在应用启动时加载
     */
    @PostConstruct
    public void initRules() {
        // 初始化流控规则
        initFlowRules();
        // 初始化降级规则（熔断策略）
        initDegradeRules();
    }

    /**
     * 初始化流控规则，添加信号量隔离配置
     */
    private void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        // 1. 为 getUserInfo 接口配置限流规则
        FlowRule userInfoRule = new FlowRule();
        // 设置资源名
        userInfoRule.setResource("getUserInfo");
        // 设置限流阈值类型（QPS 模式）
        userInfoRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 设置阈值为 5（每秒最多 5 个请求）
        userInfoRule.setCount(5);
        // 设置流控模式（直接）
        userInfoRule.setLimitApp("default");
        rules.add(userInfoRule);

        // 2. 为 getUserOrders 接口配置限流规则
        FlowRule userOrdersRule = new FlowRule();
        userOrdersRule.setResource("getUserOrders");
        userOrdersRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        userOrdersRule.setCount(3); // 每秒最多 3 个请求
        userOrdersRule.setLimitApp("default");
        rules.add(userOrdersRule);

        // 3. 为 createUser 接口配置限流规则
        FlowRule createUserRule = new FlowRule();
        createUserRule.setResource("createUser");
        createUserRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        createUserRule.setCount(2); // 每秒最多 2 个请求
        createUserRule.setLimitApp("default");
        rules.add(createUserRule);

        // 4. 为编程式接口配置限流规则 - 降低阈值更容易观察限流效果
        FlowRule programmaticRule = new FlowRule();
        programmaticRule.setResource("getUserInfoProgrammatic");
        programmaticRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        programmaticRule.setCount(3); // 每秒最多 3 个请求 - 降低阈值以便更容易触发限流
        programmaticRule.setLimitApp("default");
        rules.add(programmaticRule);
        
        // 5. 为上下文限流接口配置规则
        FlowRule contextRule = new FlowRule();
        contextRule.setResource("getUserWithContext");
        contextRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        contextRule.setCount(2); // 每秒最多 2 个请求 - 降低阈值以便更容易触发限流
        contextRule.setLimitApp("default");
        rules.add(contextRule);
        
        // 6. 第三方支付接口 - 信号量隔离
        FlowRule paymentRule = new FlowRule();
        paymentRule.setResource("thirdPartyPayment");
        // 使用信号量隔离模式
        paymentRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 设置阈值
        paymentRule.setCount(20);
        // 设置信号量隔离
        paymentRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(paymentRule);

        // 7. 短信发送接口 - 信号量隔离
        FlowRule smsRule = new FlowRule();
        smsRule.setResource("thirdPartySms");
        smsRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        smsRule.setCount(30);
        smsRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(smsRule);

        // 8. 物流查询接口 - 信号量隔离
        FlowRule logisticsRule = new FlowRule();
        logisticsRule.setResource("thirdPartyLogistics");
        logisticsRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        logisticsRule.setCount(15);
        logisticsRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        rules.add(logisticsRule);

        // 加载规则
        FlowRuleManager.loadRules(rules);
    }

    /**
     * 初始化降级规则，配置熔断策略
     */
    private void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();

        // 1. 第三方支付接口 - 异常比例熔断
        DegradeRule paymentRule = new DegradeRule();
        paymentRule.setResource("thirdPartyPayment");
        // 基于异常比例
        paymentRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        // 异常比例阈值为0.5（50%）
        paymentRule.setCount(0.5);
        // 熔断时长，单位秒
        paymentRule.setTimeWindow(5);
        // 熔断触发的最小请求数
        paymentRule.setMinRequestAmount(10);
        // 统计时长，单位毫秒
        paymentRule.setStatIntervalMs(1000);
        rules.add(paymentRule);

        // 2. 短信发送接口 - 异常比例熔断
        DegradeRule smsRule = new DegradeRule();
        smsRule.setResource("thirdPartySms");
        smsRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        smsRule.setCount(0.6);
        smsRule.setTimeWindow(5);
        smsRule.setMinRequestAmount(8);
        smsRule.setStatIntervalMs(1000);
        rules.add(smsRule);

        // 3. 物流查询接口 - 慢调用比例熔断
        DegradeRule logisticsRule = new DegradeRule();
        logisticsRule.setResource("thirdPartyLogistics");
        // 基于慢调用比例
        logisticsRule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        // 慢调用阈值，超过100ms被认为是慢调用
        logisticsRule.setCount(100);
        // 熔断时长，单位秒
        logisticsRule.setTimeWindow(10);
        // 熔断触发的最小请求数
        logisticsRule.setMinRequestAmount(5);
        // 统计时长，单位毫秒
        logisticsRule.setStatIntervalMs(1000);
        // 慢调用比例阈值，0.3（30%）
        logisticsRule.setSlowRatioThreshold(0.3);
        rules.add(logisticsRule);

        // 加载规则
        DegradeRuleManager.loadRules(rules);
    }
}