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
    private ScoreRepository scoreRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    private ArrayList<String> hitLocations = new ArrayList<String>();

    int carrierDamage = 0;
    int battleshipDamage = 0;
    int submarineDamage = 0;
    int destroyerDamage = 0;
    int patrolboatDamage = 0;

    int carrierDamage2 = 0;
    int battleshipDamage2 = 0;
    int submarineDamage2 = 0;
    int destroyerDamage2 = 0;
    int patrolboatDamage2 = 0;



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
        GamePlayer gPlayerOponente = null;
        if(gameplayer.getPartidaJuego().getGamePlayers().size() > 1) {
            gPlayerOponente = buscarGamePlayerOponente(gameplayer.getPartidaJuego(), gameplayer);
        }
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gameplayer.getPartidaJuego().getId());
        dto.put("created", gameplayer.getPartidaJuego().getCreationDate().getTime());

        dto.put("gameState", getGameState(gameplayer, gPlayerOponente));

        dto.put("gamePlayers", gameplayer.getPartidaJuego().getGamePlayers()
                .stream()
                .map(gamePlayer2 -> armarGamePlayersToDTOParaGame_view(gamePlayer2))
                .collect(toList()));
        dto.put("ships", gameplayer.getGamePlayerOfship().stream().sorted(Comparator.comparing(Ship::getType)).map(ship -> armarDtoShip(ship)).collect(toList()));
        dto.put("salvoes", getSalvoList(gameplayer.getPartidaJuego()));
        if(gPlayerOponente != null) {
            dto.put("hits", ParaArmarHits(gameplayer.getPartidaJuego(), gameplayer, gPlayerOponente));
        }else{
            dto.put("hits", hitsVacio());
        }
        return dto;
    }

    //TASK 6 - Programa 5
    private String getGameState(GamePlayer gameplayer, GamePlayer gPlayerOponente){
    String estadoJuego="WAIT";
    GamePlayer self = gameplayer;
        int carrierDamageGameStateSelf = 0;
        int battleshipDamageGameStateSelf = 0;
        int submarineDamageGameStateSelf = 0;
        int destroyerDamageGameStateSelf = 0;
        int patrolboatDamageGameStateSelf = 0;

    GamePlayer opponent = gPlayerOponente;
        int carrierDamageGameStateOpponent = 0;
        int battleshipDamageGameStateOpponent = 0;
        int submarineDamageGameStateOpponent = 0;
        int destroyerDamageGameStateOpponent = 0;
        int patrolboatDamageGameStateOpponent = 0;


    if(self.getGamePlayerOfship().size() == 0){
        return estadoJuego = "PLACESHIPS";
    }
    if(opponent == null){
        return estadoJuego = "WAITINGFOROPP";
    }
    if(opponent.getGamePlayerOfship().size() == 0){
        return estadoJuego = "WAIT";
    }

    //para verificar el daño a los barcos
        Boolean selfAllShipSunk = false;
        carrierDamage2 = 0;
        battleshipDamage2 = 0;
        submarineDamage2 = 0;
        destroyerDamage2 = 0;
        patrolboatDamage2 = 0;
        //getHits2(opponent, self);
        getHits2(self ,opponent);

        carrierDamageGameStateSelf = carrierDamage2;
        battleshipDamageGameStateSelf = battleshipDamage2;
        submarineDamageGameStateSelf = submarineDamage2;
        destroyerDamageGameStateSelf = destroyerDamage2;
        patrolboatDamageGameStateSelf = patrolboatDamage2;
        if(carrierDamageGameStateSelf == 5
                && battleshipDamageGameStateSelf ==4
                && submarineDamageGameStateSelf == 3
                && destroyerDamageGameStateSelf == 3
                &&patrolboatDamageGameStateSelf == 2){
            selfAllShipSunk = true;
        }

        Boolean opponentAllShipSunk = false;
        carrierDamage2 = 0;
        battleshipDamage2 = 0;
        submarineDamage2 = 0;
        destroyerDamage2 = 0;
        patrolboatDamage2 = 0;
        //getHits2(self ,opponent);
        getHits2(opponent, self);

        carrierDamageGameStateOpponent = carrierDamage2;
        battleshipDamageGameStateOpponent = battleshipDamage2;
        submarineDamageGameStateOpponent = submarineDamage2;
        destroyerDamageGameStateOpponent = destroyerDamage2;
        patrolboatDamageGameStateOpponent = patrolboatDamage2;
        if(carrierDamageGameStateOpponent == 5
                && battleshipDamageGameStateOpponent ==4
                && submarineDamageGameStateOpponent == 3
                && destroyerDamageGameStateOpponent == 3
                &&patrolboatDamageGameStateOpponent == 2){
            opponentAllShipSunk = true;
        }

    if(self.getGamePlayerIdOfSalvo().size() == opponent.getGamePlayerIdOfSalvo().size() ){
        if(selfAllShipSunk&&opponentAllShipSunk){
            Score scoreAux = new Score(self.getPartidaPlayer(),self.getPartidaJuego(),(float)0.5, new Date());
            if(!existeScore(scoreAux, self.getPartidaJuego())){
                scoreRepository.save(scoreAux);
            }
            return estadoJuego = "TIE";
        }

        if(selfAllShipSunk){
            Score scoreAux = new Score(self.getPartidaPlayer(),self.getPartidaJuego(),(float)0.0, new Date());
            if(!existeScore(scoreAux, self.getPartidaJuego())){
                scoreRepository.save(scoreAux);
            }
            return estadoJuego = "LOST";
        }

        if(opponentAllShipSunk){
            Score scoreAux = new Score(self.getPartidaPlayer(),self.getPartidaJuego(),(float)1.0, new Date());
            if(!existeScore(scoreAux, self.getPartidaJuego())){
                scoreRepository.save(scoreAux);
            }
            return estadoJuego = "WON";
        }
    }

    if(self.getGamePlayerIdOfSalvo().size() <= turnOfGame(self , opponent)){

        return estadoJuego = "PLAY";
    }

    return estadoJuego;
    }

    // CALCULAR DAÑO PARA GAMESTATE
    private List<Map> getHits2(GamePlayer self, GamePlayer opponent){
        List<Map> dto = new ArrayList<>();

        //LOS QUE USO YO
        /*int carrierDamage2 = 0;
        int battleshipDamage2 = 0;
        int submarineDamage2 = 0;
        int destroyerDamage2 = 0;
        int patrolboatDamage2 = 0;*/
        List<String> carrierLocations = new ArrayList<>();
        List<String> destroyerLocations = new ArrayList<>();
        List<String> submarineLocations = new ArrayList<>();
        List<String> patrolboatLocations = new ArrayList<>();
        List<String> battleshipLocations = new ArrayList<>();
        for (Ship ship: self.getGamePlayerOfship()) {
            switch (ship.getType()){
                case "carrier":
                    carrierLocations = ship.getLocations();
                    break ;
                case "battleship" :
                    battleshipLocations = ship.getLocations();
                    break;
                case "destroyer":
                    destroyerLocations = ship.getLocations();
                    break;
                case "submarine":
                    submarineLocations = ship.getLocations();
                    break;
                case "patrolboat":
                    patrolboatLocations = ship.getLocations();
                    break;
            }
        }
        for (Salvo salvo : opponent.getGamePlayerIdOfSalvo()) {
            Integer carrierHitsInTurn = 0;
            Integer battleshipHitsInTurn = 0;
            Integer submarineHitsInTurn = 0;
            Integer destroyerHitsInTurn = 0;
            Integer patrolboatHitsInTurn = 0;

            Map<String, Object> hitsMapPerTurn = new LinkedHashMap<>();
            Map<String, Object> damagesPerTurn = new LinkedHashMap<>();
            List<String> salvoLocationsList = new ArrayList<>();
            List<String> hitCellsList = new ArrayList<>();
            salvoLocationsList.addAll(salvo.getLocations());
            for (String salvoShot : salvoLocationsList) {
                if (carrierLocations.contains(salvoShot)) {
                    carrierDamage2++;
                    carrierHitsInTurn++;
                    hitCellsList.add(salvoShot);

                }
                if (battleshipLocations.contains(salvoShot)) {
                    battleshipDamage2++;
                    battleshipHitsInTurn++;
                    hitCellsList.add(salvoShot);

                }
                if (submarineLocations.contains(salvoShot)) {
                    submarineDamage2++;
                    submarineHitsInTurn++;
                    hitCellsList.add(salvoShot);

                }
                if (destroyerLocations.contains(salvoShot)) {
                    destroyerDamage2++;
                    destroyerHitsInTurn++;
                    hitCellsList.add(salvoShot);

                }
                if (patrolboatLocations.contains(salvoShot)) {
                    patrolboatDamage2++;
                    patrolboatHitsInTurn++;
                    hitCellsList.add(salvoShot);

                }
            }

            dto.add(hitsMapPerTurn);
        }
        return dto;
    }
    //END - CALCULAR DAÑO PARA GAMESTATE


    private Boolean existeScore(Score scoreAux, Game game){
        Boolean yaTieneScore = false;
        Score scoreRepo = scoreRepository.findById(scoreAux.getId()).orElse(null);
        if(scoreRepo != null && scoreRepo.getGameId().getId()==game.getId()){
            yaTieneScore = true;
        }
        return yaTieneScore;
    }

    private int turnOfGame(GamePlayer self , GamePlayer opponent){
        int turno = 0;
        int selfTurno = 0;
        int opponentTurno = 0;
        selfTurno = self.gamePlayerIdOfSalvo.size();
        opponentTurno = opponent.gamePlayerIdOfSalvo.size();

        if(selfTurno == opponentTurno){
            return turno = selfTurno;
        } else {
            turno = (int)((float)((selfTurno+opponentTurno)/2 ) +0.5);
            return turno;
        }

    }

    private Map<String, Object> hitsVacio(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("self", listVacia());
        dto.put("opponent", listVacia() );
        return dto;
    }
    private List<String> listVacia(){
        List<String> myList = new ArrayList<>();
        return myList;
    }
    //END-TASK 6 - Programa 5

    //TASK 5 - Programa 5
    private Map<String, Object> ParaArmarHits(Game game, GamePlayer gameplayer, GamePlayer gP_Oponente){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        //GamePlayer gamePlayerOponente = gP_Oponente;
        GamePlayer gamePlayerOponente = buscarGamePlayerOponente(game, gameplayer);
        carrierDamage = 0;
        battleshipDamage = 0;
        submarineDamage = 0;
        destroyerDamage = 0;
        patrolboatDamage = 0;
        //dto.put("self", getHits(gameplayer, gamePlayerOponente));
        dto.put("self", getHits(gamePlayerOponente, gameplayer));

        carrierDamage = 0;
        battleshipDamage = 0;
        submarineDamage = 0;
        destroyerDamage = 0;
        patrolboatDamage = 0;
        //dto.put("opponent", getHits(gamePlayerOponente,gameplayer));
        dto.put("opponent", getHits(gameplayer, gamePlayerOponente));


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
                    if (salvoLocations.get(x).equals(boatLocations.get(y))) {
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
        int carrierHits = cantDeHitsAlBarco(salvo,gamePlayerOponente,"carrier");
        int battleshipHits = cantDeHitsAlBarco(salvo,gamePlayerOponente,"battleship");
        int submarineHits = cantDeHitsAlBarco(salvo,gamePlayerOponente,"submarine");
        int destroyerHits = cantDeHitsAlBarco(salvo,gamePlayerOponente,"destroyer");
        int patrolboatHits = cantDeHitsAlBarco(salvo,gamePlayerOponente,"patrolboat");
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
                if (s.getType().equals(tipoBarco.trim())) {
                    boatHitsLocations = new ArrayList<String>(s.getLocations());
                }
            }

            ArrayList<String> salvoLocations = new ArrayList<String>();
            salvoLocations = new ArrayList<String>(salvo.getLocations());

            for(int x = 0; x < salvoLocations.size(); x++) {
                for (int y = 0; y < boatHitsLocations.size(); y++) {
                    if (salvoLocations.get(x).equals(boatHitsLocations.get(y))) {
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
        dto2.put("joinDate", gamePlayer.getJoinDate().getTime());
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


