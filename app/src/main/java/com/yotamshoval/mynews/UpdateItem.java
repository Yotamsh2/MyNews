package com.yotamshoval.mynews;

public class UpdateItem {

    private String itemName, itemDescription, itemCategory, timeOfPublish;

    public UpdateItem(){}

    public UpdateItem(String itemName, String itemDescription, String itemCategory, String timeOfPublish) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemCategory = itemCategory;
        this.timeOfPublish = timeOfPublish;
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

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getTimeOfPublish() {
        return timeOfPublish;
    }

    public void setTimeOfPublish(String timeOfPublish) {
        this.timeOfPublish = timeOfPublish;
    }
}
