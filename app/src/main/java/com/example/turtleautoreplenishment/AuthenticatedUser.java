package com.example.turtleautoreplenishment;

public class AuthenticatedUser 
{
	private String userName;
	private String hashedPassword;
	private String authenticationHash;
	private static AuthenticatedUser singleton;
	private AuthenticatedUser(){};
	
	public static AuthenticatedUser getUser()
	{
		if(singleton == null)
		{
			singleton = new AuthenticatedUser();
		}
		
		return singleton;
	}
	
	public void setCredentials(String user, String passwordHash, String authenticationHash)
	{
		this.userName = user;
		this.hashedPassword = passwordHash;
		this.authenticationHash = authenticationHash;
	}
	
	public String getUserName()
	{
		return this.userName;
	}
	
	public String getHashedPassword()
	{
		return this.hashedPassword;
	}
	
	public String getHashedAuthentication()
	{
		return this.authenticationHash;
	}
}
