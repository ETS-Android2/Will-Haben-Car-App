package v2.vzdm.app.ui.home;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import v2.vzdm.app.Models.Item;
import v2.vzdm.app.Models.ItemList;
import v2.vzdm.app.Models.NotificationCount;
import v2.vzdm.app.R;
import v2.vzdm.app.Utils.Functions;
import v2.vzdm.app.Utils.Session;
import v2.vzdm.app.Webservices.ApiInterface;
import v2.vzdm.app.Webservices.ServiceGenerator;
import v2.vzdm.app.ui.filter.FilterFragment;
import v2.vzdm.app.ui.notifications.NotificationsFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AutoApp";
    private ImageView profile;
    private Session session;

    private RecyclerView recyclerView;
    private HomeAdapter mAdapter;
    private ProgressBar progressBar;
    private ImageView notification;

    private TextView notificationCounter;




    List<ItemList> automobileList = new ArrayList<>();
    List<Item> automobile = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



       View view = inflater.inflate(R.layout.fragment_product_list,container,false);

        profile = view.findViewById(R.id.profile_image);
        profile.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.fragment_product_list_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter =new HomeAdapter(automobile,getActivity());
        recyclerView.setAdapter(mAdapter);

        progressBar = view.findViewById(R.id.fragment_product_list_progressbar);

        notification = view.findViewById(R.id.notification_home);
        notification.setOnClickListener(this);

        notificationCounter = view.findViewById(R.id.notification_counter);

        session = new Session(view.getContext());



        final Handler refreshHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Functions.updateNotification(view.getContext(),notificationCounter,view,getActivity());

                if(session.getnotificationCount()!=null){
                    if(Integer.parseInt(session.getnotificationCount()) > 9)
                        notificationCounter.setText("9+");
                    else
                        notificationCounter.setText(session.getnotificationCount());
                }


                refreshHandler.postDelayed(this,  10*1000);
            }
        };
        refreshHandler.postDelayed(runnable,  10*1000);

        showProgessBar(true);
        getProducts();

        return view;


    }

    public void getProducts(){
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<List<Item>> call =apiInterface.getItemList();
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if(response.isSuccessful()){


                try {
                    assert response.body() != null;
                    automobile.addAll(response.body());

                }catch (Exception exception){
                    Log.d(TAG,"Error:" +exception);
                }

                    mAdapter.notifyDataSetChanged();


                }
                else{
                    Log.d(TAG, "Fail" + response.message());

                }
                showProgessBar(false);
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.d(TAG, "Fail: " + t.getMessage());
                showProgessBar(false);
            }
        });
    }





    private void showProgessBar(Boolean isShow)
    {
        if(isShow){
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image:
                Fragment filterFragment = new FilterFragment();
                // consider using Java coding conventions (upper first char class names!!!)
                FragmentTransaction filterTransaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                filterTransaction.replace(R.id.nav_host_fragment, filterFragment);
                filterTransaction.addToBackStack(null);

                // Commit the transaction
                filterTransaction.commit();
                break;


            case R.id.notification_home:
                Fragment notificationsFragment = new NotificationsFragment();
                // consider using Java coding conventions (upper first char class names!!!)
                FragmentTransaction notificationTransaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                notificationTransaction.replace(R.id.nav_host_fragment, notificationsFragment);
                notificationTransaction.addToBackStack(null);

                // Commit the transaction
                notificationTransaction.commit();
                break;

        }

    }
}
