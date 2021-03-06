package de.hampager.dapnetmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.DAPNET;
import de.hampager.dap4j.DapnetSingleton;
import de.hampager.dap4j.callbacks.DapnetListener;
import de.hampager.dap4j.callbacks.DapnetResponse;
import de.hampager.dap4j.models.CallResource;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.adapters.CallAdapter;


public class CallFragment extends Fragment implements SearchView.OnQueryTextListener {
    private static final String TAG = "CallFragment";
    private RecyclerView recyclerView;
    private CallAdapter adapter;
    private SwipeRefreshLayout mSwipe;
    private SearchView searchView;
    public CallFragment() {
        // Required empty public constructor
    }

    public static CallFragment newInstance() {
        return new CallFragment();
    }


    private void initViews(View v) {
        adapter=new CallAdapter(new ArrayList<CallResource>());
        recyclerView = (RecyclerView) v.findViewById(R.id.item_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        fetchJSON();

    }

    private void fetchJSON() {
        DAPNET dapnet = DapnetSingleton.getInstance().getDapnet();
        //TODO: Implement non-admin
        //Log.i(TAG, "fetchJSON, admin: " + admin);
        //if (admin) {
        //  Log.i(TAG, "Admin access granted. Fetching All Calls...");
        dapnet.getCalls("", new DapnetListener<List<CallResource>>() {
            @Override
            public void onResponse(DapnetResponse<List<CallResource>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    Log.i(TAG, "Connection was successful");
                    // tasks available
                List<CallResource> data = dapnetResponse.body();
                    adapter.setmValues(data);
                    adapter.notifyDataSetChanged();
                    mSwipe.setRefreshing(false);
                } else {
                    Log.e(TAG, "Error");
                    //TODO: .code,.message etc
                    /*Log.e(TAG, "Error " + dapnetResponse.code());
                    Log.e(TAG, dapnetResponse.message());
                    Snackbar.make(recyclerView, "Error! " + dapnetResponse.code() + " " + dapnetResponse.message(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    if (dapnetResponse.code() == 401) {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();
                    }*/
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                // something went completely wrong (e.g. no internet connection)
                Log.e(TAG, throwable.getMessage());
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_call, container, false);
        v.setTag(TAG);
        setHasOptionsMenu(true);
        initViews(v);
        mSwipe = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshCalls);

        // Setup refresh listener which triggers new data loading

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override

            public void onRefresh() {

                // Your code to refresh the list here.

                // Make sure you call swipeContainer.setRefreshing(false)

                // once the network request has completed successfully.

                fetchJSON();

            }

        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "Creating menu...");
        inflater.inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);


    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        adapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

}
