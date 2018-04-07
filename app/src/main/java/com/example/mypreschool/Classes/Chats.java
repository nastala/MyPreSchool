package com.example.mypreschool.Classes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Chats {
    private String title, lastMessage;
    private long timestamp, fark;
    private String zamanfarki;

    public Chats(String title, String lastMessage, long timestamp) {
        this.title = title;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public Chats() {
    }

    public String getTitle() {
        return title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
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
