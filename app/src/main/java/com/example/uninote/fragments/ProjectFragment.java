package com.example.uninote.fragments;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.uninote.ProjectAdapter;
import com.example.uninote.R;
import com.example.uninote.ReminderAdapter;
import com.example.uninote.ReminderDetailActivity;
import com.example.uninote.ToDoAdapter;
import com.example.uninote.models.Project;
import com.example.uninote.models.Reminder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ProjectFragment extends Fragment {

    public static final String TAG = "ProjectsFragment";
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvProjects;
    private LinearLayoutManager mLayoutManager;
    private ImageButton btnAdd;
    private ProjectAdapter adapter;
    private List<Project> allProjects;

    public ProjectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAdd = view.findViewById(R.id.btnAdd);
        rvProjects = view.findViewById(R.id.rvProjects);
        allProjects = new ArrayList<>();
        adapter = new ProjectAdapter(getContext(), allProjects);
        rvProjects.setAdapter(adapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvProjects.setLayoutManager(mLayoutManager);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContent);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryProjects();
                swipeContainer.setRefreshing(false);
            }
        });
        queryProjects();
    }

    private void queryProjects() {
        final ParseQuery<ParseUser> innerQuery = ParseQuery.getQuery("_User");
        innerQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("User_Project");
        query.whereMatchesQuery("username", innerQuery);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> projects, ParseException e) {
                for (ParseObject project : projects) {
                    Log.i(TAG, "Reminder is good " + project.getParseObject("project").getObjectId());
                    final ParseQuery<Project> query = ParseQuery.getQuery("Project");
                    query.whereEqualTo("objectId", project.getParseObject("project").getObjectId());

                    query.findInBackground(new FindCallback<Project>() {
                        @Override
                        public void done(List<Project> objects, ParseException e) {
                            allProjects.addAll(objects);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}