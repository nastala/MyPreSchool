package com.example.mypreschool.Classes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by sezgi on 3/29/2018.
 */

public class ChatMessage {
    private String username, uid, message, id, zamanfarki;
    private long timestamp, fark;

    public ChatMessage(String username, String uid, String message, long timestamp) {
        this.username = username;
        this.uid = uid;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ChatMessage() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getZamanfarki() {
        if(timestamp == 0)
            return null;

        Date simdi = Calendar.getInstance().getTime();
        Date tarih = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(simdi);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        if(!tarih.before(calendar.getTime())){
            zamanfarki = simpleDateFormat.format(tarih);
        }
        else{
            fark = getDateDiff(tarih, calendar.getTime(), TimeUnit.DAYS);
            if (fark < 1) {
                zamanfarki = "Yesterday " + simpleDateFormat.format(tarih);

            } else {
                zamanfarki = (fark + 1) + " days before " + simpleDateFormat.format(tarih);
            }
        }

        return zamanfarki;
    }

    private long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
