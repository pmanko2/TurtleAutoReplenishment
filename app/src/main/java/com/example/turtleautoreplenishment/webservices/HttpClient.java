package com.example.turtleautoreplenishment.webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.turtleautoreplenishment.AuthenticatedUser;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class HttpClient 
{
	static private HttpClient singleton;
	private String userName;
	private String password;
	private String server_url = "http://www2.turtle.com/android/android_connect.php";
	
	
	private HttpClient(){};
	
	//singleton class
	public static HttpClient getInstance()
	{
		if(singleton == null)
			singleton = new HttpClient();
		
		return singleton;
	}
	
	// async method for receiving jsonobject from http request
	public void getJsonInBackground(final String requestMethod,	final HttpDataDelegate delegate,
			final ArrayList<NameValuePair> parameters)
	{
		
		Log.i("Get Json called", delegate.toString());
		
		server_url = "http://www2.turtle.com/android/android_connect.php";
		
		new AsyncTask<Object, Object, Object>()
		{

			@Override
			protected Object doInBackground(Object... params) 
			{
				String encoded = AuthenticatedUser.getUser().getHashedAuthentication();
				
				InputStream stream = openConnectionReturnStream(requestMethod, server_url, parameters, encoded);
				
				try 
				{
					JSONObject toReturn = inputStreamToJson(stream, true);
					Log.i("json returned: ", toReturn.toString());
					return toReturn;
				} 
				catch (IOException e) 
				{
					return e.toString();
				}
			}
			
			@Override
			protected void onPostExecute(Object result)
			{
				Log.i("Json Result String", result.toString());
				
				if(result instanceof JSONObject)
					delegate.handleAsyncDataReturn(result);
			}
			
		}.execute();
	}
	
	// method turns inputstream into string and then string into JSONObject
	// if unsuccessful, returns null
	private JSONObject inputStreamToJson(InputStream stream, Boolean putCRLF) throws IOException
	{
		String line = "";
	    StringBuilder total = new StringBuilder();
	    JSONObject json = null;
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(stream));

	    // Read response until the end
	    while ((line = rd.readLine()) != null) 
	    { 
	        total.append(line); 
	        if ( putCRLF )
	        	total.append( "\n" ); 
	    }
	    
	    Log.i("JSON String length: ", "Length: " + total.length());
	    
	    boolean success = true;

	    try 
	    {
			json = new JSONObject(total.toString());
			json.put("successful", true);
			
			return json;
		} 
	    catch (JSONException e) 
	    {		
	    	json = new JSONObject();
	    	try {
				json.put("successful", false);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	//Log.e("JSON Parser", "Error parsing data " + e.toString());
	    	success = false;
	    	
	    	return json;
		}
	}
	
	// method opens httpconnection and returns stream from server
	private InputStream openConnectionReturnStream(String requestMethod, String url, 
			ArrayList<NameValuePair> params, String encodedCredentials)
	{
		InputStream is = null;
		
		
		try
		{
			if(requestMethod.equals("POST"))
			{
				URI uri = new URI(server_url);
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpPost httpPost = new HttpPost(uri);
	            httpPost.setEntity(new UrlEncodedFormEntity(params));
	            httpPost.addHeader("Authorization", "Basic " + encodedCredentials);

	            HttpResponse httpResponse = httpClient.execute(httpPost);
	            
	            Log.i("Http Response", httpResponse.toString());
	            
	            HttpEntity httpEntity = httpResponse.getEntity();
	            is = httpEntity.getContent();
			}
			else
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);
 
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
			}
		}
		catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return is;
	}
	
}
