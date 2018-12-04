package de.sharknoon.slash.ChatMessages;

import java.io.Serializable;
import java.util.List;

import de.sharknoon.slash.HomeScreen.Chat;
import de.sharknoon.slash.HomeScreen.Project;

public class ChatOrProject implements  Serializable{
    private Chat chat;
    private Project project;

    public ChatOrProject(Chat chat, Project project){
        this.chat = chat;
        this.project = project;
    }

    public void setChat(Chat chat){
        this.project = null;
        this.chat = chat;
    }

    public void setProject(Project project){
        this.project = project;
        this.chat = null;
    }

    public Chat getChat(){
        return chat;
    }

    public Project getProject(){
        return project;
    }

    public String getId(){
        if(chat!=null){
            return chat.getId();
        } else if(project!=null){
            return project.getId();
        } else {
            return "NO_ID";
        }
    }

    List<Chat.Message> getMessages(){
        if(chat!=null){
            return chat.getMessages();
        } else if(project!=null){
            return project.getMessages();
        } else {
            return null;
        }
    }

    int getChatOrProject(){
        if(chat!=null){
            return 0;
        } else if (project!=null){
            return 1;
        } else {
            return -1;
        }
    }

    public String getStatus(){
        if(chat!=null){
            return "ADD_CHAT_MESSAGE";
        } else if (project!=null){
            return "ADD_PROJECT_MESSAGE";
        } else {
            return "NO_STATUS";
        }
    }

    public String getName(){
        if(chat!=null){
            return chat.getPartnerUsername();
        } else if (project!=null){
            return project.getName();
        } else {
            return "NO_STATUS";
        }
    }
}
