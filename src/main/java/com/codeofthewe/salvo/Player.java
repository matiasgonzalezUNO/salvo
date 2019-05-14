package com.codeofthewe.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    @OneToMany(mappedBy="partidaPlayer", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="playerId", fetch=FetchType.EAGER)
    private Set<Score> scores;

    public Player() { }

    public Player(String first1) {
        this.firstName = first1;
    }

    public Player(String first, String last) {
        this.firstName = first;
        this.lastName = last;
    }

    public Player(String first, String last, String mail) {
        this.firstName = first;
        this.lastName = last;
        this.email = mail;
    }

    public Player(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return firstName + " " + lastName;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPartidaPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    @JsonIgnore
    public List<Game> getGame() {
        return gamePlayers.stream().map(sub -> sub.getPartidaJuego()).collect(toList());
    }

    public double getTotal(Set<Score> scores){
        return  getWins(scores) + getLoses(scores)*0 + getDraws(scores)*0.5;
    }

    public long getWins(Set<Score> scores){
        return scores.stream().filter(score -> score.getScore() == 1.0).count();
    }

    public long getLoses(Set<Score> scores){
        return scores.stream().filter(score -> score.getScore() == 0).count();
    }

    public long getDraws(Set<Score> scores){
        return scores.stream().filter(score -> score.getScore() == 0.5).count();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}