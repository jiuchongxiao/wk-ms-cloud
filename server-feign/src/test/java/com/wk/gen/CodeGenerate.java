package com.wk.gen;

import org.apache.maven.model.Model;
import org.junit.Test;

/**
 * 生成swagger代码的工具类方法
 */
public class CodeGenerate {

    /**
     * 生成服务端代码
     */
    @Test
    public void GenerateServerProject() {
        Model model = SwaggerUtils.getPomModel();
        String artifactId = "swagger-api-server";
        String artifactVersion = model.getVersion();
        String groupId = model.getGroupId();
        /*
            1.生成项目同时将生成的代码copy到项目中
        */
        SwaggerUtils.genServer(groupId, artifactId, artifactVersion);
        /*
            2.发布生成项目
            切换到bin/server 目录下执行
            将服务端安装到本地maven仓库  mvn clean install
            将服务端安发布到maven私服    mvn --settings ../settings.xml clean install deploy -DaltDeploymentRepository=snapshots::default::http://bogon:8081/nexus/content/repositories/releases
         */
        /*
            3.依赖生成项目
            手动将如下代码添加到pom中
         */
        System.out.println("\t\t<dependency>\n" +
                "\t\t\t<groupId>" + groupId + "</groupId>\n" +
                "\t\t\t<artifactId>" + artifactId + "</artifactId>\n" +
                "\t\t\t<version>" + artifactVersion + "</version>\n" +
                "\t\t</dependency>");
    }

    /**
     * 生成客户端代码
     */
    @Test
    public void GenerateClientProject() {
        Model model = SwaggerUtils.getPomModel();
        String artifactId = "swagger-api-client";
        String artifactVersion = model.getVersion();
        String groupId = model.getGroupId();
        /*
            1.生成项目
         */
        SwaggerUtils.genClient(groupId, artifactId, artifactVersion);
        /*
            2.发布项目
            切换到bin/client 目录下执行
            将服务端安装到本地maven仓库  mvn clean install
            将服务端安发布到maven私服    mvn --settings ../settings.xml clean install deploy -DaltDeploymentRepository=snapshots::default::http://bogon:8081/nexus/content/repositories/releases
         */
        /*
            3.依赖项目
            手动将如下代码添加到pom中
         */
        System.out.println("\t\t<dependency>\n" +
                "\t\t\t<groupId>" + groupId + "</groupId>\n" +
                "\t\t\t<artifactId>" + artifactId + "</artifactId>\n" +
                "\t\t\t<version>" + artifactVersion + "</version>\n" +
                "\t\t\t<scope>test</scope>\n" +
                "\t\t</dependency>");
    }

}

