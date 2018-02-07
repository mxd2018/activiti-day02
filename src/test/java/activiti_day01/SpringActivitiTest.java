package activiti_day01;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.ietf.jgss.Oid;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import auto.Printer;

@RunWith(SpringJUnit4ClassRunner.class)
/*
 * classpath*:*�������src/main/resources,���Ӵ������src/test/resources
 * ע�⣺��Ϊ������spring.xml��import������spring-activiti.xml�ļ�����˴˴�����spring�����Ļ������ɣ�
 * 	        ��Ȼspring�ﲻ����activiti�ļ�Ҳ�У��ڴδ˴�spring.xml����Ӷ��ż���activiti�ļ�����
 */
@ContextConfiguration("classpath*:spring.xml")
public class SpringActivitiTest {
	@Autowired
	private ProcessEngine engine;
	
	//1.��������
	@Test
	public void deploymentProcess(){
		RepositoryService service = engine.getRepositoryService();
		Deployment deploy = service.createDeployment().addClasspathResource("process05.bpmn").addClasspathResource("process05.png").deploy();
	}
	
	//2.0.��������ʵ��
	@Test
	public void startProcessInstance(){
		String Key = "myProcess";
		RuntimeService runtimeService = engine.getRuntimeService();
		runtimeService.startProcessInstanceByKey(Key);

	}
	//2.0.1	ʹ��delegate expression��ʽ�����������Զ�ִ��
	@Test
	public void startProcessInstance2(){
		String Key = "myProcess";
		RuntimeService runtimeService = engine.getRuntimeService();
		//��������
		Map<String, Object> vars = new HashMap<>();
		vars.put("detegare", new Printer());
		
		runtimeService.startProcessInstanceByKey(Key, vars);
		
	}
	
	//3.�鿴���˴���ҵ��
	@Test
	public void findPersonTask(){
		String processDefinitionKey = "myProcess";
		TaskService taskService = engine.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).orderByTaskCreateTime().desc().list();
		
		Task task = tasks.get(0);
		System.out.println("id---->" + task.getId());
		System.out.println("name---->" + task.getName());
		System.out.println("Assignee---->" + task.getAssignee());
		System.out.println("ExecutionId---->" + task.getExecutionId());
		System.out.println("ProcessDefinitionId---->" + task.getProcessDefinitionId());
		System.out.println("ProcessInstanceId---->" + task.getProcessInstanceId());

