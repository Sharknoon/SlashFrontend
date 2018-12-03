package de.sharknoon.slash.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

import de.sharknoon.slash.HomeScreen.FindUser;
import de.sharknoon.slash.HomeScreen.HomeScreenClient;
import de.sharknoon.slash.HomeScreen.PersonSearchResult;
import de.sharknoon.slash.HomeScreen.UserHomeScreen;
import de.sharknoon.slash.Login.LogoutMessage;
import de.sharknoon.slash.People.PeopleAdapter;
import de.sharknoon.slash.People.Person;
import de.sharknoon.slash.R;
import de.sharknoon.slash.Registration.RegistrationMessage;
import de.sharknoon.slash.Registration.UserRegistration;
import de.sharknoon.slash.SharedPreferences.ParameterManager;

import static de.sharknoon.slash.HomeScreen.UserHomeScreen.homeScreenClient;

public class PeopleSelector extends Fragment {
    private static final String ARG_PARAM1 = "purpose";

    private ArrayList<Person> people;
    private PeopleAdapter adapter;
    private String purpose;

    private PeopleSearchResultReceiver peopleSearchResultReceiver = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PeopleSelector.
     */
    public static PeopleSelector newInstance(String purpose) {
        PeopleSelector fragment = new PeopleSelector();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, purpose);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            purpose = getArguments().getString(ARG_PARAM1);
        }
        people = new ArrayList<>();

        peopleSearchResultReceiver = new PeopleSearchResultReceiver();
        IntentFilter inf = new IntentFilter(PeopleSearchResultReceiver.ACTION);
        getActivity().registerReceiver(peopleSearchResultReceiver, inf);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people_selector, container, false);

        // Lookup the recyclerview in activity layout
        RecyclerView rvPeople = view.findViewById(R.id.people_view);
        // Create adapter passing in the people list
        adapter = new PeopleAdapter(people, purpose);
        // Attach the adapter to the recyclerview to populate items
        rvPeople.setAdapter(adapter);
        // Set layout manager to position the items
        rvPeople.setLayoutManager(new LinearLayoutManager(view.getContext()));

        this.handleSearchButton(view);

        return view;
    }

    private void handleSearchButton(View view) {
        Button button = view.findViewById(R.id.createChatFindButton);
        TextView search = view.findViewById(R.id.createChatEditWindow);
        button.setOnClickListener(view1 -> {
            if (search != null) {
                Gson gson = new Gson();
                FindUser findUser = new FindUser(ParameterManager.getSession(view1.getContext()), search.getText().toString());
                String jsonSearchMessage = gson.toJson(findUser);
                Log.d("JSON", jsonSearchMessage);

                homeScreenClient.getWebSocketClient().send(jsonSearchMessage);
                //todo Suche an Server schicken und Recyclerview mit Ergebnisliste füllen

            }
        });
    }


    public class PeopleSearchResultReceiver extends BroadcastReceiver {
        PeopleSelector ps = null;
        public static final String ACTION = "de.sharknoon.slash.RECEIVE_PERSON_SEARCH_RESULT";

        @Override
        public void onReceive(Context context, Intent intent) {
            PersonSearchResult searchResult = (PersonSearchResult) intent.getSerializableExtra(ACTION);
            people.clear();

            people.addAll(searchResult.getUsers());

            //Refresh list in UI
            adapter.notifyDataSetChanged();
        }
    }
}
