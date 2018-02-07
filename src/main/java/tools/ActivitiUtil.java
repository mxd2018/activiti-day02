package tools;

import java.io.InputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;

//����ProcessEngine����
public class ActivitiUtil {
	/**
	 * ��ʽһ��
	 * Ĭ�ϼ���classpath�¼���src/main/resources�µģ�activiti.cfg.xml�ļ�
	 */
	public static ProcessEngine findEngine(){
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		return engine;
	}
	/**
	 * ��ʽ����
	 * ͬ��Ĭ�ϼ���classpath�¼���src/main/resources�µģ�activiti.cfg.xml�ļ�
	 */
	public static ProcessEngine findEngine2(){
		ProcessEngineConfiguration config = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();
		ProcessEngine engine = config.buildProcessEngine();
		return engine;
	}
	
	/**
	 * ��ʽ����
	 * ����ָ����activiti.xml�ļ�
	 * activiti.cfg.xml�ļ����Բ�����classpath�£������ļ���Ҳ���Ը���
	 */
	public static ProcessEngine findEngine3(){
		ProcessEngineConfiguration config = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.xml", "processEngineConfiguration");
		ProcessEngine engine = config.buildProcessEngine();
		return engine;
	}
	
	/**
	 * ��ʽ�ģ�
	 * ����ָ����cfg.xml�ļ�
	 * ʹ�����ķ�ʽ��ȡactiviti.xml�ļ�
	 */
	public static ProcessEngine findEngine4(){
		InputStream is = ActivitiUtil.class.getClassLoader().getResourceAsStream("activiti.xml");
		ProcessEngineConfiguration config = ProcessEngineConfiguration.createProcessEngineConfigurationFromInputStream(is);
		ProcessEngine engine = config.buildProcessEngine();
		return engine;
	}
	/**
	 * ��ʹ��activiti.cfg.xml�ļ���ʽ����. ʹ��JAVA���뷽ʽ
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
