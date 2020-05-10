package com.yotamshoval.mynews;

public class AdvertisementItem {

    private String itemName, itemDescription, itemLocation;

    public AdvertisementItem(){}

    public AdvertisementItem(String itemName, String itemDescription, String itemLocation) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemLocation = itemLocation;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }
}
