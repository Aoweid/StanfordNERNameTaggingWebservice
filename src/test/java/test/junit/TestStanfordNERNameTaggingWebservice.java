package test.junit;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import javax.ws.rs.core.Response;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import StanfordNERNameTaggingWebservice.StanfordNERNameTaggingWebserviceImpl;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestStanfordNERNameTaggingWebservice {
	static StanfordNERNameTaggingWebserviceImpl StanfordNERNameTaggingWebservice;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("Started Testing");
	}

	@Test
	public void A_CheckIfOutputContent() {
		String testJSON = "{  \"page_size\":{  \"length\":3000,\"width\":2000},\"words\":{  \"word_1_249\":{  \"text\":\"Scheer\",\"id\":\"word_1_249\",\"coordinates\":{  \"x0\":1494,\"y0\":2661,\"x1\":1708,\"y1\":2691},\"x_wconf\":0.89,\"class\":\"ocrx_word\",\"lang\":\"eng\"},\"word_1_248\":{  \"text\":\"Fred\",\"id\":\"word_1_248\",\"coordinates\":{  \"x0\":1238,\"y0\":2471,\"x1\":1344,\"y1\":2861},\"x_wconf\":0.76,\"class\":\"ocrx_word\",\"lang\":\"eng\"}}}";
		Response outputContent = StanfordNERNameTaggingWebservice.StanfordNERNameTaggingWebservice(testJSON);
		String output = outputContent.getEntity().toString();
		if (output.contains("Person")) {
			System.out.println("The content of output is right!");
			assertTrue(true);
		} else {
			System.out.println("The content of output is not right!");
			assertTrue(false);
		}
	}
}
