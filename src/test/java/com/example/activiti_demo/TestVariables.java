package com.example.activiti_demo;

import com.example.activiti_demo.model.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestVariables {
    /**
     * 单个流程部署
     */
    @Test
    void testDeployment() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 把 bpmn 保存到数据库
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/evection_global.bpmn20.xml")
                .addClasspathResource("bpmn/evection_global.png")
                .name("全局变量测试")
                .deploy();

        System.out.println("流程部署id:" + deployment.getId());
        System.out.println("流程部署名称:" + deployment.getName());
    }

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
        evection.setNum(2); // 对应 bpm 文件中的 evection.num 变量
        variables.put("evection", evection); // 可以放pojo对象
        // 任务负责人
        variables.put("assignee0", "李四");
        variables.put("assignee1", "王经理");
        variables.put("assignee2", "杨总经理");
        variables.put("assignee3", "张财务");
        runtimeService.startProcessInstanceByKey(key, variables);
    }

    /**
     * 查看任务情况
     */
    @Test
    void testQueryTask() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        // 根据流程 key 和任务负责人查询任务
        // 返回一个一个任务对象
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("evection_global")
//                .processInstanceId("15001") // 可以根据实例id来查找
//                .taskAssignee("王经理")
                .list();

        ;
        for (Task task: list){
            System.out.println("实例id:" + task.getProcessInstanceId());
            System.out.println("任务id:" + task.getId());
            System.out.println("任务名称:" + task.getName());
            System.out.println("任务负责人:" + task.getAssignee());
            System.out.println("全局变量:" + taskService.getVariables(task.getId()));
            System.out.println("===========");
        }
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
                .taskAssignee("李四")
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常

        if (task != null){
            HashMap<String, Object> map = new HashMap<>();
            map.put("param", "参数1");

//            taskService.complete(task.getId(), map); // 全局变量，在整个流程实例都有效

//            taskService.complete(task.getId(), map, true); // 参数不知道怎么取出来，不要用

//            taskService.complete(task.getId());

//            taskService.setVariablesLocal(task.getId(), map); // 本地变量，只在当前任务有效，下一个任务会失效。能通过 taskService.getVariablesLocal( taskid ) 获取
//            taskService.complete(task.getId());
        }
    }
}
