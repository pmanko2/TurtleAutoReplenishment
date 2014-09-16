package com.example.turtleautoreplenishment;

public class Customer 
{
	private String name;
	private String firstAddress;
	private String secondAddress;
	private String city;
	private String state;
	private String zipCode;
	private String distanceTo;
	private int id;
	
	public Customer(int id, String name, String firstAddress, String secondAddress,
			String city, String state, String zip)
	{
		this.name = name;
		this.firstAddress = firstAddress;
		this.secondAddress = secondAddress;
		this.city = city;
		this.state = state;
		this.zipCode = zip;
		this.id = id;
		this.distanceTo = "";
	}
	
	public String getName() {
		return name;
	}

	public String getFirstAddress() {
		return firstAddress;
	}

	public String getSecondAddress()
	{
		return secondAddress;
	}

	public int getId() {
		return id;
	}

	public String getCity()
	{
		return this.city;
	}
	
	public String getState()
	{
		return this.state;
	}
	
	public String getZipCode()
	{
		return this.zipCode;
	}
	
	public String getFullAddress()
	{
		String full = this.firstAddress + " " + this.secondAddress + " " 
				+ this.city + " " + this.state + " " + this.zipCode;
		
		return full;
	}
	
	public String getDistanceTo()
	{
		return this.distanceTo;
	}
	
	public void setDistanceTo(String distance)
	{
		this.distanceTo = distance;
	}
	
	
}
