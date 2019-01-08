package com.example.derga.droshed;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

/**
 * Created by Gwenael on 30/04/2017.
 */

public class User implements Parcelable
{
    private final String login;
    private final String password;

    private User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in.readString(), in.readString());
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static User createUser(String login, String password){
        return new User(login, password);
    }

    public String getLogin() {
        return login;
    }

    private String getPassword() {
        return password;
    }

    public String encode64() {
        String encoding = Base64.encodeToString((getLogin()+":"+getPassword()).getBytes(),Base64.DEFAULT);
        return encoding;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(login);
        dest.writeString(password);
    }
}
