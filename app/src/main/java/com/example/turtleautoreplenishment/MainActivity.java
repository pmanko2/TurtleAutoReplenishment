package com.example.turtleautoreplenishment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.turtleautoreplenishment.webservices.HttpClient;
import com.example.turtleautoreplenishment.webservices.HttpDataDelegate;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener, HttpDataDelegate 
{

	private EditText usernameText;
	private EditText passwordText;
	private Button loginButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		usernameText = (EditText) findViewById(R.id.username_input);
		passwordText = (EditText) findViewById(R.id.password_input);
		loginButton = (Button) findViewById(R.id.login_button);

        //handler to clear error after user starts typing in field
        usernameText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                usernameText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
		
		loginButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) 
			{
				boolean error = checkInput(); //check if input correct

                if(!error) //if correct input, send login request
                {
                    String userName = usernameText.getText().toString();
                    String password = passwordText.getText().toString();
                    String authentication = userName + ":" + password;

                    AuthenticatedUser.getUser().setCredentials(userName, password, Base64.encodeToString(authentication.getBytes(), Base64.NO_WRAP));

                    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("tag", "confirm_login"));

                    HttpClient.getInstance().getJsonInBackground("POST", MainActivity.this, params);
                }
			}
			
			private boolean checkInput()
			{
                boolean isError = false;

				if(usernameText.getText().length() == 0)
				{
					usernameText.setError("Please input a username");
                    isError = true;
				}

                if(passwordText.getText().length() == 0) {
                    passwordText.setError("Please input a password");
                    isError = true;
                }

                return isError;
			}
			
		});
		
	}
	
	@Override
	public void handleAsyncDataReturn(Object ret) 
	{

		if(ret instanceof JSONObject)
		{
			JSONObject returnJson = (JSONObject) ret;
			int success = 0;
			boolean successful = false;
			
			try {
				successful = returnJson.getBoolean("successful");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			if(successful)
			{
				Intent customerList = new Intent(this, ChooseCustomerActivity.class);
				startActivity(customerList);
			}
			else
			{
				usernameText.setText("");
				passwordText.setText("");
				usernameText.setError(null);
				passwordText.setError(null);
				AuthenticatedUser.getUser().setCredentials("", "", "");
				Toast.makeText(this, "Could not log you in. Please try again", Toast.LENGTH_LONG).show();
			}
		}
		else
		{
			usernameText.setText("");
			passwordText.setText("");
			usernameText.setError(null);
			passwordText.setError(null);
			AuthenticatedUser.getUser().setCredentials("", "", "");
			Toast.makeText(this, "Could not log you in. Please try again", Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
