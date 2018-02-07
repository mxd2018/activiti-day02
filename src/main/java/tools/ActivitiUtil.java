package tools;

import java.io.InputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;

//生成ProcessEngine对象
public class ActivitiUtil {
	/**
	 * 方式一：
	 * 默认加载classpath下即（src/main/resources下的）activiti.cfg.xml文件
	 */
	public static ProcessEngine findEngine(){
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		return engine;
	}
	/**
	 * 方式二：
	 * 同样默认加载classpath下即（src/main/resources下的）activiti.cfg.xml文件
	 */
	public static ProcessEngine findEngine2(){
		ProcessEngineConfiguration config = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();
		ProcessEngine engine = config.buildProcessEngine();
		return engine;
	}
	
	/**
	 * 方式三：
	 * 加载指定的activiti.xml文件
	 * activiti.cfg.xml文件可以不放在classpath下，并且文件名也可以更改
	 */
	public static ProcessEngine findEngine3(){
		ProcessEngineConfiguration config = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.xml", "processEngineConfiguration");
		ProcessEngine engine = config.buildProcessEngine();
		return engine;
	}
	
	/**
	 * 方式四：
	 * 加载指定的cfg.xml文件
	 * 使用流的方式读取activiti.xml文件
	 */
	public static ProcessEngine findEngine4(){
		InputStream is = ActivitiUtil.class.getClassLoader().getResourceAsStream("activiti.xml");
		ProcessEngineConfiguration config = ProcessEngineConfiguration.createProcessEngineConfigurationFromInputStream(is);
		ProcessEngine engine = config.buildProcessEngine();
		return engine;
	}
	/**
	 * 不使用activiti.cfg.xml文件方式创建. 使用JAVA代码方式
	 */
	public static ProcessEngine findEngine5(){
		ProcessEngine engine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
		.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
		.setJdbcDriver("com.mysql.jdbc.Driver")
		.setJdbcUrl("jdbc:mysql://localhost:3306/activiti")
		.setJdbcUsername("root")
		.setJdbcPassword("123")
		.setAsyncExecutorEnabled(true)
		.setAsyncExecutorActivate(false)
		.buildProcessEngine();
		
		return engine;
	}
	
	
	
	public static void main(String[] args) {
		System.out.println(findEngine5());
	}
	
	
	
}
