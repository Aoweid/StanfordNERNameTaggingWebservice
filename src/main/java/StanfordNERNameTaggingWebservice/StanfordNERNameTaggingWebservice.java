
package StanfordNERNameTaggingWebservice;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
/**
 * this is the interface of the StanfordNERNameTaggingWebservice
 * @author alvin
 *
 */
@Path("/")
public interface StanfordNERNameTaggingWebservice {	
	@POST
	@Path("/StanfordNERNameTaggingWebservice")
	@Consumes("application/json")
	@Produces("application/json")
	public Response StanfordNERNameTaggingWebservice(String input);
	
}
