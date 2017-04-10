package StanfordNERNameTaggingWebservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class StanfordNameTagger {
	public static final Logger logger = LoggerFactory.getLogger(StanfordNERNameTaggingWebserviceImpl.class);
	static AbstractSequenceClassifier<CoreLabel> classifier;
	static {
		String serializedClassifier = "/opt/heavywater/bin/english.all.3class.distsim.crf.ser.gz";
		try {
			classifier = CRFClassifier.getClassifier(serializedClassifier);
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * The input is one json string that contains hocr content. It will first construct a 
	 * big string as the input for Stanford NER, which will tag the word that is either 
	 * person name or company name, and then based the stanford ner result add
	 * new tags to json and then output the updated json
	 */
	public String scan(String input) throws StanfordNameTaggingException {
		JSONObject outputJSON = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject newWords = new JSONObject();
		String wholeContent = "";
		try {
			// parse the input
			JSONObject JsonInput = (JSONObject) parser.parse(input);
			JSONObject InputWordsList = new JSONObject();
			ArrayList<Map<String, String>> wordAndIDs = new ArrayList<>();
			InputWordsList = (JSONObject) JsonInput.get("words");
			if (!JsonInput.containsKey("words")) {
				throw new StanfordNameTaggingException("Input hocr json does not contains mandatory key words");
			}
			// the words in input json is not in right syntactic sequence, first iterate through the input json to store information in a list for sorting
			for (Iterator iterator = InputWordsList.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				JSONObject word = (JSONObject) InputWordsList.get(key);
				String id = word.get("id").toString();
				String text = word.get("text").toString();
				//build a map to store text and word id and put the map in list
				Map<String, String> wordAndID = new HashMap<String, String>();
				if (!text.equals(" ")) {
					wordAndID.put("text", text);
					wordAndID.put("id", id);
					wordAndIDs.add(wordAndID);
				}

			}
			// sort result list by the word id
			Collections.sort(wordAndIDs, new Comparator<Map<String, String>>() {
				public int compare(Map<String, String> map1, Map<String, String> map2) {
					String id1 = map1.get("id").split("_")[2];
					String id2 = map2.get("id").split("_")[2];

					if (Double.parseDouble(id1) == Double.parseDouble(id2)) {
						return 0;
					} else if (Double.parseDouble(id1) > Double.parseDouble(id2)) {
						return 1;
					} else {
						return -1;
					}
				}
			});
			//follow the sequence to build the big string
			for (Map<String, String> map : wordAndIDs) {
				wholeContent = wholeContent + map.get("text") + " ";
			}
			//pre-process the string then give it to stanford ner for tagging
			wholeContent = wholeContent.replaceAll("& ", "&amp; ");
			wholeContent = wholeContent.replaceAll("&([^;]+(?!(?:\\w|;)))", "&amp;$1");
			wholeContent = wholeContent.replaceAll("<", ". &lt;");
			wholeContent = wholeContent.replaceAll("\"", "&quot;");
			wholeContent = wholeContent.replaceAll("'", "&apos;");
			wholeContent = wholeContent.replaceAll(">", ". &gt;");
			String xml = "<root>" + classifier.classifyToString(wholeContent, "xml", true) + "</root>";
			Document doc = DocumentHelper.parseText(xml);
			Element rootElt = doc.getRootElement();
			@SuppressWarnings("rawtypes")
			Iterator iter = rootElt.elementIterator("wi");
			ArrayList<String> personNames = new ArrayList<>();
			ArrayList<String> companyNames = new ArrayList<>();
			//find all words tagged with either person or company, store them in list
			while (iter.hasNext()) {
				Element recordEle = (Element) iter.next();
				if (recordEle.attributeValue("entity").equals("PERSON")) {
					personNames.add(recordEle.getText());
				}
				if (recordEle.attributeValue("entity").equals("ORGANIZATION")) {
					companyNames.add(recordEle.getText());
				}
			}
			//use the person name list and company name list to find related word id then use the id as key to update the curtain JSON
			for (Map<String, String> wordAndID : wordAndIDs) {
				if (personNames.contains(wordAndID.get("text"))) {
					String id = wordAndID.get("id");
					JSONObject word = (JSONObject) InputWordsList.get(id);
					word.put("Person", "true");
					InputWordsList.put(id, word);
				}
				if (companyNames.contains(wordAndID.get("text"))) {
					String id = wordAndID.get("id");
					JSONObject word = (JSONObject) InputWordsList.get(id);
					word.put("Company", "true");
					InputWordsList.put(id, word);
				}
			}

			// update output json
			outputJSON.put("words", InputWordsList);
			JSONObject page = (JSONObject) JsonInput.get("page_size");
			outputJSON.put("page_size", page);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			logger.error("Exception in StanfordNERNameTaggingWebservice, Exception Message :- ");
			logger.error(e.getMessage());
			throw new StanfordNameTaggingException(e.getMessage());
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			logger.error("Exception in StanfordNERNameTaggingWebservice, Exception Message :- ");
			logger.error(e.getMessage());
			throw new StanfordNameTaggingException(e.getMessage());
		}
		return outputJSON.toString();
	}

}

