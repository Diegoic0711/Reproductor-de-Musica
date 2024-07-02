package com.example.reproductordemusica;

public class ModelFirebase {
    private String song;
    private String artist;
    private String cover_image;
    private String url;
    private boolean isFavorite;



    public ModelFirebase() {
    }

    public ModelFirebase(String song, String artist, String coverImage) {
        this.song = song;
        this.artist = artist;
        this.cover_image = coverImage;
        this.url=url;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

}
