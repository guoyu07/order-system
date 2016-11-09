package cn.com.xbed.app.action;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml", "classpath:spring-db.xml", "classpath:spring-mvc.xml", "classpath:spring-mq.xml" })
public class OrderMgntActionTest {
	
	private MockMvc mock;
	@Autowired
	private OrderMgntAction orderAction;
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Before
	public void before(){
	  System.out.println("---------------------------------------------");
	  this.mock = MockMvcBuilders.standaloneSetup(orderAction).build();
	  logger.info("OrderMgntActionTest init....");
	}
	@Test
	public void bookRoomTest() throws Exception{
		System.out.println("==========================");
		MvcResult result = mock.perform(MockMvcRequestBuilders.post("/app/ordmgnt/book")).andReturn();
		logger.info("result"+result);
	}
}
