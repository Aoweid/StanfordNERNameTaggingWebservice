/**
 *
 */
package StanfordNERNameTaggingWebservice;

import java.net.URL;

import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the implementation class of StanfordNERNameTaggingWebservice.
 *
 * @author Alvin Ding
 *
 */
public class StanfordNERNameTaggingWebserviceImpl implements StanfordNERNameTaggingWebservice {

	URL link = null;
	static Logger logger = LoggerFactory.getLogger(StanfordNERNameTaggingWebserviceImpl.class);

	/**
	 * The input is one json string that contains hocr content. It will first construct a big string as the input for
	 * Stanford NER, which will tag the word that is either person name or company name, and then based the stanford ner
	 * result add new tags to json and then output the updated json
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Response StanfordNERNameTaggingWebservice(String input) {
		try {
			/* Call the service main class */
			logger.info("INPUT === " + input);
			final String response = new StanfordNameTagger().scan(input);
			logger.info("OUTPUT === " + response);
			return Response.ok(response).status(200).build();
		} catch (StanfordNameTaggingException e) {
			JSONObject responseJson = new JSONObject();
			responseJson.put("STATUS_CODE", "400");
			responseJson.put("STATUS", "Failure");
			responseJson.put("MESSAGE", e.getDetailedMessage());
			return Response.ok(responseJson.toString()).status(400).build();
		} catch (Exception e) {
			JSONObject responseJson = new JSONObject();
			responseJson.put("STATUS_CODE", "400");
			responseJson.put("STATUS", "Failure");
			responseJson.put("MESSAGE", e.getLocalizedMessage());
			return Response.ok(responseJson.toString()).status(400).build();
		}
	}
}
