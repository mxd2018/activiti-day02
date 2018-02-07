package auto;

import java.io.Serializable;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * 如果使用delegate expression,该自动执行类要实现序列化接口
 * @author Administrator
 *
 */
public class Printer implements JavaDelegate,Serializable{

	@Override
	public void execute(DelegateExecution delegateexecution) throws Exception {
		String id = delegateexecution.getId();
		System.out.println(id+"自动执行打印任务..................");
		
	}
	
}
