package activiti_day01;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

import model.User;
import tools.ActivitiUtil;

/**
 * 1.加载流程图
 * @author Administrator
 *	支持链式编程
 */
public class ActivitiTest {
	/*@Rule
	 * 单元测试不支持私有属性private 要用public 
	public ActivitiRule activitiRule = new ActivitiRule();
	@Test
	public void test(){
		
		System.out.println(activitiRule);
	}*/
	
	ProcessEngine engine;
	@Before
	public void init(){
		engine=ActivitiUtil.findEngine();
	}
	
	/**
	 * 部署流程方式一：
	 * 
	 * insert操作表:
	 * ACT_RE_PROCDEF流程定义表
	 * ACT_RE_DEPLOYMENT流程部署表
	 * ACT_GE_BYTEARRAY 静态资源表
	 * 
	 */
	@Test
	public void deploymentProcess(){
		/*
		 * CRUD操作,即增删改直接由service调用
		 * 除了Query,即查询操作要手动创建Query接口
		 */
			
		RepositoryService service = engine.getRepositoryService();
		//RepositoryService service = activitiRule.getRepositoryService();
		
		DeploymentBuilder builder = service.createDeployment();
		
		builder.addClasspathResource("process04.bpmn");
		builder.addClasspathResource("process04.png");
		
		Deployment deployment = builder.deploy();
		System.out.println("部署流程的Id："+deployment.getId());
		System.out.println("部署流程的名称："+deployment.getName());
		System.out.println("部署流程的时间："+deployment.getDeploymentTime());
		System.out.println("部署流程的范畴："+deployment.getCategory());
		
	}
	
	/**
	 * 流程部署方式二：
	 * 加载inputStream方式
	 */
	@Test
	public void deploymentProcess2(){
		
		RepositoryService service = engine.getRepositoryService();
		
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream("process01.bpmn");
		Deployment deploy = service.createDeployment().addInputStream("process01.bpmn", stream).name("第二个流程定义").deploy();
		
		System.out.println("id---->"+deploy.getId());
		System.out.println("name---->"+deploy.getName());
		System.out.println("DeploymentTime---->"+deploy.getDeploymentTime());
		System.out.println("Category---->"+deploy.getCategory());
	}
	
	/**
	 * 流程部署三
	 * zipInputStream方式
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("resource")
	@Test
	public void deployProcess3() throws FileNotFoundException{
		RepositoryService service = engine.getRepositoryService();
		
		InputStream is = new FileInputStream("D:\\Java第三阶段\\activiti_day01\\process01.zip");
		
		ZipInputStream zipIs = new ZipInputStream(is);
		
		Deployment deploy = service.createDeployment().addZipInputStream(zipIs).name("第三个流程定义").deploy();
		
		System.out.println("id---->"+deploy.getId());
		System.out.println("name---->"+deploy.getName());
	}
	
	/**
	 * 2.查寻ProcessDefinition流程定义信息
	 * 方式一：
	 */
	@Test
	public void findProcessDefinition(){
		String deploymentId ="5001";
		
		ProcessEngine engine = ActivitiUtil.findEngine();
		RepositoryService service = engine.getRepositoryService();
		ProcessDefinition processDefinition = service.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
		 
		System.out.println("Id---->"+processDefinition.getId());
		System.out.println("key---->"+processDefinition.getKey());
		System.out.println("name---->"+processDefinition.getName());
		System.out.println("version---->"+processDefinition.getVersion()); 
		
	}
	
	/**
	 * 查寻ProcessDefinition流程定义方式二
	 * 通过key查询比较容易,加上version来查询最新内容
	 */
	@Test
	public void findProcessDefinition2(){
		String key = "myProcess";
		RepositoryService service = engine.getRepositoryService();
		ProcessDefinition processDefinition = service.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
		
		System.out.println("Id---->"+processDefinition.getId());
		System.out.println("key---->"+processDefinition.getKey());
		System.out.println("name---->"+processDefinition.getName());
		System.out.println("version---->"+processDefinition.getVersion());
	}
	
