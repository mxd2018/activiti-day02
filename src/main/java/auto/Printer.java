package auto;

import java.io.Serializable;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * ���ʹ��delegate expression,���Զ�ִ����Ҫʵ�����л��ӿ�
 * @author Administrator
 *
 */
public class Printer implements JavaDelegate,Serializable{

	@Override
	public void execute(DelegateExecution delegateexecution) throws Exception {
		String id = delegateexecution.getId();
		System.out.println(id+"�Զ�ִ�д�ӡ����..................");
		
	}
	
}
