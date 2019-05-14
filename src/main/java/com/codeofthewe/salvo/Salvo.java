package com.codeofthewe.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private int turn;

    /*@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayerId")
    private GamePlayer gamePlayerId;*/

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="salvoLocations")
    private List<String> salvoLocations = new ArrayList<>();



    public Salvo() {
    }

    public Salvo(GamePlayer gamePlayerIdOfSalvo, List<String> salvoLocations) {
        this.gamePlayer = gamePlayerIdOfSalvo;
        this.salvoLocations = salvoLocations;
    }

    public Salvo(int turn, GamePlayer gamePlayerIdOfSalvo, List<String> salvoLocations) {
        this.turn = turn;
        this.gamePlayer = gamePlayerIdOfSalvo;
        this.salvoLocations = salvoLocations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GamePlayer getGamePlayerIdOfSalvo() {
        return gamePlayer;
    }

    public void setGamePlayerIdOfSalvo(GamePlayer gamePlayerIdOfSalvo) {
        this.gamePlayer = gamePlayerIdOfSalvo;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }
}
