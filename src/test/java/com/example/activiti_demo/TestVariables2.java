package com.example.activiti_demo;

import com.example.activiti_demo.model.Evection;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class TestVariables2 {
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
                .taskAssignee("杨总经理")
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常

        if (task != null){
            HashMap<String, Object> map = new HashMap<>();
            // assignee3 会覆盖启动时设置的值
            map.put("assignee3", "roy"); // 杨总经理 处理完后，会发送给 roy
            taskService.complete(task.getId(), map);
        }
    }
}
