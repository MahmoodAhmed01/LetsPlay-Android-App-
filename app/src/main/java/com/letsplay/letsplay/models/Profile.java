package com.letsplay.letsplay.models;

/**
 * Created by Mahmood on 8/26/2017.
 */

public class Profile {

    String name;
    String pictureUrl;
    Game game;
    String totalPlayed;
    String totalWin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getTotalPlayed() {
        return totalPlayed;
    }

    public void setTotalPlayed(String totalPlayed) {
        this.totalPlayed = totalPlayed;
    }

    public String getTotalWin() {
        return totalWin;
    }

    public void setTotalWin(String totalWin) {
        this.totalWin = totalWin;
    }

    public String getWinPercentage(){

        Integer totalPlayed = Integer.parseInt(this.totalPlayed);
        Integer totalWin = Integer.parseInt(this.totalWin);

        Integer percentage = (int)(totalWin.floatValue()/totalPlayed.floatValue() * 100.0f);

        return percentage.toString();

    }
}
