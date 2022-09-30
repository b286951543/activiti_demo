package com.example.basic_demo;

import com.example.basic_demo.model.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

/**
 * 任务的候选人测试
 */
public class TestCandidate1 {
    @Test
    void testDeployment() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 把 bpmn 保存到数据库
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/leave1.bpmn20.xml")
                .addClasspathResource("bpmn/leave1.png") // png,jpg,gif,svg
                .name("请假申请-候选人测试1")
                .deploy();

        System.out.println("流程部署id:" + deployment.getId());
        System.out.println("流程部署名称:" + deployment.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    void testStartProcess() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 runtimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        HashMap<String, Object> variables = new HashMap<>();

        variables.put("candidateUsers", "王经理,李经理");

        // 根据流程定义 id 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave1", variables);

        System.out.println("流程定义id:" + processInstance.getProcessDefinitionId());
        System.out.println("流程定义version:" + processInstance.getProcessDefinitionVersion());
        System.out.println("流程定义实例:" + processInstance.getId());
        System.out.println("当前活动id:" + processInstance.getActivityId());
    }

    @Test
    void testQueryTask() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        // 根据流程 key 和任务负责人查询任务
        // 返回一个一个任务对象
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("leave1")
//                .taskCandidateUser("李经理")
//                .processInstanceId("15001") // 可以根据实例id来查找
                .list();
        for (Task task: list){
            System.out.println("实例id:" + task.getProcessInstanceId());
            System.out.println("任务id:" + task.getId());
            System.out.println("任务名称:" + task.getName());
            System.out.println("任务负责人:" + task.getAssignee());
            System.out.println("全局变量:" + taskService.getVariables(task.getId()));
            List<IdentityLink> candidateList = taskService.getIdentityLinksForTask(task.getId());
            for (IdentityLink bean: candidateList){
                System.out.println("任务候选人:" + bean.getUserId());
            }
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
                .processDefinitionKey("leave1")
//                .taskAssignee("worker")
                .taskCandidateUser("李经理") // 使用候选人来执行
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常
        taskService.complete(task.getId());
//        taskService.complete("140002");

        // 校验 taskId 与 任务负责人 是否对应
//        Task task = taskService.createTaskQuery().taskId("").taskAssignee("李经理").singleResult();

//        taskService.setVariablesLocal(task.getId(), paramMap); // 设置local 变量
    }
}
