package com.example.activiti_demo;

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

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

public class ActivitiDemo {
    // 单个流程部署
    @Test
    void testDeployment() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 把 bpmn 保存到数据库
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/dd.bpmn20.xml")
                .addClasspathResource("bpmn/dd.ddId.png") // png,jpg,gif,svg
                .name("请假流程申请部署")
                .deploy();

        System.out.println("流程部署id:" + deployment.getId());
        System.out.println("流程部署名称:" + deployment.getName());
    }

    /**
     * zip压缩文件上传方式部署
     */
    @Test
    void testDeploymentByZip() {
        // bpmn.zip 中，图片文件 leave.myLeave1.png 中的 myLeave，对应资源文件 leave.bpmn20.xml 中的 id
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("bpmn/bpmn.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 把 bpmn 保存到数据库
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("zip部署")
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
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myLeave1");

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
        String assignee = "manager";
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        // 根据流程 key 和任务负责人查询任务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("myLeave1")
                .taskAssignee(assignee)
                .list();

        for (Task task: list){
            System.out.println("流程实例id:" + task.getProcessDefinitionId()); // 指的是 myLeave1 整个流程的实例的 id
            System.out.println("任务id:" + task.getId()); // 指的是 提交请假申请 这一个步骤的 id
            System.out.println("任务负责人:" + task.getAssignee());
            System.out.println("任务名称:" + task.getName());
            System.out.println("业务id:" + task.getBusinessKey()); // 启动流程实例时传入
            Map<String, Object> map = taskService.getVariables(task.getId());
            System.out.println("上次步骤携带的参数:" + map);
        }
    }

    /**
     * worker 完成任务
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
                .processDefinitionKey("myLeave1")
                .taskAssignee("worker")
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", "1");
        paramMap.put("userName", "小明");
        paramMap.put("why", "拉肚子");
        // worker 把任务提交到 manager
        taskService.complete(task.getId(), paramMap);
    }

    // manager 完成任务
    @Test
    void testFinishTask2() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        // 根据流程 key 和任务负责人查询任务
        // 返回一个一个任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("myLeave1")
                .taskAssignee("manager")
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常
        Map<String, Object> map = taskService.getVariables(task.getId());
        System.out.println("上次步骤携带的参数:" + map);
        map.put("result", "通过");
        // manager 把任务提交到 financer
        taskService.complete(task.getId(), map);
    }

    // financer 完成任务
    @Test
    void testFinishTask3() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取 taskService
        TaskService taskService = processEngine.getTaskService();
        // 根据流程 key 和任务负责人查询任务
        // 返回一个一个任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("myLeave1")
                .taskAssignee("financer")
                .singleResult(); // 这里只会查询到一个任务，如果查到有多个任务，会抛出异常
        Map<String, Object> map = taskService.getVariables(task.getId());
        System.out.println("上次步骤携带的参数:" + map);
        map.put("result2", "通过");
        taskService.complete(task.getId(), map);
    }

    @Test
    void testQueryProcessDefinition() {
        // 创建 processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        // 查询当前 key 的所有的流程定义
        List<ProcessDefinition> definitionList = processDefinitionQuery.processDefinitionKey("myLeave1")
                .orderByProcessDefinitionVersion() // 按照版本排序
                .desc()
                .list();
        for (ProcessDefinition processDefinition: definitionList){
            System.out.println("流程定义id:" + processDefinition.getId());
            System.out.println("流程定义name:" + processDefinition.getName());
            System.out.println("流程定义key:" + processDefinition.getKey());
            System.out.println("流程定义version:" + processDefinition.getVersion());
            System.out.println("流程部署id:" + processDefinition.getDeploymentId());
        }
    }

    // 查询当前 key 有哪些流程实例在跑
    @Test
    void testQueryProcessInstance() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("myLeave1")
                .list();

        for (ProcessInstance processInstance: list){
            System.out.println("-------------------");
            System.out.println("流程实例id:" + processInstance.getProcessInstanceId());
            System.out.println("所属流程定义id:" + processInstance.getProcessDefinitionId());
            System.out.println("是否执行完成:" + processInstance.isEnded());
            System.out.println("是否暂停:" + processInstance.isSuspended());
            System.out.println("当前活动标识:" + processInstance.getActivityId());
            System.out.println("业务关键字:" + processInstance.getBusinessKey());
            System.out.println("-------------------");
        }
    }

    @Test
    void testDeleteDeployment() {
        // 流程部署id
        String deploymentId = "1";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 删除流程定义，如果该流程定义有流程实例启动，则删除时出错
        repositoryService.deleteDeployment(deploymentId);
        // 设置true，则级联删除流程定义，即使该流程定义有流程实例启动，也能删除。
//        repositoryService.deleteDeployment(deploymentId, true);
    }

    // 资源文件下载
    @Test
    void testQueryBpmnFile() throws IOException {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myLeave1")
                .singleResult();
        // 部署id
        String deploymentId = processDefinition.getDeploymentId();
        InputStream pngInput = repositoryService.getResourceAsStream(deploymentId, processDefinition.getDiagramResourceName());
        InputStream bpmnInput = repositoryService.getResourceAsStream(deploymentId, processDefinition.getResourceName());
        File file_png = new File("/Users/jiangzx/Desktop/dpmn/00/myLeave.png");
        File file_bpmn = new File("/Users/jiangzx/Desktop/dpmn/00/myLeave.bpmn");
        FileOutputStream bpmnOut = new FileOutputStream(file_bpmn);
        FileOutputStream pngOut = new FileOutputStream(file_png);

        // 输入流，输出流的转换
        IOUtils.copy(pngInput, pngOut);
        IOUtils.copy(bpmnInput, bpmnOut);

        pngOut.close();
        bpmnOut.close();
        pngInput.close();
        bpmnInput.close();
    }

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
            System.out.println("------" + bean.getActivityId());
            System.out.println("------" + bean.getActivityName());
            System.out.println("------" + bean.getProcessDefinitionId());
            System.out.println("------" + bean.getProcessInstanceId());
            System.out.println("=======================");
        }
    }
}
