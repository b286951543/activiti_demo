package com.example.activiti_demo;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务的候选人测试
 */
public class TestCandidate {
    @Test
    void testDeployment() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 把 bpmn 保存到数据库
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/leave2.bpmn20.xml")
                .addClasspathResource("bpmn/leave2.png") // png,jpg,gif,svg
                .name("请假申请-候选人测试")
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
        // 根据流程定义 id 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave2");

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
                .processDefinitionKey("leave2")
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
                .processDefinitionKey("leave2")
//                .taskAssignee("worker")
                .taskCandidateUser("李经理") // 使用候选人来执行
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常
        taskService.complete(task.getId());
//        taskService.complete("140002");

        // 校验 taskId 与 任务负责人 是否对应
//        Task task = taskService.createTaskQuery().taskId("").taskAssignee("李经理").singleResult();

//        taskService.setVariablesLocal(task.getId(), paramMap); // 设置local 变量
    }

    /**
     * 领取任务，领取后责任人为领取人，其他候选人再不能完成
     * 如果任务已经有任务负责人了，则会报错
     */
    @Test
    void testClaimTask() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();

        taskService.claim("155002", "王经理"); // 把 李经理加到 145002 任务的 任务负责人 及 任务候选人 里面
        // 有了任务负责人后，候选人就不能再去处理了
    }

    /**
     * 退还任务（领取后退还任务）
     */
    @Test
    void testAssigneeToGroupTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .taskId("155002")
                .taskAssignee("小红经理2")
                .singleResult();
        if (task != null){
            // 把负责人设置为 null ，即退还任务。退还后，候选人里的一个 王经理 （可能有多个）也会被删除
            taskService.setAssignee(task.getId(), null);
            System.out.println("任务id：" + task.getId() + " 已归还。");
        }

//        taskService.setAssignee("155002", "小红经理"); // 把 小红经理 加到 155002 任务的 任务负责人 及 任务候选人 里面
    }

    /**
     * 更换任务的负责人
     */
    @Test
    void testAssigneeToGroupTask2() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        taskService.setAssignee("155002", "小红经理2"); // 任务负责人 及 任务候选人 里会更新
        // 例如旧的数据为：任务负责人：王经理。任务候选人：王经理，李经理，王经理
        // 更新后的新数据为：任务负责人：小红经理。任务候选人：王经理，李经理，小红经理
        System.out.println("任务：155002 的负责人已更换");
    }
}
