package com.pharmacy.gts.Models;

public class OrderModel {

    private String morderid;
    private String memberid;
    private String transactionid;
    private String transid;
    private String amtreceived;
    private String dueblc;
    private String created_date;
    private String activated;
    private String enabled;

    public OrderModel() {
    }

    public OrderModel(String morderid, String memberid, String transactionid, String transid, String amtreceived, String dueblc, String created_date, String activated, String enabled) {
        this.morderid = morderid;
        this.memberid = memberid;
        this.transactionid = transactionid;
        this.transid = transid;
        this.amtreceived = amtreceived;
        this.dueblc = dueblc;
        this.created_date = created_date;
        this.activated = activated;
        this.enabled = enabled;
    }

    public String getMorderid() {
        return morderid;
    }

    public void setMorderid(String morderid) {
        this.morderid = morderid;
    }

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }

    public String getTransid() {
        return transid;
    }

    public void setTransid(String transid) {
        this.transid = transid;
    }

    public String getAmtreceived() {
        return amtreceived;
    }

    public void setAmtreceived(String amtreceived) {
        this.amtreceived = amtreceived;
    }

    public String getDueblc() {
        return dueblc;
    }

    public void setDueblc(String dueblc) {
        this.dueblc = dueblc;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getActivated() {
        return activated;
    }

    public void setActivated(String activated) {
        this.activated = activated;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }
}
