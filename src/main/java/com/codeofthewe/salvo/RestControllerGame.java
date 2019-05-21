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
import static java.util.stream.Collectors.toSet;

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
    private ShipRepository shipRepository;
    @Autowired
    private SalvoRepository salvoRepository;
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
        dto.put("salvoes", getSalvoList(game));
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

    @RequestMapping(path = "/game/{IdGame}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> unirteAUnJuego(@PathVariable Long IdGame,Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        Player authenticatedPlayer = getAuthentication(authentication);
        if(authenticatedPlayer == null){
            return new ResponseEntity<>(makeMap("error","No estas logueado"), HttpStatus.FORBIDDEN);
        } else {

            Game gameAux = repositoryGame.findById(IdGame).get();
            int cantidadDeGPPorJuego = gameAux.getGamePlayers().size();

            if(gameAux == null){
                return new ResponseEntity<>(makeMap("error","No hay tal juego"), HttpStatus.FORBIDDEN);
            }
            if(cantidadDeGPPorJuego > 1){
                return new ResponseEntity<>(makeMap("error","El juego está lleno"), HttpStatus.FORBIDDEN);
            }
            GamePlayer auxGameP = new GamePlayer(authenticatedPlayer,gameAux);
            repositoryGamePlayer.save(auxGameP);
            return new ResponseEntity<>(makeMap("gpid", auxGameP.getId()), HttpStatus.CREATED);

        }
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
        dtoSalvo.put("locations", salvo.getLocations());
        return dtoSalvo;
    }

    private Map<String, Object> armarGamePlayersToDTO(GamePlayer gamePlayer){
        Map<String, Object> dto2 = new LinkedHashMap<String, Object>();
        dto2.put("gpid", gamePlayer.getId());
        dto2.put("FechaGamePlayer", gamePlayer.getJoinDate());
        dto2.put("player", armarPlayerToDTO(gamePlayer.getPartidaPlayer())); //.forEach(gamePlayer -> myList.addAll(armarSalvoList(gamePlayer.getGamePlayerIdOfSalvo())))
        dto2.put("ships", gamePlayer.getGamePlayerOfship().stream().map(ship -> armarDtoShip(ship)).collect(toList()));

        return dto2;
    }

    private Map<String, Object> armarDtoShip(Ship ship){
        Map<String, Object> dto3 = new LinkedHashMap<String, Object>();
        dto3.put("type", ship.getType());
        dto3.put("locations", ship.getLocations());


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

    //TASK 3 - Programa 5
    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> agregarBarcos(@PathVariable Long gamePlayerId,
                                                             Authentication authentication,
                                                             @RequestBody List<Ship> ships) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        Player authenticatedPlayer = getAuthentication(authentication);
        if(authenticatedPlayer == null) {
            return new ResponseEntity<>(makeMap("error", "No estas logueado"), HttpStatus.UNAUTHORIZED );
        }

        GamePlayer gamePlayer = repositoryGamePlayer.findById(gamePlayerId).get();
        if(gamePlayer == null){
            return new ResponseEntity<>(makeMap("error","No hay tal juego"), HttpStatus.UNAUTHORIZED);
        }

        if(wrongGamePlayer(gamePlayer, authenticatedPlayer)){
            return new ResponseEntity<>(makeMap("error","Juego equivocado"), HttpStatus.UNAUTHORIZED);
        } else {
            if(gamePlayer.getGamePlayerOfship().isEmpty()){
                ships.forEach(ship -> ship.setGamePlayerOfship(gamePlayer));
                gamePlayer.setGamePlayerOfship(ships.stream().collect(toSet()));
                shipRepository.saveAll(ships);
                return new ResponseEntity<>(makeMap("Ok","barcos guardados"), HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(makeMap("error","El jugador ya colocó los barcos en el Juego"), HttpStatus.UNAUTHORIZED);
            }

        }

    }

    private boolean wrongGamePlayer(GamePlayer gamePlayerAux, Player PlayerLogueado){
        boolean correctGP = gamePlayerAux.getPartidaPlayer().getId() != PlayerLogueado.getId();
        return correctGP;
    }

    @RequestMapping("/game/players/{gamePlayerId}/ships")
    public List<Object> dameBarcosDeunGamePlayer(@PathVariable Long gamePlayerId){
        return repositoryGamePlayer.findById(gamePlayerId).get().getGamePlayerOfship().stream()
                .map(ship -> armarDtoShip(ship))
                .collect(toList());

    }

    //END - TASK 3 - Programa 5

    //TASK 4 - Programa 5
    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> agregarSalvos(@PathVariable Long gamePlayerId,
                                                             Authentication authentication,
                                                             @RequestBody Salvo Salvoes) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        Player authenticatedPlayer = getAuthentication(authentication);
        if(authenticatedPlayer == null) {
            return new ResponseEntity<>(makeMap("error", "No estas logueado"), HttpStatus.UNAUTHORIZED );
        }

        GamePlayer gamePlayer = repositoryGamePlayer.findById(gamePlayerId).get();
        if(gamePlayer == null){
            return new ResponseEntity<>(makeMap("error","No hay tal juego"), HttpStatus.UNAUTHORIZED);
        }

        if(wrongGamePlayer(gamePlayer, authenticatedPlayer)){
            return new ResponseEntity<>(makeMap("error","Juego equivocado"), HttpStatus.UNAUTHORIZED);
        }

        if(gamePlayerConSalvos(gamePlayer, Salvoes)){
            return new ResponseEntity<>(makeMap("error","Juego ya tiene Salvos"), HttpStatus.UNAUTHORIZED);
        } else {
            if(gamePlayer.getGamePlayerOfship().isEmpty()){
               /* Salvoes.forEach(salvo -> salvo.setGamePlayerIdOfSalvo(gamePlayer));
                gamePlayer.setGamePlayerIdOfSalvo(Salvoes.stream().collect(toSet()));
                salvoRepository.saveAll(Salvoes);*/
                Salvoes.setGamePlayerIdOfSalvo(gamePlayer);
                salvoRepository.save(Salvoes);
                return new ResponseEntity<>(makeMap("Ok","salvos guardados"), HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(makeMap("error","El jugador ya colocó los salvos en el turno"), HttpStatus.UNAUTHORIZED);
            }

        }

    }

    //gamePlayerConSalvos
    private boolean gamePlayerConSalvos(GamePlayer gamePlayerAux, Salvo salvoes ){

        boolean tieneSalvo = false;
        int turno = salvoes.getTurn();
        for (Salvo s:gamePlayerAux.getGamePlayerIdOfSalvo()){
            if(s.getTurn() == salvoes.getTurn() ){
                tieneSalvo = true;
            }
        }

        return tieneSalvo;
    }

    //END - TASK 4 - Programa 5
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
