package com.example.activiti_demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.junit.jupiter.api.Test;

public class TestCreateTable {
    /**
     * 相关表格的创建
     */
    @Test
    void testCreateTable() {
        // 创建 activiti 的表结构，需要设置 activiti.cfg.xml 的 databaseSchemaUpdate 属性
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // processEngineConfiguration 为 bean 的 id
//        ProcessEngineConfiguration processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml", "processEngineConfiguration");
        System.out.println(processEngine);
    }
}
