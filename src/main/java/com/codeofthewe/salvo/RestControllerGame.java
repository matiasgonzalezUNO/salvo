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

    private ArrayList<String> hitLocations = new ArrayList<String>();

    int carrierDamage = 0;
    int battleshipDamage = 0;
    int submarineDamage = 0;
    int destroyerDamage = 0;
    int patrolboatDamage = 0;



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
                .map(gamePlayer2 -> armarGamePlayersToDTOParaGame_view(gamePlayer2))
                .collect(toList()));
        dto.put("ships", gameplayer.getGamePlayerOfship().stream().sorted(Comparator.comparing(Ship::getType)).map(ship -> armarDtoShip(ship)).collect(toList()));
        dto.put("salvoes", getSalvoList(gameplayer.getPartidaJuego()));
        if(gameplayer.getPartidaJuego().getGamePlayers().size() > 1) {
            dto.put("hits", ParaArmarHits(gameplayer.getPartidaJuego(), gameplayer));
        }
        return dto;
    }

    //TASK 5 - Programa 5
    private Map<String, Object> ParaArmarHits(Game game, GamePlayer gameplayer){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        GamePlayer gamePlayerOponente = buscarGamePlayerOponente(game, gameplayer);
        carrierDamage = 0;
        battleshipDamage = 0;
        submarineDamage = 0;
        destroyerDamage = 0;
        patrolboatDamage = 0;
        dto.put("self", getHits(gameplayer, gamePlayerOponente));
        carrierDamage = 0;
        battleshipDamage = 0;
        submarineDamage = 0;
        destroyerDamage = 0;
        patrolboatDamage = 0;
        dto.put("opponent", getHits(gamePlayerOponente,gameplayer));

        return dto;
    }

    private GamePlayer buscarGamePlayerOponente(Game game, GamePlayer gameplayer){
        GamePlayer gamePlayerAux = null;
        Set<GamePlayer> gamePlayers= game.getGamePlayers();
        for (GamePlayer gp:gamePlayers){
            if(gp.getId() != gameplayer.getId() ){
                gamePlayerAux = gp;
            }
        }
        return gamePlayerAux;
    }

    private List<Object> getHits(GamePlayer gamePlayerHit, GamePlayer gamePlayerOponenteHit){
        //gamePlayer.getGamePlayerOfship().stream().map(ship -> armarDtoShip(ship)).collect(toList()));

        return gamePlayerHit.getGamePlayerIdOfSalvo().stream()
                .sorted(Comparator.comparingInt(Salvo::getTurn))
                        .map(salvoTurn -> armarDtoSalvoPorTurno(salvoTurn,gamePlayerOponenteHit)).collect(toList());
    }

    private Map<String,Object> armarDtoSalvoPorTurno(Salvo salvo, GamePlayer gamePlayerOponente){
        Map<String,Object> dto = new LinkedHashMap<String, Object>();

        //hitLocations.clear();
        dto.put("turn", salvo.getTurn());

        dto.put("damages", dameDaños(salvo, gamePlayerOponente));
        //dto.put("hitLocations", hitLocations);
        dto.put("hitLocations", heAcertado(salvo, gamePlayerOponente));
        //dto.put("hitLocations", salvo.getLocations());
        dto.put("missed", perdidos(salvo,heAcertado(salvo, gamePlayerOponente)));

        return dto;
    }

    private int perdidos(Salvo salvo, List<String> heAcertado){
        int missed = 0;
        missed = salvo.getLocations().size() - heAcertado.size();
        return missed;
    }

    private List<String> heAcertado(Salvo salvo, GamePlayer gamePlayerOponente){
        ArrayList<String> heAcertado = new ArrayList<String>();
        ArrayList<String> boatLocations = new ArrayList<String>();
        for (Ship shipAux : gamePlayerOponente.getGamePlayerOfship()) {
            boatLocations = new ArrayList<String>(shipAux.getLocations());
            ArrayList<String> salvoLocations = new ArrayList<String>();
            salvoLocations = new ArrayList<String>(salvo.getLocations());

            for(int x = 0; x < salvoLocations.size(); x++) {
                for (int y = 0; y < boatLocations.size(); y++) {
                    if (salvoLocations.get(x) == boatLocations.get(y)) {
                        heAcertado.add(salvoLocations.get(x));
                    }
                }
            }
        }

        return heAcertado;
    }
    /*private List<Object> heAcertado(Salvo salvo, GamePlayer gamePlayerOponente){
        return salvo.getLocations().stream().map(salvoAux -> compararSalvoShip(salvoAux,gamePlayerOponente)).collect(toList());
    }

    private String compararSalvoShip(String salvoAux, GamePlayer gamePlayerOponente){
        gamePlayerOponente.getGamePlayerOfship().forEach(ship -> ship.getLocations().forEach(possi -> possi = salvoAux));
    }*/

    private Map<String,Object> dameDaños(Salvo salvo, GamePlayer gamePlayerOponente){
        int carrierHits = cantDeHitsAlBarco(salvo,gamePlayerOponente,"Carrier");
        int battleshipHits = cantDeHitsAlBarco(salvo,gamePlayerOponente,"Battleship");
        int submarineHits = cantDeHitsAlBarco(salvo,gamePlayerOponente,"Submarine");
        int destroyerHits = cantDeHitsAlBarco(salvo,gamePlayerOponente,"Destroyer");
        int patrolboatHits = cantDeHitsAlBarco(salvo,gamePlayerOponente,"PatrolBoat");
        Map<String,Object> dto = new LinkedHashMap<String, Object>();
        dto.put("carrierHits", carrierHits);
        dto.put("battleshipHits", battleshipHits);
        dto.put("submarineHits", submarineHits);
        dto.put("destroyerHits", destroyerHits);
        dto.put("patrolboatHits", patrolboatHits);

        carrierDamage = carrierDamage + carrierHits;
        battleshipDamage = battleshipDamage + battleshipHits;
        submarineDamage = submarineDamage + submarineHits;
        destroyerDamage = destroyerDamage + destroyerHits;
        patrolboatDamage = patrolboatDamage + patrolboatHits;

        dto.put("carrier", carrierDamage);
        dto.put("battleship", battleshipDamage);
        dto.put("submarine", submarineDamage);
        dto.put("destroyer", destroyerDamage);
        dto.put("patrolboat", patrolboatDamage);

        return dto;
    }

    private int cantDeHitsAlBarco(Salvo salvo, GamePlayer gamePlayerOponente,String tipoBarco){
        int cantDeHitsAlBarco = 0;
        ArrayList<String> boatHitsLocations = new ArrayList<String>();

            for (Ship s : gamePlayerOponente.getGamePlayerOfship()) {
                if (s.getType() == tipoBarco) {
                    boatHitsLocations = new ArrayList<String>(s.getLocations());
                }
            }

            ArrayList<String> salvoLocations = new ArrayList<String>();
            salvoLocations = new ArrayList<String>(salvo.getLocations());

            for(int x = 0; x < salvoLocations.size(); x++) {
                for (int y = 0; y < boatHitsLocations.size(); y++) {
                    if (salvoLocations.get(x) == boatHitsLocations.get(y)) {
                        //hitLocations.add(salvoLocations.get(x));
                        cantDeHitsAlBarco = cantDeHitsAlBarco + 1;
                    }
                }
            }

        return cantDeHitsAlBarco;
    }

    //END - TASK 5 - Programa 5

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
        dto2.put("joinDate", gamePlayer.getJoinDate());
        dto2.put("player", armarPlayerToDTO(gamePlayer.getPartidaPlayer())); //.forEach(gamePlayer -> myList.addAll(armarSalvoList(gamePlayer.getGamePlayerIdOfSalvo())))
        dto2.put("ships", gamePlayer.getGamePlayerOfship().stream().map(ship -> armarDtoShip(ship)).collect(toList()));

        return dto2;
    }
    private Map<String, Object> armarGamePlayersToDTOParaGame_view(GamePlayer gamePlayer){
        Map<String, Object> dto2 = new LinkedHashMap<String, Object>();
        dto2.put("gpid", gamePlayer.getId());
        dto2.put("joinDate", gamePlayer.getJoinDate());
        dto2.put("player", armarPlayerToDTO(gamePlayer.getPartidaPlayer())); //.forEach(gamePlayer -> myList.addAll(armarSalvoList(gamePlayer.getGamePlayerIdOfSalvo())))

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
        //dto.put("score", armarScoreToDTO(player));
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
            return new ResponseEntity<>(makeMap("error","Juego ya tiene Salvos para este turno"), HttpStatus.UNAUTHORIZED);
        } else {
                Salvoes.setGamePlayerIdOfSalvo(gamePlayer);
                salvoRepository.save(Salvoes);
                return new ResponseEntity<>(makeMap("Ok","salvos guardados"), HttpStatus.CREATED);
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


