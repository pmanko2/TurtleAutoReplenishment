package com.example.turtleautoreplenishment;

import android.os.Parcel;
import android.os.Parcelable;

public class ScannedItem implements Parcelable 
{
	private String customerProduct;
    private String turtleProduct;
	private String replenishmentType;
    private String descriptionOne;
    private String descriptionTwo;
	private int quantity;
	
	public ScannedItem(String turtleProdID, String custProdID, String replenishment, String descOne,
                       String descTwo, int quantity)
	{
		this.customerProduct = custProdID;
        this.turtleProduct = turtleProdID;
		this.replenishmentType = replenishment;
        this.descriptionOne = descOne;
        this.descriptionTwo = descTwo;
		this.quantity = quantity;
	}
	
	public ScannedItem(Parcel in)
	{
		this.customerProduct = in.readString();
        this.turtleProduct = in.readString();
		this.replenishmentType = in.readString();
        this.descriptionOne = in.readString();
        this.descriptionTwo = in.readString();
		this.quantity = in.readInt();
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

    public void setQuantity(int newQuantity)
    {
        this.quantity = newQuantity;
    }

    public void setReplenishmentType(String newRepType)
    {
        this.replenishmentType = newRepType;
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) 
	{
		out.writeString(customerProduct);
        out.writeString(turtleProduct);
		out.writeString(replenishmentType);
        out.writeString(descriptionOne);
        out.writeString(descriptionTwo);
		out.writeInt(quantity);
		
	}
	
	public static final Parcelable.Creator<ScannedItem> CREATOR = new Parcelable.Creator<ScannedItem>() {

		@Override
		public ScannedItem createFromParcel(Parcel source) 
		{
			return new ScannedItem(source);
		}

		@Override
		public ScannedItem[] newArray(int size) 
		{
			return new ScannedItem[size];
		}
	};
}
