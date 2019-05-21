package com.codeofthewe.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;



    @ElementCollection
    @Column(name="shipLocation")
    private List<String> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayerIdOfship")
    private GamePlayer gamePlayerOfship;

    private String type;

    public Ship() {
    }

    public Ship( GamePlayer gamePlayerOfship, String type,List<String> locations) {
        this.locations = locations;
        this.gamePlayerOfship = gamePlayerOfship;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GamePlayer getGamePlayerOfship() {
        return gamePlayerOfship;
    }

    public void setGamePlayerOfship(GamePlayer gamePlayerOfship) {
        this.gamePlayerOfship = gamePlayerOfship;
    }
}
