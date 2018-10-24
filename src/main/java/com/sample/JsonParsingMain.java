package com.sample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;


public class JsonParsingMain {

	ObjectMapper objectMapper = new ObjectMapper();
	
	public static void main(String args[]){
		String path= "";
		try {
			FileInputStream finputstream = new FileInputStream(
			        "sampleData.properties");
			Properties props = new Properties();
				props.load(finputstream);
				path=props.getProperty("file_path");
				JsonParsingMain parsrObj =new JsonParsingMain();
				String message=parsrObj.parseJsonUsingPathId(path);
				System.out.println("message : "+message);	
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String parseJsonUsingPathId(String url) {
		String message="Success";
		JsonNode jsonRes =fetchJsonFromFile(url);
		// jsonRes = fetchJsonFromUrl("url");
		if (jsonRes != null) {
			final Iterator<JsonNode> fields = jsonRes.getElements();
			int total=0;
			while (fields.hasNext()) {
				int sum=0; 
				JsonNode next =fields.next();
				String key=next.path("key").getTextValue();
				System.out.println("Key = "+key);
				Iterator<JsonNode> numList =next.path("numbers").getElements();
				while(numList.hasNext()){
					JsonNode number = numList.next();
					sum+=number.asInt();
					System.out.println("Number = "+number.asInt());
				}
				System.out.println("Each Json Sum = "+sum);
				total+=sum;
			}
				
			System.out.println("Total of all Integers = "+total);
			message="Success";
			}
		else
			message="failed";
	
		return message;
	}
	
	private JsonNode fetchJsonFromFile(String url) {
		JsonNode jsonNode=null;
		try {
			 jsonNode =objectMapper.readTree(objectMapper.getJsonFactory().
					createJsonParser(new FileReader(url)));
		}catch(final JsonProcessingException e){
			System.out.println("JSON validation for url " + e);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonNode;
	}
	
	@SuppressWarnings("unused")
	private JsonNode fetchJsonFromUrl(String url) {

		final String json = this.fetchDataFromUrl(url);

		JsonFactory factory = null;
		JsonParser jp = null;
		JsonNode jsonNode = null;
		if (json == null || json.contains("error"))
			return null;
		try {

			factory = objectMapper.getJsonFactory();
			jp = factory.createJsonParser(json);
			jsonNode = objectMapper.readTree(jp);
		} catch (final JsonProcessingException e) {
			final String json200 = json.substring(0, json.length() > 100 ? 200 : json.length());
			System.out.println("JSON validation for url " + e);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonNode;
	}
	
	@SuppressWarnings("resource")
	private String fetchDataFromUrl(final String url) {
		try {
			String urltohit = url;
			HttpURLConnection connection;
			int statusCode;
			boolean redirect = false;
			int redirectCounter = 0;

			do {
				connection = (HttpURLConnection) new URL(urltohit).openConnection();

				connection.setRequestMethod("GET");
				connection.setUseCaches(false);

				//final String authentication = this.username + ":" + password;
				// final String authentication = "aparna" + ":" + "A123#45";
				//connection.setRequestProperty("Authorization",
				//		"Basic " + new String(Base64.encodeBase64(authentication.getBytes("UTF8")), "UTF8"));

				connection.connect();

				statusCode = connection.getResponseCode();
				if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP || statusCode == HttpURLConnection.HTTP_MOVED_PERM
						|| statusCode == HttpURLConnection.HTTP_SEE_OTHER) {

					urltohit = connection.getHeaderField("Location");
					redirect = true;
					redirectCounter++;
				}

				if (statusCode == HttpURLConnection.HTTP_OK || (urltohit!=null)
						|| redirectCounter >= 3) {
					redirect = false;
				}
			} while (redirect);

			if (statusCode != HttpURLConnection.HTTP_OK) {
				System.out.println("mailer returned HTTP status " + statusCode);
			}
// we can use  BufferedReader or CharStreams.toString to convert InputStreamReader to string
			return new Scanner(new InputStreamReader(connection.getInputStream(), "UTF8")).toString();
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (final ProtocolException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;

	}
}
