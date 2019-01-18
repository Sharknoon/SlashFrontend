package de.sharknoon.slash.People;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import de.sharknoon.slash.Activties.AddPeopleActivity;
import de.sharknoon.slash.Activties.CreateClientProjektActivity;
import de.sharknoon.slash.ChatMessages.GetChat;
import de.sharknoon.slash.ChatMessages.ImageLoader;
import de.sharknoon.slash.Fragments.PeopleSelector;
import de.sharknoon.slash.Image.SentimentLoader;
import de.sharknoon.slash.Project.UpdateScrumMasterMessage;
import de.sharknoon.slash.R;
import de.sharknoon.slash.SharedPreferences.ParameterManager;

import static de.sharknoon.slash.HomeScreen.UserHomeScreen.homeScreenClient;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {
    private final List<Person> people;
    private final String purpose;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        final TextView nameTextView;
        final TextView roleTextView;
        final ImageView profilePicture;
        final ImageView personMood;
        private final Context context;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder(Context context, View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.context = context;
            profilePicture = itemView.findViewById(R.id.element_picture);
            personMood = itemView.findViewById(R.id.element_mood);
            nameTextView = itemView.findViewById(R.id.person_name);
            roleTextView = itemView.findViewById(R.id.person_role);
            itemView.setOnClickListener(this);

            /*if(purpose.equals(PeopleSelector.PROJECT_INFO))
                itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle("Select The Action");
                        menu.add(0, v.getId(), 0, "Call");//groupId, itemId, order, title
                        menu.add(0, v.getId(), 0, "SMS");
                    }
                });*/
        }

        // Handles the row being clicked
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Person person = people.get(position);
                Intent intent;
                switch(purpose) {
                    case PeopleSelector.CHAT:
                        //Send broadcast to Create Chat
                        intent = new Intent(CreateClientProjektActivity.ChatPersonReceiver.ACTION);
                        intent.putExtra("Person", person);
                        context.sendBroadcast(intent);
                        break;
                    case PeopleSelector.PROJECT:
                        //Send broadcast to Add People
                        intent = new Intent(AddPeopleActivity.ProjectPersonReceiver.ACTION);
                        intent.putExtra("Person", person);
                        context.sendBroadcast(intent);

                        //Send broadcast to People Selector
                        Intent intent2 = new Intent(PeopleSelector.PeopleSelectedReceiver.ACTION);
                        intent2.putExtra(PeopleSelector.PeopleSelectedReceiver.ACTION, person.getId());
                        context.sendBroadcast(intent2);

                        //Remove selected person from list
                        people.remove(position);
                        notifyItemRemoved(position);
                        break;
                    case PeopleSelector.SELECTED:
                        //Send broadcast to People Selector
                        intent = new Intent(PeopleSelector.PeopleDeselectedReceiver.ACTION);
                        intent.putExtra(PeopleSelector.PeopleDeselectedReceiver.ACTION, person.getId());
                        context.sendBroadcast(intent);

                        //Remove selected person from list
                        people.remove(position);
                        notifyItemRemoved(position);
                        break;
                    case PeopleSelector.PROJECT_INFO:
                        PopupMenu popup = new PopupMenu(context, view);
                        popup.inflate(R.menu.project_member_context_menu);

                        if(ParameterManager.getUserId(context).equals(person.getId()))
                            popup.getMenu().findItem(R.id.message_user).setEnabled(false);
                        if(ParameterManager.getCurrentOpenChatOrProject().getProject().getProjectOwner().equals(person.getId()))
                            popup.getMenu().findItem(R.id.make_scrum_master).setEnabled(false);

                        popup.setOnMenuItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case R.id.message_user:
                                    Gson gson1 = new Gson();
                                    GetChat getChat = new GetChat(ParameterManager.getSession(context), person.getId());
                                    String jsonChatMessage = gson1.toJson(getChat);
                                    Log.i("XXXXXX",jsonChatMessage);

                                    if(homeScreenClient != null)
                                        homeScreenClient.getWebSocketClient().send(jsonChatMessage);
                                    else
                                        Toast.makeText(context, context.getString(R.string.error_socket_not_connected), Toast.LENGTH_LONG).show();
                                    return true;
                                case R.id.make_scrum_master:
                                    Gson gson2 = new Gson();
                                    UpdateScrumMasterMessage updateScrumMasterMessage = new UpdateScrumMasterMessage(
                                            ParameterManager.getSession(context),
                                            ParameterManager.getCurrentOpenChatOrProject().getProject().getId(),
                                            person.getId());
                                    String jsonScrumMasterMessage = gson2.toJson(updateScrumMasterMessage);
                                    Log.i("XXXXXX",jsonScrumMasterMessage);

                                    if(homeScreenClient != null) {
                                        homeScreenClient.getWebSocketClient().send(jsonScrumMasterMessage);
                                        for(int i=0; i<people.size(); i++) {
                                            if(people.get(i).getRole().equals(Person.SCRUM_MASTER)) {
                                                people.get(i).setRole(Person.MEMBER);
                                                notifyItemChanged(i);
                                            }
                                        }
                                        person.setRole(Person.SCRUM_MASTER);
                                        notifyItemChanged(people.indexOf(person));
                                        ParameterManager.getCurrentOpenChatOrProject().getProject().setProjectOwner(person.getId());
                                    } else
                                        Toast.makeText(context, context.getString(R.string.error_socket_not_connected), Toast.LENGTH_LONG).show();
                                    return true;
                            }
                            return false;
                        });
                        popup.show();
                        break;
                }
            }
        }
    }

    // Pass in the contact array into the constructor
    public PeopleAdapter(List<Person> people, String purpose) {
        this.people = people;
        this.purpose = purpose;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public PeopleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView;
        if(purpose.equals(PeopleSelector.SELECTED))
            contactView = inflater.inflate(R.layout.layout_grid_view_element, parent, false);
        else
            contactView = inflater.inflate(R.layout.layout_recycle_view_element, parent, false);

        // Return a new holder instance
        return new ViewHolder(context, contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PeopleAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Person person = people.get(position);

        // Set item views based on your views and data model
        TextView name = viewHolder.nameTextView;
        name.setText(person.getUsername());
        TextView role = viewHolder.roleTextView;
        if(person.getRole() != null)
            role.setText(person.getRole());
        else if(role != null)
            role.setVisibility(View.GONE);

        //Profilbild setzen
        ImageView picture = viewHolder.profilePicture;
        new ImageLoader(person.getImage(), viewHolder.context, picture);

        ImageView mood = viewHolder.personMood;
        new SentimentLoader(person.getSentiment(), mood);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return people.size();
    }
}