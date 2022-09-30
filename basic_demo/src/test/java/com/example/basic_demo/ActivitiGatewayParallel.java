package com.example.basic_demo;

import com.example.basic_demo.model.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

/**
 * 并行网关，多个任务同时执行，并且需要任务都执行完后，才能继续执行
 * 在该网关上设置条件是不能生效的
 */
public class ActivitiGatewayParallel {

    @Test
    void testDeployment() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 把 bpmn 保存到数据库
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/parallel_gateway.bpmn20.xml")
                .addClasspathResource("bpmn/parallel_gateway.png")
                .name("并行网关测试")
                .deploy();

        // 部署的相关表格为：act_re_deployment
        // 流程定义的相关表格: act_re_procdef
        // 流程实例的相关表格： act_ru_execution
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
        String key = "parallel_gateway";
        HashMap<String, Object> variables = new HashMap<>();

        // 设置流程变量
        Evection evection = new Evection();
        evection.setNum(4); // 对应 bpm 文件中的 evection.num 变量
        variables.put("evection", evection); // 可以放pojo对象
        // 任务负责人
        variables.put("assignee0", "worker");
        variables.put("assignee1", "技术经理");
        variables.put("assignee2", "项目经理");
        variables.put("assignee3", "总经理");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, variables);

        System.out.println("流程定义id:" + processInstance.getProcessDefinitionId());
        System.out.println("流程定义名称:" + processInstance.getProcessDefinitionName()); // 并行网关测试
        System.out.println("流程实例id:" + processInstance.getId());
        System.out.println("流程实例名称:" + processInstance.getName()); // null
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
                .processDefinitionKey("parallel_gateway")
//                .processInstanceId("15001") // 可以根据实例id来查找
//                .taskAssignee("王经理")
                .list();
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
                .processDefinitionKey("parallel_gateway")
//                .processInstanceId("15001") // 可以根据实例id来查找
                .taskAssignee("项目经理")
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常

        if (task != null){
            taskService.complete(task.getId());
        }
    }
}
