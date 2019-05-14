package com.codeofthewe.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private Date creationDate;

    @OneToMany(mappedBy="partidaJuego", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="gameId", fetch=FetchType.EAGER)
    private Set<Score> scores;



    public Game() {
    }

    public Game(Date creationDate) {
        this.creationDate = creationDate;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreationDate() {

        return creationDate;
    }

    public void setCreationDate(Date creationDate) {

        this.creationDate = creationDate;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPartidaJuego(this);
        gamePlayers.add(gamePlayer);
    }

    @JsonIgnore
    public List<Player> getPlayer() {
        return gamePlayers.stream().map(sub -> sub.getPartidaPlayer()).collect(toList());
    }


    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

}
