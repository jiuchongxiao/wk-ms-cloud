package com.wk.gen;

import io.swagger.codegen.SwaggerCodegen;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by cc on 17-1-21.
 */
public class SwaggerUtils {
    private static final String jsonConfigPath="src/main/resources/static/swagger.json";
    private static final String generateProjectPath="bin/";
    //获得pom文件配置
    public static Model getPomModel() {
        MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        Model model = null;
        try {
            model = mavenreader.read(new FileReader(new File("pom.xml")));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return model;
    }
    //以下为配置，一般情况下不要修改
    public static void genServer(String groupId, String artifactId, String artifactVersion) {

        //删掉已经存在的server代码
        FileSystemUtils.deleteRecursively(new File(generateProjectPath+File.separator+"/server"));
        //使用swagger生成server代码
        String basePackage = groupId;
        String configPackage = groupId + ".swagger.config";
        String apiPackage = groupId + ".swagger.api";
        String modelPackage = groupId + ".swagger.model";
        SwaggerCodegen.main(new String[]{
                "generate",
                "-i", jsonConfigPath,
                "-l", "spring",
                "-o", "bin/server",
                //"-c", "src/main/resources/server.json",
                "--artifact-id", artifactId,
                "--group-id", groupId,
                "--artifact-version", artifactVersion,
                "--model-package", modelPackage,
                "-D","basePackage="+basePackage+",configPackage="+configPackage+",apiPackage=" + apiPackage
        });
        File from = new File(generateProjectPath+File.separator+"server"+File.separator+"src"+File.separator+"main"+File.separator+"java");
        File to = new File("src"+File.separator+"main"+File.separator+"java");
        try {
            //拷贝生成文件
            copyRecursively(from,to,"Swagger2SpringBoot.java");
            File templateFile = new File(to,groupId.replaceAll("\\.",File.separator)+File.separator+"swagger"+File.separator+"config"+File.separator+"AspectConfig.java");
            generateAspectConfig(groupId,templateFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void genClient(String groupId, String artifactId, String artifactVersion) {
        String basePackage = groupId;
        String configPackage = groupId + ".swagger.config";
        String apiPackage = groupId + ".swagger.api";
        String modelPackage = groupId + ".swagger.model";
        //删掉已经存在的client代码
        FileSystemUtils.deleteRecursively(new File(generateProjectPath+"/client"));
        //使用swagger生成client代码
        SwaggerCodegen.main(new String[]{
                "generate",
                "-i", jsonConfigPath,
                "-l", "java", "--library", "feign",
                "-o", "bin/client",
//                "-c", "src/main/resources/client.json",
                "--artifact-id", artifactId,
                "--group-id", groupId,
                "--artifact-version", artifactVersion,
                "--model-package", modelPackage,
                "-D","basePackage="+basePackage+",configPackage="+configPackage+",apiPackage=" + apiPackage
        });
    }


    //生成aop拦截配置文件
    private static void generateAspectConfig(String groupId,File templateFile){
        //生成aop拦截配置文件用到的模板文件
        String template = "package {{basePackage}}.swagger.config;\n" +
                "\n" +
                "import org.aspectj.lang.ProceedingJoinPoint;\n" +
                "import org.aspectj.lang.Signature;\n" +
                "import org.aspectj.lang.annotation.Around;\n" +
                "import org.aspectj.lang.annotation.Aspect;\n" +
                "//import org.slf4j.Logger;\n" +
                "//import org.slf4j.LoggerFactory;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.context.ConfigurableApplicationContext;\n" +
                "import org.springframework.stereotype.Component;\n" +
                "\n" +
                "import java.lang.reflect.InvocationTargetException;\n" +
                "import java.lang.reflect.Method;\n" +
                "import java.util.HashMap;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "/**\n" +
                " *  拦截{{basePackage}}包中的方法\n" +
                " *  Created by cc on 16-6-6.\n" +
                " */\n" +
                "@Aspect\n" +
                "@Component\n" +
                "public class AspectConfig {\n" +
                "    //private static final Logger logger = LoggerFactory.getLogger(AspectConfig.class);\n" +
                "\n" +
                "    //存放拦截类和实现类的对应关系\n" +
                "    private static final Map<Signature, BeanMethod> cache = new HashMap<>();\n" +
                "\n" +
                "    @Autowired\n" +
                "    private ConfigurableApplicationContext context;\n" +
                "\n" +
                "    /**\n" +
                "     * 拦截所有 org.cc.test 包内类的方法\n" +
                "     *\n" +
                "     * @param jp\n" +
                "     * @return\n" +
                "     * @throws Throwable\n" +
                "     */\n" +
                "    @Around(\"execution(public * {{basePackage}}.swagger.api.*Controller.*(..))\")\n" +
                "    public Object process(ProceedingJoinPoint jp) throws Throwable {\n" +
                "        //logger.debug(\"拦截方法所属的类型:[\" + jp.getSignature().getDeclaringType() + \"]\");\n" +
                "        //logger.debug(\"拦截方法名称:[\" + jp.getSignature().getName() + \"]\");\n" +
                "        //logger.debug(\"拦截方法参数:[\" + jp.getArgs() + \"]\");\n" +
                "        BeanMethod beanMethod = cache.get(jp.getSignature());\n" +
                "        if (beanMethod == null) {\n" +
                "            //logger.debug(\"缓存中取得方法:[\" + jp.getSignature() + \"]\");\n" +
                "            //通过@Service(value=\"com.wk.p3.name.api.PetApi\"),找到拦截方法对应的实现类\n" +
                "            Object bean = context.getBean(jp.getSignature().getDeclaringType().getName());\n" +
                "            //找到拦截方法对应的实现方法method\n" +
                "            Method method = getMethod(bean, jp.getSignature());\n" +
                "            if (bean != null && method != null) {\n" +
                "                //准备走拦截类外部实现的方法\n" +
                "                beanMethod = new BeanMethod(bean, method);\n" +
                "                cache.put(jp.getSignature(), beanMethod);\n" +
                "            } else {\n" +
                "                //logger.debug(\"不走拦截后的外部实现方法:[\" + jp.getSignature() + \"]\");\n" +
                "                //调用拦截类本身的方法\n" +
                "                return jp.proceed(jp.getArgs());\n" +
                "            }\n" +
                "        }\n" +
                "        Object re = beanMethod.invoke(jp.getArgs());\n" +
                "        //logger.debug(\"拦截方法后返回值:[\" + re.toString() + \"]\");\n" +
                "        return re;\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 找到bean中有相同signature的方法\n" +
                "     * @param bean\n" +
                "     * @param signature\n" +
                "     * @return\n" +
                "     */\n" +
                "    private Method getMethod(Object bean, Signature signature) {\n" +
                "        Method[] methods = bean.getClass().getDeclaredMethods();\n" +
                "        for (int i = 0; i < methods.length; i++) {\n" +
                "            //找出方法名同的方法,且参数类型个数一致\n" +
                "            if (signature.getName().equals(methods[i].getName()) //方法名称一致\n" +
                "                    && shortString(methods[i].toString()).equals(shortString(signature.toLongString())) //方法类参数类型一致\n" +
                "                    ) {\n" +
                "                return methods[i];\n" +
                "            }\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 将字符串\n" +
                "     * public org.springframework.http.ResponseEntity com.wk.p3.name.service.PetApi_impl.findPetsByTags(java.util.List) throws com.wk.p3.name.api.NotFoundException\n" +
                "     * 转换为\n" +
                "     * (java.util.List)\n" +
                "     *\n" +
                "     * @return\n" +
                "     */\n" +
                "    private String shortString(String str) {\n" +
                "        int f = str.indexOf(\"(\");\n" +
                "        int t = str.indexOf(\")\");\n" +
                "        if (f > 0 && t > f) {\n" +
                "            return str.substring(f, t);\n" +
                "        } else {\n" +
                "            return \"\";\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private static class BeanMethod {\n" +
                "        Method method = null;\n" +
                "        Object bean = null;\n" +
                "\n" +
                "        public BeanMethod(Object bean, Method method) {\n" +
                "            this.method = method;\n" +
                "            this.bean = bean;\n" +
                "        }\n" +
                "\n" +
                "        private Object invoke(Object[] args) throws InvocationTargetException, IllegalAccessException {\n" +
                "            return method.invoke(bean, args);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "}\n";
        String FileContent = template.replaceAll("\\{\\{basePackage\\}\\}",groupId);
        try {
            FileCopyUtils.copy(FileContent.getBytes(),templateFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //工具类
    private static void copyRecursively(File src, File dest,String excludeFileName) throws IOException {
        Assert.isTrue(src != null && (src.isDirectory() || src.isFile()), "Source File must denote a directory or file");
        Assert.notNull(dest, "Destination File must not be null");
        doCopyRecursively(src, dest,excludeFileName);
    }
    //工具类
    private static void doCopyRecursively(File src, File dest,String excludeFileName) throws IOException {
        Assert.isTrue(src != null && (src.isDirectory() || src.isFile()), "Source File must denote a directory or file");
        Assert.notNull(dest, "Destination File must not be null");
        if(src.isDirectory()) {
            dest.mkdir();
            File[] ex = src.listFiles();
            if(ex == null) {
                throw new IOException("Could not list files in directory: " + src);
            }

            File[] ioex = ex;
            int var4 = ex.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File entry = ioex[var5];
                if(excludeFileName.equals(entry.getName())){

                }else{
                    doCopyRecursively(entry, new File(dest, entry.getName()),excludeFileName);
                }
            }
        } else if(src.isFile()) {
            try {
                dest.createNewFile();
            } catch (IOException var7) {
                IOException var8 = new IOException("Failed to create file: " + dest);
                var8.initCause(var7);
                throw var8;
            }
            FileCopyUtils.copy(src, dest);
        }

    }


}
