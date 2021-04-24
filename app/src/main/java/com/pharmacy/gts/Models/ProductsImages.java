package com.pharmacy.gts.Models;

import java.io.Serializable;

/**
 * Created by admin on 6/3/2017.
 */

public class ProductsImages implements Serializable {
    private String productimg;


    public ProductsImages() {
    }

    public ProductsImages(String productimg) {
        this.productimg = productimg;
    }


    public String getProductimg() {
        return productimg;
    }

    public void setProductimg(String productimg) {
        this.productimg = productimg;
    }
}