	/**
	 * 查寻 的分页操作
	 */
	@Test
	public void findProcessByPage(){
	
		String key = "myProcess";
		
		RepositoryService service = engine.getRepositoryService();
		
		List<ProcessDefinition>  processDefinitions =service.createProcessDefinitionQuery().processDefinitionKey(key).listPage(0, 10);
		
		for(ProcessDefinition processDefinition:processDefinitions){
			System.out.println("Id---->"+processDefinition.getId());
			System.out.println("key---->"+processDefinition.getKey());
			System.out.println("name---->"+processDefinition.getName());
			System.out.println("version---->"+processDefinition.getVersion());
		}
	}
	
	/**
	 * 3.启动流程实例processInstance
	 * 理解线程启动
	 * insert操作
	 * ACT_HI_TASKINST 
	 * ACT_HI_PROCINST
	 * ACT_HI_ACTINST
	 * ACT_RU_EXECUTION 流程实例的分支,单一分支 EXECUTION的Id和processInstance的id相同，如果是多分支id值不同
	 *  ACT_RU_TASK  当前个人待办任务
	 */
	@Test
	public void startProcessInstance(){
		String processDefinitionId = "myProcess:3:5004";

		RuntimeService runtimeService = engine.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId);
		
		System.out.println("id----->"+processInstance.getId());
		
		System.out.println("ProcessDefinitionId----->"+processInstance.getProcessDefinitionId());
		
		System.out.println("key----->"+processInstance.getProcessDefinitionKey());
		
		System.out.println("name----->"+processInstance.getName());
	}
	
	/**
	 * 启动流程实例，通过key,它会查找最新的线程实例
	 * 注意：user类需要序列化
	 */
	@Test
	public void startProcessInstance2(){
		String key = "myProcess";
		
		RuntimeService runtimeService = engine.getRuntimeService();
		//变量Map<key,value>--->${key}
		Map<String, Object> variables = new HashMap<>();
		User user = new User();
		user.setUsername("张三");
		
		variables.put("user", user);
		
		runtimeService.startProcessInstanceByKey(key,variables);
		
		//runtimeService.startProcessInstanceById(processDefinitionKey, businessKey);
		//runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
		//runtimeService.startProcessInstanceByKey(key);
	}
	
	/**
	 * 启动流程实例 ,通过监听器给变量赋值
	 * 注意：监听器是用来监听“休假申请的”，不是“自动打印”
	 */
	@Test
	public void startProcessInstance3(){
		
		String key = "myProcess";
		
		RuntimeService  runtimeService = engine.getRuntimeService();
		
		runtimeService.startProcessInstanceByKey(key);
	}
	
	
	
	/**
	 * 4.查看我的待办任务
	 */
	@Test
	public void findPersonalTask(){
		String processDefinitionKey = "myProcess";
		
		TaskService taskService = engine.getTaskService();
		
		//List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).list();
		//taskService.createTaskQuery().deploymentId("").list();
		//taskService.createTaskQuery().processDefinitionId("").list();
		//taskService.createTaskQuery().processInstanceBusinessKey("").list();
		
		List<Task> tasks = taskService.createTaskQuery().taskAssignee("李四").list();
		
		for(Task task :tasks){
			System.out.println("id---->"+task.getId());
			System.out.println("name---->"+task.getName());
			System.out.println("Assignee---->"+task.getAssignee());
			System.out.println("ExecutionId---->"+task.getExecutionId());
			System.out.println("ProcessDefinitionId---->"+task.getProcessDefinitionId());
			System.out.println("ProcessInstanceId---->"+task.getProcessInstanceId());
			
			//完成个人任务
			completePersonalTask(task);
		}	
	}
	/**
	 * 5.完成个人待办任务,才可进入下一个任务结点
	 * 注意：这个不是测试类
	 */
	
	public void completePersonalTask(Task task){
		
		TaskService taskService = engine.getTaskService();
		taskService.complete(task.getId());
	}
	
}
