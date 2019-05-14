package com.codeofthewe.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class RestControllerGame {

    @Autowired
    private GameRepository repositoryGame;
    @Autowired
    private GamePlayerRepository repositoryGamePlayer;
    @Autowired
    private PlayerRepository playerRepository;

    @RequestMapping("/games")
    public List<Object> dameTodosLosjuegos() {
        return repositoryGame.findAll().stream().map(game -> armarGamesToDTO(game)).collect(toList());

    }

    private Map<String, Object> armarGamesToDTO(Game game){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("crationDate", game.getCreationDate().getTime());
        dto.put("gamePlayers", game.getGamePlayers()
                .stream()
                .map(gamePlayer -> armarGamePlayersToDTO(gamePlayer))
                .collect(toList()));
        return dto;
    }

    @RequestMapping("/game_view/{Id}")
    public Map<String, Object> dameJuegoConBarcos(@PathVariable Long Id) {
        return armarUnSoloGameToDTO(repositoryGamePlayer.findById(Id).get());

    }

    private Map<String, Object> armarUnSoloGameToDTO(GamePlayer gameplayer){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gameplayer.getPartidaJuego().getId());
        dto.put("created", gameplayer.getPartidaJuego().getCreationDate().getTime());
        dto.put("gamePlayers", gameplayer.getPartidaJuego().getGamePlayers()
                .stream()
                .map(gamePlayer2 -> armarGamePlayersToDTO(gamePlayer2))
                .collect(toList()));
        dto.put("ships", gameplayer.getGamePlayerOfship().stream().map(ship -> armarDtoShip(ship)).collect(toList()));
        dto.put("salvoes", getSalvoList(gameplayer.getPartidaJuego()));
        return dto;
    }

    private List<Map<String,Object>> getSalvoList(Game game){
        List<Map<String,Object>> myList = new ArrayList<>();
        game.getGamePlayers().forEach(gamePlayer -> myList.addAll(armarSalvoList(gamePlayer.getGamePlayerIdOfSalvo())));
        return myList;
    }

    private List<Map<String,Object>> armarSalvoList(Set<Salvo> salvoes){
        return salvoes.stream().map(salvo -> armarDtoSalvo(salvo)).collect(Collectors.toList());
    }

    private Map<String,Object> armarDtoSalvo(Salvo salvo) {
        Map<String,Object> dtoSalvo = new LinkedHashMap<String, Object>();
        dtoSalvo.put("turn", salvo.getTurn());
        dtoSalvo.put("player", salvo.getGamePlayerIdOfSalvo().getPartidaPlayer().getId());
        dtoSalvo.put("locations", salvo.getSalvoLocations());
        return dtoSalvo;
    }

    private Map<String, Object> armarGamePlayersToDTO(GamePlayer gamePlayer){
        Map<String, Object> dto2 = new LinkedHashMap<String, Object>();
        dto2.put("id", gamePlayer.getId());
        dto2.put("FechaGamePlayer", gamePlayer.getJoinDate());
        dto2.put("player", gamePlayer.getPartidaPlayer());

        return dto2;
    }

    private Map<String, Object> armarDtoShip(Ship ship){
        Map<String, Object> dto3 = new LinkedHashMap<String, Object>();
        dto3.put("type", ship.getTipoBarco());
        dto3.put("locations", ship.getShipLocation());


        return dto3;
    }

    ///api/leaderBoard ----> playerRepository
    @RequestMapping("/leaderBoard")
    public List<Object> damePuntuacion() {
        return playerRepository.findAll().stream().map(player -> armarPlayerToDTO(player)).collect(toList());

    }

    private Map<String, Object> armarPlayerToDTO(Player player){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("email", player.getEmail());
        dto.put("score", armarScoreToDTO(player));
        return dto;
    }

    private Map<String, Object> armarScoreToDTO(Player player){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("name", player.getEmail());
        dto.put("total", player.getTotal(player.getScores()));
        dto.put("won", player.getWins(player.getScores()));
        dto.put("lost", player.getLoses(player.getScores()));
        dto.put("tied", player.getDraws(player.getScores()));
        return dto;
    }
}

/* @RequestMapping("/game")
    public List<Long> getAll() {
        return repositoryGame.findAll().stream().map(sub -> sub.getId()).collect(toList());
    }

    @RequestMapping("/games")
    public List<Date> getAll2() {
        return repositoryGame.findAll().stream().map(sub -> sub.getCreationDate()).collect(toList());
    }

   @RequestMapping("/game3")
    public Map<String, ArrayList<Game>> getAll3(){
        Map<String, ArrayList<Game>> juegos = new HashMap<String, ArrayList<Game>>();
       ArrayList<Game> juegoRepo = new ArrayList<Game>();
       juegoRepo = new ArrayList<Game>(repositoryGame.findAll());
       juegos.put("1",juegoRepo);
        //juegos.put(1, repositoryGame.getOne((long) 1).getCreationDate());
        //juegos.put(repositoryGame.findAll().stream().map(sub -> sub.getId()).collect(toList()), );


        return juegos;
    }
*/
