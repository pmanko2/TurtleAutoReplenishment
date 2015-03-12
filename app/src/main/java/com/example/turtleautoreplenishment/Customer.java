package com.example.turtleautoreplenishment;

public class Customer 
{
	private String name;
	private String firstAddress;
	private String secondAddress;
	private String city;
	private String state;
	private String zipCode;
	private int id;
    private int shipTo;
	
	public Customer(int id, String name, String firstAddress, String secondAddress,
			String city, String state, String zip, int shipTo)
	{
		this.name = name;
		this.firstAddress = firstAddress;
		this.secondAddress = secondAddress;
		this.city = city;
		this.state = state;
		this.zipCode = zip;
		this.id = id;
        this.shipTo = shipTo;
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

    public int getShipTo() {return this.shipTo;}
	
	public String getFullAddress()
	{
		return this.firstAddress + " " + this.secondAddress + " "
				+ this.city + " " + this.state + " " + this.zipCode;
	}
	
}
