package com.example.uninote.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uninote.R;
import com.example.uninote.ReminderAdapter;
import com.example.uninote.models.Reminder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ReminderFragment extends Fragment {

    public static final String TAG = "RemindersFragment";
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvReminders;
    private LinearLayoutManager mLayoutManager;
    private ReminderAdapter adapter;
    private List<Reminder> allReminders;

    public ReminderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvReminders = view.findViewById(R.id.rvReminders);
        allReminders = new ArrayList<>();
        adapter = new ReminderAdapter(getContext(), allReminders);
        rvReminders.setAdapter(adapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvReminders.setLayoutManager(mLayoutManager);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContent);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryReminders();
                swipeContainer.setRefreshing(false);
            }
        });

        queryReminders();
    }

    private void queryReminders() {
        ParseQuery<Reminder> query = ParseQuery.getQuery(Reminder.class);
        query.include(Reminder.KEY_USER);
        query.findInBackground(new FindCallback<Reminder>() {
            @Override
            public void done(List<Reminder> reminders, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                }
                for (Reminder reminder : reminders){
                    Log.i(TAG, "ToDo is good " + reminder.getTitle());
                }
                allReminders.addAll(reminders);
                adapter.notifyDataSetChanged();
            }
        });
    }
}