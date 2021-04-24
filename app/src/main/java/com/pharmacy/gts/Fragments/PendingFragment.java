package com.pharmacy.gts.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ethanhua.skeleton.SkeletonScreen;
import com.pharmacy.gts.Adapters.MasterOrderAdapter;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.App.ComMethod;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.Models.MOrderModel;
import com.pharmacy.gts.PrefManager.UserManager;
import com.pharmacy.gts.R;
import com.pharmacy.gts.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


public class PendingFragment extends Fragment {

    private UserManager userManager;
    private KProgressHUD kProgressHUD;
    private SkeletonScreen skeletonScreen;
    private MasterOrderAdapter adapter;
    private ArrayList<MOrderModel> mOrderModelPendingArrayList;
    private SwipeRefreshLayout refreshLayout;
    private ComMethod comMethod;
    private TextView tvEmpty;


    public PendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userManager = new UserManager(getContext());
        setProgress();
        comMethod = new ComMethod(getContext());
        mOrderModelPendingArrayList = new ArrayList<>();
        adapter = new MasterOrderAdapter(getContext(), mOrderModelPendingArrayList);
        tvEmpty = (TextView) getActivity().findViewById(R.id.tv_empty_pending);

        refreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefreshLayout_pending);
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview_pending);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!comMethod.checkNetworkConnection()) {
                    TastyToast.makeText(getContext(), "Check your Internet Connection !!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                } else {
                    refreshLayout.setRefreshing(true);
                    getOrders();
                }
            }
        });


        if (comMethod.checkNetworkConnection())
            getOrders();
        else
            comMethod.alertDialog("Network", getString(R.string.no_network), true, null);

    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    private void getOrders() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_ORDER_PENDING + userManager.getMID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                kProgressHUD.dismiss();
                refreshLayout.setRefreshing(false);
                mOrderModelPendingArrayList.clear();

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() == 0) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MOrderModel model = new MOrderModel();
                                model.setMorderid(jsonObject.getString("morderid"));
                                model.setMemberid(jsonObject.getString("memberid"));
                                model.setAddressid(jsonObject.getString("addressid"));
                                model.setTotalamount(jsonObject.getString("totalamount"));
                                model.setTotalqty(jsonObject.getString("totalqty"));
                                model.setDiscount(jsonObject.getString("discount"));
                                model.setNett(jsonObject.getString("nett"));
                                model.setGST(jsonObject.getString("gstamt"));
                                model.setStatus(jsonObject.getString("status"));
                                model.setOrderdate(jsonObject.getString("orderdate"));
                                model.setDelivereddate(jsonObject.getString("delivereddate"));
                                model.setName(jsonObject.getString("name"));
                                model.setPhone(jsonObject.getString("phone"));
                                model.setAddress(jsonObject.getString("address"));
                                model.setHno(jsonObject.getString("hno"));
                                model.setPostcode(jsonObject.getString("postcode"));
                                model.setCity(jsonObject.getString("city"));
                                model.setCreated_date(jsonObject.getString("created_date"));
                                model.setPaystatus(jsonObject.getString("paystatus"));
                                model.setDuenett(jsonObject.getString("duenett"));
                                model.setActivated(jsonObject.getString("activated"));
                                model.setEnabled(jsonObject.getString("enabled"));
                                mOrderModelPendingArrayList.add(model);

                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userManager.getToken());
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getContext()).addRequestQueue(stringRequest);
    }

}
