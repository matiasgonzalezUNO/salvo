package com.codeofthewe.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    PasswordEncoder passwordEncoder;

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<String> loguear(@RequestParam String username, @RequestParam String password) {
        if (username.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findByEmail(username);
        if (player != null) {
            return new ResponseEntity<>("Name already used", HttpStatus.CONFLICT);
        }

        playerRepository.save(new Player(username,passwordEncoder.encode(password)));
        return new ResponseEntity<>("Named added", HttpStatus.CREATED);


    }

    @RequestMapping("/games")
    public Map<String, Object> makeLogeedPlayer(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        authentication = SecurityContextHolder.getContext().getAuthentication();
        Player authenticatedPlayer = getAuthentication(authentication);
        if(authenticatedPlayer == null){
            dto.put("player","Guest");
            dto.put("games",dameTodosLosjuegos());
        } else {
            dto.put("player",loggedPlayerDTO(authenticatedPlayer));
            dto.put("games",dameTodosLosjuegos());
        }

        return  dto;
    }

    public List<Object> dameTodosLosjuegos() {
        return repositoryGame.findAll().stream().map(game -> armarGamesToDTO(game)).collect(toList());

    }

    private Map<String, Object> armarGamesToDTO(Game game){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate().getTime());
        dto.put("gamePlayers", game.getGamePlayers()
                .stream()
                .map(gamePlayer -> armarGamePlayersToDTO(gamePlayer))
                .collect(toList()));
        dto.put("scores", makeListGamesScores(game.getScores()));
        dto.put("Separacion de games","*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
        return dto;
    }

    //Task 5-start
    public List<Object> makeListGamesScores(Set<Score> scores) {
        return scores
                .stream()
                .map(score -> makeScoreDTO(score))
                .collect(Collectors.toList());
    }
    private Map<String, Object> makeScoreDTO(Score score) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("playerID", score.getPlayerId().getId());
        dto.put("score", score.getScore());
        dto.put("finishDate", score.getFinishDate());
        return dto;
    }
    //Task 5 - end

    private Player getAuthentication(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        } else {
            return (playerRepository.findByEmail(authentication.getName()));
        }
    }

    private Map<String, Object> loggedPlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("name",player.getEmail());
        return dto;
    }

    private List<Map<String, Object>> loggedPlayerGetGameDTO(Set<Game> games) {
        /*List<Map<String, Object>> dto = new ArrayList<>();
        dto.put("id", player.getId());
        dto.put("name",player.getEmail());
        return dto;*/
        return games.stream().map(gamesLogPlayer -> armarGamesLogPlayerToDTO(gamesLogPlayer)).collect(Collectors.toList());
    }

    private Map<String, Object> armarGamesLogPlayerToDTO(Game game){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("crationDate", game.getCreationDate().getTime());
        dto.put("gamePlayers", game.getGamePlayers()
                .stream()
                .map(gamePlayer -> armarGamePlayersToDTO(gamePlayer))
                .collect(toList()));
        return dto;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createJuego(Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        Player authenticatedPlayer = getAuthentication(authentication);
        if(authenticatedPlayer == null){
            return new ResponseEntity<>(makeMap("error","No name given"), HttpStatus.FORBIDDEN);
        } else {
            Date date = Date.from(java.time.ZonedDateTime.now().toInstant());
            Game auxGame = new Game(date);
            repositoryGame.save(auxGame);

            GamePlayer auxGameP = new GamePlayer(authenticatedPlayer,auxGame);
            repositoryGamePlayer.save(auxGameP);
            //return new ResponseEntity<>("Game Created", HttpStatus.CREATED);
            return new ResponseEntity<>(makeMap("gpid", auxGameP.getId()), HttpStatus.CREATED);
        }
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @RequestMapping("/game_view/{Id}")
    public Map<String, Object> dameJuegoConBarcos(@PathVariable Long Id,Authentication authentication) {

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
        dto2.put("gpid", gamePlayer.getId());
        dto2.put("FechaGamePlayer", gamePlayer.getJoinDate());
        dto2.put("player", armarPlayerToDTO(gamePlayer.getPartidaPlayer())); //.forEach(gamePlayer -> myList.addAll(armarSalvoList(gamePlayer.getGamePlayerIdOfSalvo())))

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

/*
    //viejo codigo /games
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

     */
