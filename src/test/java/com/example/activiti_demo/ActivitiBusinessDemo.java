package com.example.activiti_demo;

import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;

public class ActivitiBusinessDemo {
    /**
     * 启动流程实例时，添加 businessKey
     */
    @Test
    void addBusinessKey() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 启动流程时，添加业务key(可保存业务相关表格的id，或者json数据)，用于关联其他业务信息，长度限制255，对应表格为：act_ru_task
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("myLeave1", "1001");
        System.out.println("businessKey:" + instance.getBusinessKey());
    }

    /**
     * 暂停（挂起）、激活该定义下的全部流程实例
     */
    @Test
    void testSuspendAllProcessInstance() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 获取流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
//                .processDefinitionId("")
                .processDefinitionKey("myLeave1")
                .singleResult();

        // 当前流程定义的实例是否挂起
        boolean suspended = processDefinition.isSuspended();
        // 流程定义id
        String definitionId = processDefinition.getId();
        if (suspended){ // 挂机状态，则改为激活
            repositoryService.activateProcessDefinitionById(definitionId, // 流程定义id
                    true, // 是否激活
                    null); // 激活时间
            System.out.println("流程定义id:" + definitionId + " 已激活");
        }else {
            repositoryService.suspendProcessDefinitionById(definitionId,
                    true, // 是否暂停
                    null); // 暂停的时间
            System.out.println("流程定义id:" + definitionId + " 已暂停");
        }
    }

    /**
     * 挂起、激活单个流程实例
     */
    @Test
    void testSuspendSingleProcessInstance() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("20001")
                .singleResult();

        boolean suspended = instance.isSuspended();
        String instanceId = instance.getId();

        if (suspended){
            // 已经暂停，重新激活
            runtimeService.activateProcessInstanceById(instanceId);
            System.out.println("流程实例id:" + instanceId + " 已激活");
        }else {
            runtimeService.suspendProcessInstanceById(instanceId);
            System.out.println("流程实例id:" + instanceId + " 已暂停");
        }
    }
}