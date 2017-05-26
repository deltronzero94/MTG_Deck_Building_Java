/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JSON_Import;

/**
 *
 * @author reticent
 */
public class MTG_Set {
    
    //Declared Member Variables
    private String name;
    private String code;
    private String releaseDate;
    private String border;
    private String type;
    private Card [] cards;
    private boolean onlineOnly;   
    
    //Default Constructor
    public MTG_Set()
    {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Card[] getCards() {
        return cards;
    }

    public void setCards(Card[] cards) {
        this.cards = cards;
    }

    public boolean isOnlineOnly() {
        return onlineOnly;
    }

    public void setOnlineOnly(boolean onlineOnly) {
        this.onlineOnly = onlineOnly;
    }
}
