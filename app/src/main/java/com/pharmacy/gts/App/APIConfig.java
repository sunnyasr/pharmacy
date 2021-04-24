package com.pharmacy.gts.App;

import java.util.regex.Pattern;

public class APIConfig {


    public final static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public final static Pattern usernamePattern = Pattern.compile("[A-Za-z0-9_]+");

    //    Offline Server
//    private final static String BASE_URL = "http://192.168.43.44/GTS2020/Pharmacy/public/api/";
//    public static String BASE_IMAGE = "http://192.168.43.44/GTS2020/Pharmacy/public/";

    //    Offline Server
//    private final static String BASE_URL = "http://192.168.1.10/GTS2020/Pharmacy/public/api/";
//    public static String BASE_IMAGE = "http://192.168.1.10/GTS2020/Pharmacy/public/";

    //Live Server
    private final static String BASE_URL = "http://test.teamtathastu.com/public/api/";
    public static String BASE_IMAGE = "http://test.teamtathastu.com/public/";

    public final static String LOGIN = BASE_URL + "login";
    public final static String SLIDER = BASE_URL + "slider/getSliderApp";
    public final static String ME = BASE_URL + "me";
    public final static String PASSWORD_FORGOT = BASE_URL + "forgotPass/";
    public final static String UPLOAD_IMAGE = BASE_URL + "uploadImage";
    public final static String CHANGE_PASS = BASE_URL + "changepass";
    public final static String UPDATE_PROFILE = BASE_URL + "profileupdate";
    public final static String CHECK_USERNAME = BASE_URL + "checkUsername";
    public final static String REGISTER = BASE_URL + "registerApp";
    public final static String WEBPAGE = BASE_URL + "web/page/";
    public static final String GET_PRODUCT = BASE_URL + "product/getproducts";
    public static final String GET_SINSGLE_PRODUCT = BASE_URL + "product/getSingleProduct/";

    //Cart API's
    public static final String ADD_TO_CART = BASE_URL + "cart/addCart";
    public static final String GET_CART = BASE_URL + "cart/getCart/";
    public static final String UPDATE_CART = BASE_URL + "cart/updateCart";
    public static final String REMOVE_CART = BASE_URL + "cart/cartRemove/";

    //Address
    public static final String ADD_ADDRESS = BASE_URL + "address/create";
    public static final String GET_ADDRESS = BASE_URL + "address/getAddress/";
    public static final String UPDATE_ADDRESS = BASE_URL + "address/update";
    public static final String DELETE_ADDRESS = BASE_URL + "address/delete/";

    //Orders
    public static final String PLACE_ORDER = BASE_URL + "order/place";
    public static final String GET_ORDER = BASE_URL + "order/getMOrder/";
    public static final String GET_ORDER_DELIVERED = BASE_URL + "order/getMOrderDelivered/";
    public static final String GET_ORDER_PENDING = BASE_URL + "order/getMOrderPending/";
    public static final String GET_ORDER_DETAIL = BASE_URL + "order/getOrderDetail/";

    //Contact
    public static final String GET_CONTACT = BASE_URL + "contact/getContact";

    //Transaction
    public static final String PAY_NOW= BASE_URL+"transaction/paynow";
    public static final String PAY_UPDATE= BASE_URL+"transaction/update";
    public static final String GET_PAY_HISTORY= BASE_URL+"transaction/getTrans/";
    public static final String GET_TRANSATIONS= BASE_URL+"transaction/getTransByMember/";

    //Contact
    public static final String GET_COUPON = BASE_URL + "coupon/getCouponByMember/";
    public static final String CHECK_COUPON = BASE_URL + "coupon/checkcoupon";


}
