package com.codeofthewe.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player playerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game gameId;

    private float score;

    private Date finishDate;

    public Score() {
    }

    public Score(Player playerId, Game gameId, float score, Date date) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.score = score;
        this.finishDate = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    public Player getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Player playerId) {
        this.playerId = playerId;
    }

    @JsonIgnore
    public Game getGameId() {
        return gameId;
    }

    public void setGameId(Game gameId) {
        this.gameId = gameId;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }
}


