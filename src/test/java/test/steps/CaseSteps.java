/*
 *
 */
package test.steps;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.Output;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.StackStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class CaseSteps {
	static AmazonCloudFormation stackbuilder;
	static String StanfordNERNameTaggingWebservice;

	@When("^StanfordNERNameTaggingWebservice CFT is launched$")
	public void namedEntityExtraction_CFT_is_launched() throws Throwable {
		System.out.println("Checking if StanfordNERNameTaggingWebservice is launched");
		stackbuilder = new AmazonCloudFormationClient(new InstanceProfileCredentialsProvider());
		DescribeStacksRequest describeRequest = new DescribeStacksRequest();
		describeRequest.setStackName("heavywater-StanfordNERNameTaggingWebservice");
		String stackStatus = "Unknown";
		List<Stack> stacks = stackbuilder.describeStacks(describeRequest).getStacks();
		if (stacks.isEmpty()) {
			stackStatus = "NO_SUCH_STACK";
			assertTrue(false);
		} else {
			for (Stack stack : stacks) {
				if (stack.getStackStatus().equals(StackStatus.CREATE_FAILED.toString())
						|| stack.getStackStatus().equals(StackStatus.ROLLBACK_FAILED.toString())
						|| stack.getStackStatus().equals(StackStatus.DELETE_FAILED.toString())) {
					stackStatus = stack.getStackStatus();
					assertTrue(false);
				} else if (stack.getStackStatus().equals(StackStatus.CREATE_COMPLETE.toString())) {
					stackStatus = stack.getStackStatus();
					assertTrue(true);
				} else {
					stackStatus = stack.getStackStatus();
					assertTrue(false);
				}
			}
		}
		System.out.println(stackStatus);
	}

	@Then("^I get CFT output$")
	public void i_get_CFT_output() throws Throwable {
		System.out.println("Checking for StanfordNERNameTaggingWebservice CFT output");
		DescribeStacksRequest describeRequest = new DescribeStacksRequest();
		describeRequest.setStackName("heavywater-StanfordNERNameTaggingWebservice");
		List<Stack> stacks = stackbuilder.describeStacks(describeRequest).getStacks();
		for (Stack stack : stacks) {
			for (Output out : stack.getOutputs()) {
				if (out.getOutputKey().equals("StanfordNERNameTaggingWebserviceUrl")) {
					StanfordNERNameTaggingWebservice = out.getOutputValue();
				}
			}
		}
		System.out.println(StanfordNERNameTaggingWebservice);
	}

	@Then("^I execute my RestFul service with empty input$")
	public void i_execute_my_RestFul_service_with_empty_input() throws Throwable {
		StanfordNERNameTaggingWebservice = "https://mgnrl9129c.execute-api.us-east-1.amazonaws.com/acceptance/tagnameentityusingstanfordner";
		String result = "";
		int code = 200;
		try {
			String replyBody = "";
			System.out.println("Checking the response of StanfordNERNameTaggingWebservice");
			String inputString = "{  \"page_size\":{  \"length\":3000,\"width\":2000}}";
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			System.out.println("Start time" + dateFormat.format(cal.getTime()));
			System.out.println(StanfordNERNameTaggingWebservice);
			String restURL = StanfordNERNameTaggingWebservice;
			HttpClient httpClient = new HttpClient();
			PostMethod mPost = new PostMethod(restURL);
			Header methodHeader = new Header();
			try {
				// construct a input and send request to webservice and get response

				methodHeader.setName("content-type");
				methodHeader.setValue("application/json");
				methodHeader.setName("accept");
				methodHeader.setValue("application/json");
				RequestEntity entity = new StringRequestEntity(inputString, "application/json", "ISO-8859-1");
				mPost.addRequestHeader(methodHeader);
				mPost.setRequestEntity(entity);
				int statusCode = httpClient.executeMethod(mPost);
				System.out.println("statusCode  : " + statusCode);
				System.out.println("Output  : " + mPost.getResponseBodyAsString());
				if (statusCode == 200) {
					// parse response and print out
					InputStream is = mPost.getResponseBodyAsStream();
					BufferedReader r = new BufferedReader(new InputStreamReader(is));
					String line;
					while ((line = r.readLine()) != null) {
						JSONParser parser = new JSONParser();
						Object obj = parser.parse(line);
						JSONObject json = (JSONObject) obj;
					}
					cal = Calendar.getInstance();
					System.out.println("End time" + dateFormat.format(cal.getTime()));
				} else if (statusCode == 403) {
					System.out.println("Invalid username/password");
				} else {
					System.out.println("other error");
					System.out.println(mPost.getResponseBodyAsString());
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (mPost != null) {
					mPost.releaseConnection();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			result = "->Red<-\t";
		}
	}

	@Then("^I execute my RestFul service with 1000 transactions under 200 threads$")
	public void i_execute_my_RestFul_service() throws Throwable {
		ExecutorService executor = Executors.newFixedThreadPool(200);
		StanfordNERNameTaggingWebservice = "https://mgnrl9129c.execute-api.us-east-1.amazonaws.com/acceptance/tagnameentityusingstanfordner";
		System.out.println("Checking the response of StanfordNERNameTaggingWebservice");
		for (int i = 0; i < 1000; i++) {
			System.out.println("Calling webservice");
			Runnable worker = new MyRunnable();
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {

		}
	}

	public class MyRunnable implements Runnable {

		MyRunnable() {
		}

		@Override
		public void run() {

			String result = "";
			int code = 200;
			try {
				String replyBody = "";
				System.out.println("Checking the response of StanfordNERNameTaggingWebservice");
				String inputString = "{  \"page_size\":{  \"length\":3000,\"width\":2000},\"words\":{  \"word_1_249\":{  \"text\":\"Michael\",\"id\":\"word_1_249\",\"coordinates\":{  \"x0\":1494,\"y0\":2661,\"x1\":1708,\"y1\":2691},\"x_wconf\":0.89,\"class\":\"ocrx_word\",\"lang\":\"eng\"},\"word_1_248\":{  \"text\":\"Jackson\",\"id\":\"word_1_248\",\"coordinates\":{  \"x0\":1238,\"y0\":2471,\"x1\":1344,\"y1\":2861},\"x_wconf\":0.76,\"class\":\"ocrx_word\",\"lang\":\"eng\"}}}";
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

				Calendar cal = Calendar.getInstance();
				System.out.println("Start time" + dateFormat.format(cal.getTime()));
				System.out.println(StanfordNERNameTaggingWebservice);
				String restURL = StanfordNERNameTaggingWebservice;
				HttpClient httpClient = new HttpClient();
				PostMethod mPost = new PostMethod(restURL);
				Header methodHeader = new Header();

				try {
					// construct a input and send request to webservice and get response

					JSONParser parser1 = new JSONParser();
					JSONObject textBlobs = (JSONObject) parser1.parse(inputString);
					methodHeader.setName("content-type");
					methodHeader.setValue("application/json");
					methodHeader.setName("accept");
					methodHeader.setValue("application/json");
					RequestEntity entity = new StringRequestEntity(textBlobs.toString(), "application/json",
							"ISO-8859-1");
					mPost.addRequestHeader(methodHeader);
					mPost.setRequestEntity(entity);
					int statusCode = httpClient.executeMethod(mPost);
					System.out.println(statusCode);
					assertTrue(statusCode == 200);
					String response = mPost.getResponseBodyAsString();
					assertTrue(response.toLowerCase().contains("person"));
					System.out.println(response);
					if (statusCode == 200) {
						// parse response and print out
						InputStream is = mPost.getResponseBodyAsStream();
						BufferedReader r = new BufferedReader(new InputStreamReader(is));
						String line;
						while ((line = r.readLine()) != null) {
							JSONParser parser = new JSONParser();
							Object obj = parser.parse(line);
							JSONObject json = (JSONObject) obj;
						}
						cal = Calendar.getInstance();
						System.out.println("End time" + dateFormat.format(cal.getTime()));
					} else if (statusCode == 403) {
						System.out.println("Invalid username/password");
					} else {
						System.out.println("other error");
						System.out.println(mPost.getResponseBodyAsString());
					}

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (mPost != null) {
						mPost.releaseConnection();
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				result = "->Red<-\t";
			}
		}
	}
}
