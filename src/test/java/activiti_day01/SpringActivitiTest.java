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
 * classpath*:*代表的是src/main/resources,不加代表的是src/test/resources
 * 注意：因为我们在spring.xml里import导入了spring-activiti.xml文件，因此此处加载spring上下文环境即可，
 * 	        当然spring里不导入activiti文件也行，在次此处spring.xml后面加逗号加上activiti文件即可
 */
@ContextConfiguration("classpath*:spring.xml")
public class SpringActivitiTest {
	@Autowired
	private ProcessEngine engine;
	
	//1.部署流程
	@Test
	public void deploymentProcess(){
		RepositoryService service = engine.getRepositoryService();
		Deployment deploy = service.createDeployment().addClasspathResource("process05.bpmn").addClasspathResource("process05.png").deploy();
	}
	
	//2.0.启动流程实例
	@Test
	public void startProcessInstance(){
		String Key = "myProcess";
		RuntimeService runtimeService = engine.getRuntimeService();
		runtimeService.startProcessInstanceByKey(Key);

	}
	//2.0.1	使用delegate expression方式来完成任务的自动执行
	@Test
	public void startProcessInstance2(){
		String Key = "myProcess";
		RuntimeService runtimeService = engine.getRuntimeService();
		//创建变量
		Map<String, Object> vars = new HashMap<>();
		vars.put("detegare", new Printer());
		
		runtimeService.startProcessInstanceByKey(Key, vars);
		
	}
	
	//3.查看个人待办业务
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

