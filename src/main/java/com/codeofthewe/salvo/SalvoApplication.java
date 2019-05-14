package com.codeofthewe.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository repositoryPlayer, GameRepository repositoryGame, GamePlayerRepository repositoryGamePlayer, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository ) {
		return (args) -> {
			Date date = new Date();
			// save a couple of customers

			/*Player p2 = new Player("sebas", "bullian");
			repositoryPlayer.save(p2);
			Game g2 = new Game(date);
			repositoryGame.save(g2);
			repositoryGamePlayer.save(new GamePlayer(p2,g2));*/


			Player p1 = new Player("Lucas", "carri", "Lcarri@gmail");
			repositoryPlayer.save(p1);
			Player p2 = new Player("jet", "black", "JBlack@gmail");
			repositoryPlayer.save(p2);

			Game g1 = new Game(date);
			repositoryGame.save(g1);

			GamePlayer gp1 = new GamePlayer(p1,g1);
			GamePlayer gp2 = new GamePlayer(p2,g1);
			repositoryGamePlayer.save(gp1);
			repositoryGamePlayer.save(gp2);

			List<String> l_1 = new ArrayList<String>();
			l_1.add("H4");
			l_1.add("H2");
			l_1.add("H3");
            l_1.add("H1");
			Ship s1 = new Ship(gp1, "Destroyer", l_1);
			shipRepository.save(s1);

			List<String> l_2 = new ArrayList<String>();
			l_2.add("A1");
			l_2.add("A2");
			l_2.add("A3");
			Ship s2 = new Ship(gp1, "Destroyer", l_2);
			shipRepository.save(s2);

			List<String> l_3 = new ArrayList<String>();
			l_3.add("B8");
			l_3.add("C8");
			l_3.add("D8");
			l_3.add("E8");
			l_3.add("F8");
			Ship s3 = new Ship(gp1, "Destroyer", l_3);
			shipRepository.save(s3);

			List<String> l_4 = new ArrayList<String>();
			l_4.add("I10");
			l_4.add("J10");
			Ship s4 = new Ship(gp2, "boatPatrol", l_4);
			shipRepository.save(s4);

			List<String> l_5 = new ArrayList<String>();
			l_5.add("I3");
			l_5.add("I4");
			l_5.add("I5");
			l_5.add("I6");
			l_5.add("I7");
			Ship s5 = new Ship(gp2, "carrier", l_5);
			shipRepository.save(s5);

			List<String> listSalvo_1 = new ArrayList<String>();
			listSalvo_1.add("A5");
			listSalvo_1.add("B5");
			Salvo salvo1 = new Salvo(1,gp1, listSalvo_1);
			salvoRepository.save(salvo1);

			List<String> listSalvo_2 = new ArrayList<String>();
			listSalvo_2.add("J5");
			listSalvo_2.add("J6");
			Salvo salvo2 = new Salvo(1, gp2, listSalvo_2);
			salvoRepository.save(salvo2);

			List<String> listSalvo_3 = new ArrayList<String>();
			listSalvo_3.add("I5");
			listSalvo_3.add("J5");
			Salvo salvo3 = new Salvo(2, gp1, listSalvo_3);
			salvoRepository.save(salvo3);

			Score Score1 = new Score(p1, g1, (float)1, date);
			scoreRepository.save(Score1);

			Score Score2 = new Score(p2, g1, (float)0, date);
			scoreRepository.save(Score2);

			// *-*-*-*-*-*-*-*-*-*-*JUEGO 2 -*-*-*-*-*-*-*-*-*-*-*-*

			Game g2 = new Game(date);
			repositoryGame.save(g2);

			Player p3 = new Player("Spike", "Spieguel", "SpikeS@gmail");
			repositoryPlayer.save(p3);

			GamePlayer gp3 = new GamePlayer(p2,g2);
			GamePlayer gp4 = new GamePlayer(p3,g2);
			repositoryGamePlayer.save(gp3);
			repositoryGamePlayer.save(gp4);

			Score Score3 = new Score(p2, g2, (float)0.5, date);
			scoreRepository.save(Score3);

			Score Score4 = new Score(p3, g2, (float)0.5, date);
			scoreRepository.save(Score4);



			/*GamePlayer gp2 = new GamePlayer();
			repositoryGamePlayer.save(gp2);
			p1.addGamePlayer(gp2);*/
			//no funciona agregar

		};
	}
}
