package com.example.activiti_demo;

import com.example.activiti_demo.model.Evection;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class TestVariables1 {
    /**
     * 启动流程实例
     */
    @Test
    void testStartProcess() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String key = "evection_global";
        HashMap<String, Object> variables = new HashMap<>();

        // 设置流程变量
        Evection evection = new Evection();
        evection.setNum(3); // 对应 bpm 文件中的 evection.num 变量
        variables.put("evection", evection); // 可以放pojo对象
        // 任务负责人
        variables.put("assignee0", "李四1");
        variables.put("assignee1", "王经理1");
        variables.put("assignee2", "杨总经理1");
        variables.put("assignee3", "张财务1");
        runtimeService.startProcessInstanceByKey(key, variables);
    }

    @Test
    void testFinishTask() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        // 根据流程 key 和任务负责人查询任务
        // 返回一个一个任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("evection_global")
//                .processInstanceId("15001") // 可以根据实例id来查找
                .taskAssignee("张财务1")
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常

        if (task != null){
            taskService.complete(task.getId());
        }
    }

    /**
     * 根据流程实例id查询该流程实例的全局变量
     */
    @Test
    void testGetInstanceVariablesTask() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        // 根据流程 key 和任务负责人查询任务
        // 返回一个一个任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("evection_global")
//                .processInstanceId("15001") // 可以根据实例id来查找
                .taskAssignee("张财务1")
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常

        if (task != null){
            Map<String, Object> map = processEngine.getRuntimeService().getVariables("47501");// 根据流程实例id查询该流程实例的全局变量
            System.out.println(map);
        }
    }

    /**
     * 根据流程实例id设置全局变量（在整个实例过程中都有效），该实例必须未完成
     */
    @Test
    void testSetGlobalVariableByExecutionId() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        HashMap<String, Object> map = new HashMap<>();
        map.put("extra", "中途添加的值");
        runtimeService.setVariables("47501", map);
    }
}