		//��ɸ�������
		completePersonTask(task);
		
	}
	
	/**
	 * 5.��ɸ��˴�������,�ſɽ�����һ��������
	 */
	public void completePersonTask(Task task){
		TaskService taskService = engine.getTaskService();
		taskService.complete(task.getId());
	}
	
	//������
	@Test
	public void CreateGroup(){
		IdentityService identityService = engine.getIdentityService();
		Group group1 = identityService.newGroup("1001");
		group1.setName("������");
		group1.setType("manager");
		Group group2 = identityService.newGroup("1002");
		group2.setName("Ա����");
		group2.setType("common");
		
		identityService.saveGroup(group1);
		identityService.saveGroup(group2);
	}
	
	//ɾ����
	@Test
	public void deleteGroup(){
		IdentityService identityService = engine.getIdentityService();
		identityService.deleteGroup("1001");
		identityService.deleteGroup("1002");
	}
	
	//���� �û�
	@Test
	public void createUser(){
		IdentityService identityService = engine.getIdentityService();
		//ע�������User�ǹ������ṩ��User �����Զ����User
		User user = identityService.newUser("1");
		user.setFirstName("jack");
		user.setLastName("jack");
		user.setPassword("123321");
		
		User user2 = identityService.newUser("2");
		user2.setFirstName("rose");
		user2.setLastName("rose");
		user2.setPassword("4567");
		
		identityService.saveUser(user);
		identityService.saveUser(user2);
	}
	
	//��������û�����
	@Test
	public void createMembership(){
		IdentityService identityService = engine.getIdentityService();
		//ע�⣺�������������˳��Ҫд���ˣ���һ����UserId �ڶ�����groupId
		identityService.createMembership("1", "1001");
		identityService.createMembership("2", "1002");
	}

	// ɾ������û��Ĺ�ϵ
	@Test
	public void deleteMembership() {
		IdentityService service = engine.getIdentityService();
		service.deleteMembership("1", "1001");
	}
	
	//ͨ���û���ѯ������
	@Test
	public void findGroupByUser(){
		IdentityService identityService = engine.getIdentityService();
		GroupQuery query = identityService.createGroupQuery().groupMember("1");
		Group group = query.singleResult();
		System.out.println(group.getId()+"-------------"+group.getName());
	}
	
	//ͨ�����ѯ���г�Ա
	@Test
	public void findUserByGroup(){
		IdentityService identityService = engine.getIdentityService();
		List<User> users = identityService.createUserQuery().memberOfGroup("1001").list();
		
		for(User user:users){
			System.out.println(user.getPassword()+"-------------"+user.getFirstName());
		}
	}
	
	/**
	 * �����ѡ�� ��ѡ��һ��ʰȡ����,���ִ����,ִ���������,������ѡ�˽�������������
	 */
	@Test
	public void selectTaskCandidate(){
		String processDefinitionKey = "myProcess";
		String candidateUser = "jack01";
		
		TaskService taskService = engine.getTaskService();
		//�鿴��ѡ�˿���ִ�е�����
		List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskCandidateUser(candidateUser).list();
		
		for(Task task:tasks){
			System.out.println("id--->" + task.getId());
			System.out.println("Assignee--->" + task.getAssignee());
			System.out.println("Tenant--->" + task.getTenantId());//Tenant�������ˣ��軧
			
			/**
			 * Candidateʰȡ����--->����ִ����Assignee; ��ѡ�˿϶�����ֱ���������Ҫ��ת����ִ���ߵĽ�ɫ
			 * ��һ��������Ҫִ�е�����
			 * �ڶ���������ѡ��һ����ѡ��ת��
			 * 
			 * 
			 * ����1��act_ru_identitylink�������������ѡ����ʲô��˼������
			 */
			taskService.claim(task.getId(),candidateUser);
			//��ѡ��ת�����������
			taskService.complete(task.getId());
			 
		}
	}
	
	/**
	 * �����ѡ�� �����ѡ��-->���е������ѡ��--claim()ִ����--->complete����
	 */
	@Test
	public void selectTaskByCandidateGroup() {
		String processDefinitionKey = "myProcess";
		//��pgmn�����л�û��Group���˲��Խ����ο�����Ҫʵ����ҪЩ���ú�group�����ú�user���ٽ����߽������ϵ������ʵ�ֿ�����
		String candidateGroup = "������01";
		
		TaskService taskService = engine.getTaskService();
		//�鿴��ѡ�˿���ִ�е�����
		List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskCandidateGroup(candidateGroup).taskCandidateUser("jack02").list();
		for(Task task:tasks){
			System.out.println("id--->" + task.getId());
			System.out.println("Assignee--->" + task.getAssignee());
			System.out.println("Tenant--->" + task.getTenantId());//Tenant�������ˣ��軧
			
		taskService.claim(task.getId(),"jack02");
		//��ѡ��ת�����������
		taskService.complete(task.getId());
		}
	}
	
	/**
	 * �������ص�ʹ��
	 * ���---->������⣺��Ϊֻ��ѡһ�����������ķ�֧��ѡ�е���һ����ִ֧����ɣ����̼�����
	 */
	@Test
	public void testExclusiveGateway(){
		// 1.��������
		RepositoryService service = engine.getRepositoryService();
		Deployment deployment = service.createDeployment().addClasspathResource("process06.bpmn").addClasspathResource("process06.png").deploy();
		// 2.��������
		String key = "myProcess";
		RuntimeService runtimeService = engine.getRuntimeService();
		
		// 3.��������(�ؼ���conditionѡ���������������ģ���pgmn�е�${}��Ӧ)
		Map<String, Object> vars = new HashMap<>(); 
		vars.put("days", 10);
		runtimeService.startProcessInstanceByKey(key, vars);

		// 4.��Ѱ����
		TaskService taskService = engine.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(key).orderByTaskCreateTime().desc().list();

			System.out.println("id--->" + tasks.get(0).getId());
			System.out.println("name-->" + tasks.get(0).getName());
			taskService.complete(tasks.get(0).getId());
	}
	
	/**
	 * ��������
	 * + --->������������ͨ������ִ����һ������
	 */
	@Test
	public void testParralleGateway(){
		// 1.��������
		RepositoryService service = engine.getRepositoryService();
		Deployment deployment = service.createDeployment().addClasspathResource("process07.bpmn").addClasspathResource("process07.png").deploy();

		// 2.��������
		String key = "myProcess";
		RuntimeService runtimeService = engine.getRuntimeService();
		runtimeService.startProcessInstanceByKey(key);
		// 3.��Ѱ����
		TaskService taskService = engine.getTaskService();
		
		List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(key).orderByTaskCreateTime().desc().list();
		if (!tasks.isEmpty()) {
			Task task = tasks.get(0);
			System.out.println("id--->" + task.getId());
			System.out.println("name-->" + task.getName());
			taskService.complete(task.getId());
		}
	}
	
	/**
	 * ��������
	 * ԲȦ--->���ϼ���������ִ�м�����֧�����б�ִ�е� ��֧����ɲ������ܽ�����һ��
	 */
	@Test
	public void testInclusiveGateway() {
		// 1.��������
		RepositoryService service = engine.getRepositoryService();
		Deployment deployment = service.createDeployment().addClasspathResource("process08.bpmn").addClasspathResource("process08.png").deploy();
		// 2.��������
		String key = "myProcess";
		RuntimeService runtimeService = engine.getRuntimeService();
		Map<String,Object> vars = new HashMap<>();
		vars.put("flag1", 1);
		vars.put("flag2", 1);
		vars.put("flag3", 3);
		runtimeService.startProcessInstanceByKey(key,vars);
		// ��Ѱ����
		TaskService taskService = engine.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(key).orderByTaskCreateTime().desc().list();
		if (!tasks.isEmpty()) {
			Task task = tasks.get(0);
			System.out.println("id--->" + task.getId());
			System.out.println("name-->" + task.getName());
			taskService.complete(task.getId());
		}

	}
}
