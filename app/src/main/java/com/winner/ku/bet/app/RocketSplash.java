package com.winner.ku.bet.app;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.winner.ku.bet.app.databinding.FragmentRocketSplashBinding;

import java.util.concurrent.TimeUnit;


public class RocketSplash extends Fragment {


    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {

            int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            Activity activity = getActivity();
            if (activity != null
                    && activity.getWindow() != null) {
                activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            }
        }
    };

    private FragmentRocketSplashBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RocketForever.SetBase();
        mHidePart2Runnable.run();
        binding = FragmentRocketSplashBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new AsyncTask<Void, Integer, Void>()
        {
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                binding.advancedBar.setProgressPercentage(values[0], true);
            }

            @Override
            protected void onPostExecute(Void unused) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_rocketSplash_to_rocketMain);
                super.onPostExecute(unused);

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    int count = 0;
                    for (int i = 0; i < 100; i+=1) {
                        if (i < 50)
                            TimeUnit.MILLISECONDS.sleep(80);
                        else
                            TimeUnit.MILLISECONDS.sleep(50);
                        count+=1;
                        publishProgress(count);


                    }
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();


    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}