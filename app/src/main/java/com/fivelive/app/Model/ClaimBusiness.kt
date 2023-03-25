package com.fivelive.app.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*
public class ClaimBusiness {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("zipcode")
    @Expose
    private String zipcode;
    @SerializedName("google_rating")
    @Expose
    private String googleRating;
    @SerializedName("yelp_rating")
    @Expose
    private String yelpRating;
    @SerializedName("cuisines")
    @Expose
    private List<String> cuisines = null;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("fav_status")
    @Expose
    private int favStatus;
    @SerializedName("image")
    @Expose
    private List<String> image = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getGoogleRating() {
        return googleRating;
    }

    public void setGoogleRating(String googleRating) {
        this.googleRating = googleRating;
    }

    public String getYelpRating() {
        return yelpRating;
    }

    public void setYelpRating(String yelpRating) {
        this.yelpRating = yelpRating;
    }

    public List<String> getCuisines() {
        return cuisines;
    }

    public void setCuisines(List<String> cuisines) {
        this.cuisines = cuisines;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getFavStatus() {
        return favStatus;
    }

    public void setFavStatus(int favStatus) {
        this.favStatus = favStatus;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

}*/
class ClaimBusiness {
    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("zipcode")
    @Expose
    var zipcode: String? = null

    @SerializedName("google_rating")
    @Expose
    var googleRating: String = ""

    @SerializedName("yelp_rating")
    @Expose
    var yelpRating: String = ""

    @SerializedName("latitude")
    @Expose
    var latitude: String = ""

    @SerializedName("longitude")
    @Expose
    var longitude: String = ""

    @SerializedName("category")
    @Expose
    var category: List<String> = listOf()

    @SerializedName("distance")
    @Expose
    var distance: String? = null

    @SerializedName("fav_status")
    @Expose
    var favStatus = 0

    @SerializedName("image")
    @Expose
    var image: List<String> = listOf()
}