		//完成个人任务
		completePersonTask(task);
		
	}
	
	/**
	 * 5.完成个人待办任务,才可进入下一个任务结点
	 */
	public void completePersonTask(Task task){
		TaskService taskService = engine.getTaskService();
		taskService.complete(task.getId());
	}
	
	//创建组
	@Test
	public void CreateGroup(){
		IdentityService identityService = engine.getIdentityService();
		Group group1 = identityService.newGroup("1001");
		group1.setName("管理组");
		group1.setType("manager");
		Group group2 = identityService.newGroup("1002");
		group2.setName("员工组");
		group2.setType("common");
		
		identityService.saveGroup(group1);
		identityService.saveGroup(group2);
	}
	
	//删除组
	@Test
	public void deleteGroup(){
		IdentityService identityService = engine.getIdentityService();
		identityService.deleteGroup("1001");
		identityService.deleteGroup("1002");
	}
	
	//创建 用户
	@Test
	public void createUser(){
		IdentityService identityService = engine.getIdentityService();
		//注意这里的User是工作流提供的User 不是自定义的User
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
	
	//创建组和用户关心
	@Test
	public void createMembership(){
		IdentityService identityService = engine.getIdentityService();
		//注意：这里的两个参数顺序不要写错了，第一个是UserId 第二个是groupId
		identityService.createMembership("1", "1001");
		identityService.createMembership("2", "1002");
	}

	// 删除组和用户的关系
	@Test
	public void deleteMembership() {
		IdentityService service = engine.getIdentityService();
		service.deleteMembership("1", "1001");
	}
	
	//通过用户查询所在组
	@Test
	public void findGroupByUser(){
		IdentityService identityService = engine.getIdentityService();
		GroupQuery query = identityService.createGroupQuery().groupMember("1");
		Group group = query.singleResult();
		System.out.println(group.getId()+"-------------"+group.getName());
	}
	
	//通过组查询组中成员
	@Test
	public void findUserByGroup(){
		IdentityService identityService = engine.getIdentityService();
		List<User> users = identityService.createUserQuery().memberOfGroup("1001").list();
		
		for(User user:users){
			System.out.println(user.getPassword()+"-------------"+user.getFirstName());
		}
	}
	
	/**
	 * 任务候选人 候选人一般拾取任务,变成执行者,执行完任务后,其他候选人将看不到该任务
	 */
	@Test
	public void selectTaskCandidate(){
		String processDefinitionKey = "myProcess";
		String candidateUser = "jack01";
		
		TaskService taskService = engine.getTaskService();
		//查看候选人可能执行的任务
		List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskCandidateUser(candidateUser).list();
		
		for(Task task:tasks){
			System.out.println("id--->" + task.getId());
			System.out.println("Assignee--->" + task.getAssignee());
			System.out.println("Tenant--->" + task.getTenantId());//Tenant：承租人，佃户
			
			/**
			 * Candidate拾取任务--->任务执行者Assignee; 候选人肯定不能直接完成任务，要先转换成执行者的角色
			 * 第一个参数：要执行的任务
			 * 第二个参数：选择一个候选人转正
			 * 
			 * 
			 * 问题1：act_ru_identitylink表里出现三个候选人是什么意思？？？
			 */
			taskService.claim(task.getId(),candidateUser);
			//候选人转正后完成任务
			taskService.complete(task.getId());
			 
		}
	}
	
	/**
	 * 任务候选组 任务候选组-->组中的任务候选人--claim()执行者--->complete操作
	 */
	@Test
	public void selectTaskByCandidateGroup() {
		String processDefinitionKey = "myProcess";
		//在pgmn流程中还没设Group，此测试仅供参考，如要实现需要些设置好group再设置好user，再将两者建立起关系，具体实现看上文
		String candidateGroup = "管理组01";
		
		TaskService taskService = engine.getTaskService();
		//查看候选人可能执行的任务
		List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskCandidateGroup(candidateGroup).taskCandidateUser("jack02").list();
		for(Task task:tasks){
			System.out.println("id--->" + task.getId());
			System.out.println("Assignee--->" + task.getAssignee());
			System.out.println("Tenant--->" + task.getTenantId());//Tenant：承租人，佃户
			
		taskService.claim(task.getId(),"jack02");
		//候选人转正后完成任务
		taskService.complete(task.getId());
		}
	}
	
	/**
	 * 排他网关的使用
	 * 叉叉---->个人理解：即为只能选一个满足条件的分支，选中的哪一个分支执行完成，流程即结束
	 */
	@Test
	public void testExclusiveGateway(){
		// 1.部署流程
		RepositoryService service = engine.getRepositoryService();
		Deployment deployment = service.createDeployment().addClasspathResource("process06.bpmn").addClasspathResource("process06.png").deploy();
		// 2.启动流程
		String key = "myProcess";
		RuntimeService runtimeService = engine.getRuntimeService();
		
		// 3.创建变量(关键，condition选择的条件在这里给的，和pgmn中的${}对应)
		Map<String, Object> vars = new HashMap<>(); 
		vars.put("days", 10);
		runtimeService.startProcessInstanceByKey(key, vars);

		// 4.查寻任务
		TaskService taskService = engine.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(key).orderByTaskCreateTime().desc().list();

			System.out.println("id--->" + tasks.get(0).getId());
			System.out.println("name-->" + tasks.get(0).getName());
			taskService.complete(tasks.get(0).getId());
	}
	
	/**
	 * 并行网关
	 * + --->当所有审批都通过才能执行下一步操作
	 */
	@Test
	public void testParralleGateway(){
		// 1.部署流程
		RepositoryService service = engine.getRepositoryService();
		Deployment deployment = service.createDeployment().addClasspathResource("process07.bpmn").addClasspathResource("process07.png").deploy();

		// 2.启动流程
		String key = "myProcess";
		RuntimeService runtimeService = engine.getRuntimeService();
		runtimeService.startProcessInstanceByKey(key);
		// 3.查寻任务
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
	 * 包含网关
	 * 圆圈--->符合几个条件就执行几条分支，所有被执行的 分支都完成操作才能进入下一步
	 */
	@Test
	public void testInclusiveGateway() {
		// 1.部署流程
		RepositoryService service = engine.getRepositoryService();
		Deployment deployment = service.createDeployment().addClasspathResource("process08.bpmn").addClasspathResource("process08.png").deploy();
		// 2.启动流程
		String key = "myProcess";
		RuntimeService runtimeService = engine.getRuntimeService();
		Map<String,Object> vars = new HashMap<>();
		vars.put("flag1", 1);
		vars.put("flag2", 1);
		vars.put("flag3", 3);
		runtimeService.startProcessInstanceByKey(key,vars);
		// 查寻任务
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
