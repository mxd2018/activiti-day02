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
 * 1.��������ͼ
 * @author Administrator
 *	֧����ʽ���
 */
public class ActivitiTest {
	/*@Rule
	 * ��Ԫ���Բ�֧��˽������private Ҫ��public 
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
	 * �������̷�ʽһ��
	 * 
	 * insert������:
	 * ACT_RE_PROCDEF���̶����
	 * ACT_RE_DEPLOYMENT���̲����
	 * ACT_GE_BYTEARRAY ��̬��Դ��
	 * 
	 */
	@Test
	public void deploymentProcess(){
		/*
		 * CRUD����,����ɾ��ֱ����service����
		 * ����Query,����ѯ����Ҫ�ֶ�����Query�ӿ�
		 */
			
		RepositoryService service = engine.getRepositoryService();
		//RepositoryService service = activitiRule.getRepositoryService();
		
		DeploymentBuilder builder = service.createDeployment();
		
		builder.addClasspathResource("process04.bpmn");
		builder.addClasspathResource("process04.png");
		
		Deployment deployment = builder.deploy();
		System.out.println("�������̵�Id��"+deployment.getId());
		System.out.println("�������̵����ƣ�"+deployment.getName());
		System.out.println("�������̵�ʱ�䣺"+deployment.getDeploymentTime());
		System.out.println("�������̵ķ��룺"+deployment.getCategory());
		
	}
	
	/**
	 * ���̲���ʽ����
	 * ����inputStream��ʽ
	 */
	@Test
	public void deploymentProcess2(){
		
		RepositoryService service = engine.getRepositoryService();
		
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream("process01.bpmn");
		Deployment deploy = service.createDeployment().addInputStream("process01.bpmn", stream).name("�ڶ������̶���").deploy();
		
		System.out.println("id---->"+deploy.getId());
		System.out.println("name---->"+deploy.getName());
		System.out.println("DeploymentTime---->"+deploy.getDeploymentTime());
		System.out.println("Category---->"+deploy.getCategory());
	}
	
	/**
	 * ���̲�����
	 * zipInputStream��ʽ
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("resource")
	@Test
	public void deployProcess3() throws FileNotFoundException{
		RepositoryService service = engine.getRepositoryService();
		
		InputStream is = new FileInputStream("D:\\Java�����׶�\\activiti_day01\\process01.zip");
		
		ZipInputStream zipIs = new ZipInputStream(is);
		
		Deployment deploy = service.createDeployment().addZipInputStream(zipIs).name("���������̶���").deploy();
		
		System.out.println("id---->"+deploy.getId());
		System.out.println("name---->"+deploy.getName());
	}
	
	/**
	 * 2.��ѰProcessDefinition���̶�����Ϣ
	 * ��ʽһ��
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
	 * ��ѰProcessDefinition���̶��巽ʽ��
	 * ͨ��key��ѯ�Ƚ�����,����version����ѯ��������
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
	 * ��Ѱ �ķ�ҳ����
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
	 * 3.��������ʵ��processInstance
	 * ����߳�����
	 * insert����
	 * ACT_HI_TASKINST 
	 * ACT_HI_PROCINST
	 * ACT_HI_ACTINST
	 * ACT_RU_EXECUTION ����ʵ���ķ�֧,��һ��֧ EXECUTION��Id��processInstance��id��ͬ������Ƕ��֧idֵ��ͬ
	 *  ACT_RU_TASK  ��ǰ���˴�������
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
	 * ��������ʵ����ͨ��key,����������µ��߳�ʵ��
	 * ע�⣺user����Ҫ���л�
	 */
	@Test
	public void startProcessInstance2(){
		String key = "myProcess";
		
		RuntimeService runtimeService = engine.getRuntimeService();
		//����Map<key,value>--->${key}
		Map<String, Object> variables = new HashMap<>();
		User user = new User();
		user.setUsername("����");
		
		variables.put("user", user);
		
		runtimeService.startProcessInstanceByKey(key,variables);
		
		//runtimeService.startProcessInstanceById(processDefinitionKey, businessKey);
		//runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
		//runtimeService.startProcessInstanceByKey(key);
	}
	
	/**
	 * ��������ʵ�� ,ͨ����������������ֵ
	 * ע�⣺�������������������ݼ�����ġ������ǡ��Զ���ӡ��
	 */
	@Test
	public void startProcessInstance3(){
		
		String key = "myProcess";
		
		RuntimeService  runtimeService = engine.getRuntimeService();
		
		runtimeService.startProcessInstanceByKey(key);
	}
	
	
	
	/**
	 * 4.�鿴�ҵĴ�������
	 */
	@Test
	public void findPersonalTask(){
		String processDefinitionKey = "myProcess";
		
		TaskService taskService = engine.getTaskService();
		
		//List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).list();
		//taskService.createTaskQuery().deploymentId("").list();
		//taskService.createTaskQuery().processDefinitionId("").list();
		//taskService.createTaskQuery().processInstanceBusinessKey("").list();
		
		List<Task> tasks = taskService.createTaskQuery().taskAssignee("����").list();
		
		for(Task task :tasks){
			System.out.println("id---->"+task.getId());
			System.out.println("name---->"+task.getName());
			System.out.println("Assignee---->"+task.getAssignee());
			System.out.println("ExecutionId---->"+task.getExecutionId());
			System.out.println("ProcessDefinitionId---->"+task.getProcessDefinitionId());
			System.out.println("ProcessInstanceId---->"+task.getProcessInstanceId());
			
			//��ɸ�������
			completePersonalTask(task);
		}	
	}
	/**
	 * 5.��ɸ��˴�������,�ſɽ�����һ��������
	 * ע�⣺������ǲ�����
	 */
	
	public void completePersonalTask(Task task){
		
		TaskService taskService = engine.getTaskService();
		taskService.complete(task.getId());
	}
	
}
