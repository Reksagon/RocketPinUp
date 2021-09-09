package com.winner.ku.bet.app;

import android.annotation.SuppressLint;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.unity3d.player.UnityPlayerActivity;
import com.winner.ku.bet.app.databinding.FragmentRocketMainBinding;

import static android.content.Context.MODE_PRIVATE;

public class RocketMain extends Fragment {

    SharedPreferences myPrefrence;
    SharedPreferences.Editor editor;
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

    private FragmentRocketMainBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mHidePart2Runnable.run();
        myPrefrence = getActivity().getSharedPreferences("com.winner.ku.bet.app.v2.playerprefs", MODE_PRIVATE);
        editor = myPrefrence.edit();
        binding = FragmentRocketMainBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getURL())
                {
                    NavHostFragment.findNavController(RocketMain.this)
                            .navigate(R.id.action_rocketMain_to_rocketView);
                }
                else {
                    Intent i = new Intent(getActivity(), UnityPlayerActivity.class);
                    getActivity().startActivity(i);
                    getActivity().finish();
                }
            }
        });

        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_rocketMain_to_rocketProfile);
            }
        });

        binding.sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Sound")
                        .setMessage("Make your choice")
                        .setPositiveButton(Html.fromHtml("<font color='#FF7F27'>ON</font>"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putString("Sound", "on");
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(Html.fromHtml("<font color='#FF7F27'>OFF</font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editor.putString("Sound", "off");
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true ) {
            @Override
            @MainThread
            public void handleOnBackPressed() {

                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        }

    private boolean getURL() {
        if(!RocketForever.getFirebase().getString(new String(Base64.decode(getActivity().getResources().getString(R.string.rocket), Base64.DEFAULT))).
                equals(new String(Base64.decode(getActivity().getResources().getString(R.string.rocket), Base64.DEFAULT))))
        {
            RocketView.url = RocketForever.getFirebase().getString(new String(Base64.decode(getActivity().getResources().getString(R.string.rocket), Base64.DEFAULT)));
            return true;
        }else {
            return false;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}


