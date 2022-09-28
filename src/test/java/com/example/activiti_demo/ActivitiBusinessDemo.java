package com.example.activiti_demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;

public class ActivitiBusinessDemo {
    @Test
    void addBusinessKey() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 启动流程时，添加业务key(例如出差申请单的id)，用于关联其他业务信息，长度限制255
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("myLeave1", "1001");
        System.out.println("businessKey:" + instance.getBusinessKey());
    }
}
