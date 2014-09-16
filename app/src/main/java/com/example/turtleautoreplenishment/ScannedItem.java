package com.example.turtleautoreplenishment;

import android.os.Parcel;
import android.os.Parcelable;

public class ScannedItem implements Parcelable 
{
	private String barcodeNumber;
	private String replenishmentType;
	private int quantity;
	
	public ScannedItem(String number, String replenishment, int quantity)
	{
		this.barcodeNumber = number;
		this.replenishmentType = replenishment;
		this.quantity = quantity;
	}
	
	public ScannedItem(Parcel in)
	{
		this.barcodeNumber = in.readString();
		this.replenishmentType = in.readString();
		this.quantity = in.readInt();
	}
	
	public String getBarcodeNumber()
	{
		return this.barcodeNumber;
	}
	
	public String getReplenishmentType()
	{
		return this.replenishmentType;
	}
	
	public int getQuantity()
	{
		return this.quantity;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) 
	{
		out.writeString(barcodeNumber);
		out.writeString(replenishmentType);
		out.writeInt(quantity);
		
	}
	
	public static final Parcelable.Creator<ScannedItem> CREATOR = new Parcelable.Creator<ScannedItem>() {

		@Override
		public ScannedItem createFromParcel(Parcel source) 
		{
			// TODO Auto-generated method stub
			return new ScannedItem(source);
		}

		@Override
		public ScannedItem[] newArray(int size) 
		{
			// TODO Auto-generated method stub
			return new ScannedItem[size];
		}
	};
}
