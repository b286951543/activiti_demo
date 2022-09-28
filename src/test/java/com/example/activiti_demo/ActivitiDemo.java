package com.example.activiti_demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class ActivitiDemo {
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
     * zip压缩文件上传方式
     */
    @Test
    void testDeploymentByZip() {
        // bpmn.zip 中，图片文件 leave.myLeave1.png 中的 myLeave， 对应资源文件 leave.bpmn20.xml 中的 id
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
}
