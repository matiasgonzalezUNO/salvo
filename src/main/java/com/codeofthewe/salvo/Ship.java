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
    private List<String> shipLocations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayerIdOfship")
    private GamePlayer gamePlayerOfship;

    private String tipoBarco;

    public Ship() {
    }



    public Ship( GamePlayer gamePlayerId, String tipoB, List<String> shipLocation) {

        this.gamePlayerOfship = gamePlayerId;
        this.shipLocations = shipLocation;
        this.tipoBarco = tipoB;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public List<String> getShipLocation() {
        return shipLocations;
    }

    public void setShipLocation(List<String> shipLocation) {
        this.shipLocations = shipLocation;
    }

    public String getTipoBarco() {
        return tipoBarco;
    }

    public void setTipoBarco(String tipoBarco) {
        this.tipoBarco = tipoBarco;
    }
}
