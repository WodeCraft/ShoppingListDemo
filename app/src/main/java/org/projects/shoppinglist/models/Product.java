package org.projects.shoppinglist.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jcr on 13-04-2018.
 */

public class Product implements Parcelable {

    /**
     * Name of the product
     */
    String name;

    /**
     * Quantity to buy
     */
    int quantity;

    /**
     * Default empty constructor
     */
    public Product() {

    }

    /**
     * Constructor used for setting the name and quantity.
     * @param name
     * @param quantity
     */
    public Product(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Will return the name and quantity of the product.
     * @return
     */
    @Override
    public String toString() {
        return quantity + " x " + name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write product to parcel
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(quantity);
    }

    /**
     * Used by Parcelable interface
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    /**
     * Create product from Parcel
     * @param in
     */
    private Product(Parcel in) {
        this.name = in.readString();
        this.quantity = in.readInt();
    }
}
