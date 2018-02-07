package listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class UserListener implements TaskListener{
	//delegateTask代理任务
	@Override
	public void notify(DelegateTask delegateTask) {
		delegateTask.setAssignee("李四");
	}

}
