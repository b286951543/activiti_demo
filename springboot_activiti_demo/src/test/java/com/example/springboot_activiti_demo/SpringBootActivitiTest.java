package com.example.springboot_activiti_demo;

import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootActivitiTest {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    /**
     * 启动流程实例
     * 会自动部署 resources/processes 文件夹下的 bpmn
     */
    @Test
    public void testStartProcess() {
        System.out.println(runtimeService);
        // 根据流程定义 id 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave2");

        System.out.println("流程定义id:" + processInstance.getProcessDefinitionId());
        System.out.println("流程定义version:" + processInstance.getProcessDefinitionVersion());
        System.out.println("流程定义实例:" + processInstance.getId());
        System.out.println("当前活动id:" + processInstance.getActivityId());
    }

    @Test
    public void testQueryTask() {
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
    public void testFinishTask() {
        // 根据流程 key 和任务负责人查询任务
        // 返回一个一个任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("leave2")
                .taskAssignee("worker")
//                .taskCandidateUser("李经理") // 使用候选人来执行
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常
        taskService.complete(task.getId());
//        taskService.complete("140002");

        // 校验 taskId 与 任务负责人 是否对应
//        Task task = taskService.createTaskQuery().taskId("").taskAssignee("李经理").singleResult();

//        taskService.setVariablesLocal(task.getId(), paramMap); // 设置local 变量
    }
}
