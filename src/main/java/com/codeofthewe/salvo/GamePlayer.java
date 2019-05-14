package com.codeofthewe.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player partidaPlayer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="juego_id")
    private Game partidaJuego;

    public Player getPartidaPlayer() {
        return partidaPlayer;
    }

    public void setPartidaPlayer(Player partidaPlayer) {
        this.partidaPlayer = partidaPlayer;
    }

    @JsonIgnore
    public Game getPartidaJuego() {
        return partidaJuego;
    }

    public void setPartidaJuego(Game partidaJuego) {
        this.partidaJuego = partidaJuego;
    }

    private Date joinDate;

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    @OneToMany(mappedBy="gamePlayerOfship", fetch=FetchType.EAGER)
    Set<Ship> gamePlayerOfship;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Salvo> gamePlayerIdOfSalvo;

    public Set<Ship> getGamePlayerOfship() {
        return gamePlayerOfship;
    }

    public void setGamePlayerOfship(Set<Ship> gamePlayerOfship) {
        this.gamePlayerOfship = gamePlayerOfship;
    }

    public GamePlayer() { }

    public GamePlayer(Player partidaPlayer, Game partidaJuego) {
        this.partidaPlayer = partidaPlayer;
        this.partidaJuego = partidaJuego;
        this.joinDate = new Date();
    }

    public Set<Salvo> getGamePlayerIdOfSalvo() {
        return gamePlayerIdOfSalvo;
    }

    public void setGamePlayerIdOfSalvo(Set<Salvo> gamePlayerIdOfSalvo) {
        this.gamePlayerIdOfSalvo = gamePlayerIdOfSalvo;
    }
}
