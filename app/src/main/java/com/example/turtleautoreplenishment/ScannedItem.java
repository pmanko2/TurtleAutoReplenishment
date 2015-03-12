package com.example.turtleautoreplenishment;

import android.os.Parcel;
import android.os.Parcelable;

public class ScannedItem
{
	private String customerProduct;
    private String turtleProduct;
	private String replenishmentType;
    private String descriptionOne;
    private String descriptionTwo;
	private int quantity;
    private String max;
    private String min;
    private String binNumber;
    private int sqLiteID;
	
	public ScannedItem(int id, String turtleProdID, String custProdID, String replenishment, String descOne,
                       String descTwo, int quantity, String max, String min, String binNumber)
	{
        this.sqLiteID = id;
		this.customerProduct = custProdID;
        this.turtleProduct = turtleProdID;
		this.replenishmentType = replenishment;
        this.descriptionOne = descOne;
        this.descriptionTwo = descTwo;
		this.quantity = quantity;
        this.max = max;
        this.min = min;
        this.binNumber = binNumber.substring(5);
	}

    public String getCustomerProduct() {return this.customerProduct;}
	public String getTurtleProduct() {return this.turtleProduct;}
	public String getReplenishmentType()
	{
		return this.replenishmentType;
	}
	public String getDescOne() {return this.descriptionOne;}
    public String getDescTwo() {return this.descriptionTwo;}
	public int getQuantity()
	{
		return this.quantity;
	}
    public String getMin(){return this.min;}
    public String getMax(){return this.max;}
    public String getBinNumber(){return this.binNumber;}
    public int getSqLiteID(){return this.sqLiteID;}

    public void setQuantity(int newQuantity)
    {
        this.quantity = newQuantity;
    }
    public void setReplenishmentType(String newRepType)
    {
        this.replenishmentType = newRepType;
    }
    public void setMin(String newMin){this.min = newMin;}
    public void setMax(String newMax){this.max = newMax;}
}
