package de.sharknoon.slash.Activties;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import de.sharknoon.slash.ChatMessages.ChatOrProject;
import de.sharknoon.slash.ChatMessages.UserChatScreen;
import de.sharknoon.slash.R;

public class ChatScreenActivity extends AppCompatActivity {
    public static final String PROJECT = "project";

    private static UserChatScreen screen;
    private static LinearLayout messageScreen;
    public static boolean active = false;
    ChatOrProject chatOrProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        active = true;
        setContentView(R.layout.activity_chat_screen);

        //Stop keypad from spawning directly at beginning
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        String name = "NO_NAME";

        if(getIntent().getExtras() != null) {
                name = getIntent().getExtras().getString("NAME");
                chatOrProject = (ChatOrProject)getIntent().getExtras().getSerializable("CHATORPROJECT");
        }

        if(getActionBar() != null) {
            getActionBar().setTitle(name);
        }
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(name);
        }

        messageScreen = findViewById(R.id.chatscreen_message_screen);

        screen = new UserChatScreen();

        if(chatOrProject != null) {
            screen.setChat(chatOrProject, this, messageScreen);
        } else {
            Log.i("Chat", "No chatOrProject!");
        }

        this.handleButtons(chatOrProject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project_menu, menu);
        //Only show info button if it's a project
        if(chatOrProject == null || chatOrProject.getProject() == null)
            menu.findItem(R.id.action_info).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                Intent intent = new Intent(this, ProjectInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(PROJECT, chatOrProject.getProject());
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onStart(){
        super.onStart();
        active = true;
    }

    @Override
    public void onStop(){
        super.onStop();
        active = false;
    }

    public static void setChat(ChatOrProject chatOrProject, Context context){
        screen.setChat(chatOrProject, context, messageScreen);
    }

    public void handleButtons(ChatOrProject chatOrProject){
        ImageButton btn = findViewById(R.id.chatscreen_button_addon);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                moveAddonScreenUpDown();
            }
        });


        Button createTemplate = findViewById(R.id.chatscreen_button_template);
        createTemplate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                screen.startMessageBuilder(v.getContext(), chatOrProject.getStatus(), chatOrProject.getId());
            }
        });

        Button createMeme = findViewById(R.id.chatscreen_button_meme);
        createMeme.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                hideKeyboard();
                Intent goToMemeGenerator = new Intent(getApplicationContext(), MemeTemplateSelectionActivity.class);
                startActivity(goToMemeGenerator);
                //  screen.sendMessage(0, v.getContext(), chatOrProject.getId(),  chatOrProject.getStatus(), "","", "");

            }
        });

        ImageButton sendButton = findViewById(R.id.chatscreen_button_send);
        EditText editText = findViewById(R.id.chatscreen_message_field);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screen.sendMessage(0, v.getContext(), chatOrProject.getId(), chatOrProject.getStatus(), editText.getText().toString(), "", "");
                editText.setText("");
                hideKeyboard();
            }
        });
    }

    public void moveAddonScreenUpDown(){
        LinearLayout layout = findViewById(R.id.chatscreen_menu_bottom);

        if(layout.getVisibility() == View.GONE){
            //Show the panel
            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.new_chat_message_up);
            layout.startAnimation(bottomUp);
            layout.setVisibility(View.VISIBLE);
        } else {
            //Hide panel
            Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.new_chat_message_down);
            layout.startAnimation(bottomDown);
            layout.setVisibility(View.GONE);
        }
    }

    //Hide Keyboard
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        LinearLayout layout = findViewById(R.id.chatscreen_menu_bottom);
        layout.setVisibility(View.GONE);
    }
}
