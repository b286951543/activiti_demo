package com.example.basic_demo;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

public class ReimburseDemo {
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
                .addClasspathResource("bpmn/reimburse.bpmn20.xml")
                .addClasspathResource("bpmn/reimburse.png") // png,jpg,gif,svg
//                .addClasspathResource("bpmn/evection_global.bpmn20.xml")
//                .addClasspathResource("bpmn/evection_global.png")
                .name("退款申请流程测试")
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
        HashMap<String, Object> map = new HashMap<>();
        map.put("userName", "小红");
        // 根据流程定义 id 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("reimburse", map);

        System.out.println("流程定义id:" + processInstance.getProcessDefinitionId());
        System.out.println("流程定义version:" + processInstance.getProcessDefinitionVersion());
        System.out.println("流程定义实例:" + processInstance.getId());
        System.out.println("当前活动id:" + processInstance.getActivityId());
    }

    /**
     * 查询当前个人的可执行任务
     */
    @Test
    void testFindPersonalTaskList() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        // 根据流程 key 和任务负责人查询任务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("reimburse")
                .taskAssignee("客服")
                .list();

        for (Task task: list){
            System.out.println("流程定义id:" + task.getProcessDefinitionId());
            System.out.println("流程实例id:" + task.getProcessInstanceId()); // 指的是 myLeave1 整个流程的实例的 id
            System.out.println("任务id:" + task.getId()); // 指的是 提交请假申请 这一个步骤的 id
            System.out.println("任务名称:" + task.getName());
            System.out.println("任务负责人:" + task.getAssignee());
            System.out.println("业务id:" + task.getBusinessKey()); // 启动流程实例时传入
            Map<String, Object> map = taskService.getVariables(task.getId());
            System.out.println("上次步骤携带的参数:" + map);
        }
    }

    /**
     * 小红完成任务
     */
    @Test
    void testFinishTask() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        // 根据流程 key 和任务负责人查询任务
        // 返回一个一个任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("reimburse")
                .taskAssignee("小红")
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常
        taskService.complete(task.getId());
    }

    // 客服完成任务
    @Test
    void testFinishTask2() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        // 根据流程 key 和任务负责人查询任务
        // 返回一个一个任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("reimburse")
                .taskAssignee("客服")
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常

        Map<String, Object> map = taskService.getVariables(task.getId());
        int type = 5;
        switch(type){
            case 1: // 同意且金额不超过100
                map.put("kefuTo", 1);
                break;
            case 2: // 同意且金额超过100，发给财务
            case 3: // 不确认，直接发给财务
                map.put("kefuTo", 2);
                break;
            case 4: // 不确定，发给运营
                // 校验之前有没发给运营，没有才发
                map.put("kefuTo", 3);
                break;
            case 5: // 拒绝
                map.put("kefuTo", 0);
                break;
        }
        taskService.complete(task.getId(), map);
    }

    // 运营完成任务
    @Test
    void testFinishTask3() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        // 根据流程 key 和任务负责人查询任务
        // 返回一个一个任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("reimburse")
                .taskAssignee("运营")
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常

        Map<String, Object> map = taskService.getVariables(task.getId());
        map.put("kefuTo", -1);
//        map.remove("kefuTo"); // 不能删除，只能修改
        taskService.complete(task.getId(), map);
    }

    // 财务完成任务
    @Test
    void testFinishTask4() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        // 根据流程 key 和任务负责人查询任务
        // 返回一个一个任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("reimburse")
                .taskAssignee("财务")
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常

        Map<String, Object> map = taskService.getVariables(task.getId());
        int type = 0;
        switch(type){
            case 0: // 拒绝
                Object countObj = map.get("count");
                if (countObj == null){
                    // 第一次拒绝
                    map.put("count", 1);
                }else {
                    // 第二次拒绝
                    map.put("count", 2);
                }

                map.put("caiwuTo", 0);
                break;
            case 1: // 同意
                map.put("caiwuTo", 1);
                break;
            case 2: // 发给技术
                // 校验之前有没发给技术，没有才发
                map.put("caiwuTo", 2);
                break;
        }
        taskService.complete(task.getId(), map);
    }

    // 技术完成任务
    @Test
    void testFinishTask5() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        // 根据流程 key 和任务负责人查询任务
        // 返回一个一个任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("reimburse")
                .taskAssignee("技术")
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常

        Map<String, Object> map = taskService.getVariables(task.getId());
        map.put("caiwuTo", -1);
//        map.remove("caiwuTo"); // 不能删除，只能修改
        taskService.complete(task.getId(), map);
    }

    @Test
    void testQueryProcessInstance() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("reimburse")
                .list();

        for (ProcessInstance processInstance: list){
            System.out.println("-------------------");
            System.out.println("所属流程定义id:" + processInstance.getProcessDefinitionId());
            System.out.println("流程实例id:" + processInstance.getProcessInstanceId());
            System.out.println("是否执行完成:" + processInstance.isEnded());
            System.out.println("是否暂停:" + processInstance.isSuspended());
            System.out.println("当前活动标识:" + processInstance.getActivityId());
            System.out.println("业务关键字:" + processInstance.getBusinessKey());
            System.out.println("-------------------");
        }
    }

    /**
     * 查看历史记录
     */
    @Test
    void testFindHistoryInfo() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        // 获取 actinst 表的查询对象
        HistoricActivityInstanceQuery instanceQuery = historyService.createHistoricActivityInstanceQuery();
//        instanceQuery.processInstanceId("2501");
        instanceQuery.orderByProcessInstanceId().asc().orderByHistoricActivityInstanceStartTime().asc();

        List<HistoricActivityInstance> list = instanceQuery.list();
        for (HistoricActivityInstance bean: list){
            System.out.println("流程定义id:" + bean.getProcessDefinitionId());
            System.out.println("流程实例id:" + bean.getProcessInstanceId());
            System.out.println("任务id:" + bean.getActivityId());
            System.out.println("任务名称:" + bean.getActivityName());
            System.out.println("任务负责人:" + bean.getAssignee());
            System.out.println("=======================");
        }
    }
}
