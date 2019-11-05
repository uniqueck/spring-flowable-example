package com.uniqueck;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Slf4j
class FlowableApplicationTests {

	@Autowired
	private EmailService emailService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;


    @Test
    void contextLoads() {

    	String customerId = "1";
		String processInstanceId = this.beginCustomerEnrollment(customerId, "demo@demo.com");
		log.info("process instance id {}", processInstanceId);
		assertNotNull(processInstanceId, "the process instance id should not be null");

		List<Task> tasks = taskService.createTaskQuery()
				.active()
				.taskName("confirm-email-task")
				.includeProcessVariables()
				.processVariableValueEquals("customerId", customerId)
				.list();

		assertEquals(1, tasks.size(), "there should be one outstanding task");


		tasks.forEach(task -> {
			this.taskService.claim(task.getId(), "uniqueck");
			this.taskService.complete(task.getId());
		});

		assertEquals(this.emailService.sends.get("demo@demo.com").get(), 1);

	}


    String beginCustomerEnrollment(String customerId, String email) {
		Map<String, Object> vars = new HashMap<>();
		vars.put("customerId", customerId);
		vars.put("email", email);
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("signup-process", vars);
		return processInstance.getId();
    }


}

