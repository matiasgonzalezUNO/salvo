package com.codeofthewe.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository repositoryPlayer, GameRepository repositoryGame, GamePlayerRepository repositoryGamePlayer, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository, PasswordEncoder passwordEncoder ) {
		return (args) -> {

			// save a couple of customers

			/*
			Player p1 = new Player("Lucas", "carri", "Lcarri@gmail");
			repositoryPlayer.save(p1);
			Player p2 = new Player("jet", "black", "JBlack@gmail");
			repositoryPlayer.save(p2);

			Date date = new Date();
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

			 */


			// *-*-*-*-*-*-*-*-*-*-*Program INTRODUCCIÓN JAVA 1 -*-*-*-*-*-*-*-*-*-*-*-*
			Player p1 = new Player("Jack", "Bauer", "j.bauer@ctu.gov",passwordEncoder.encode("24"));
			repositoryPlayer.save(p1);

			Player p2 = new Player("Chloe", " O'Brian", "c.obrian@ctu.gov",passwordEncoder.encode("42"));
			repositoryPlayer.save(p2);

			Player p3 = new Player("Kim", "Bauer", "kim_bauer@gmail.com",passwordEncoder.encode("kb"));
			repositoryPlayer.save(p3);

			Player p4 = new Player("Tony", "Almeida", "t.almeida@ctu.gov",passwordEncoder.encode("mole"));
			repositoryPlayer.save(p4);

			Date newDate1 = new Date();
			Date newDate2 = Date.from(newDate1.toInstant().plusSeconds(3600));
			Date newDate3 = Date.from(newDate2.toInstant().plusSeconds(3600));
			Date newDate4 = Date.from(newDate3.toInstant().plusSeconds(3600));
			Date newDate5 = Date.from(newDate4.toInstant().plusSeconds(3600));
			Date newDate6 = Date.from(newDate5.toInstant().plusSeconds(3600));
			Date newDate7 = Date.from(newDate6.toInstant().plusSeconds(3600));
			Date newDate8 = Date.from(newDate7.toInstant().plusSeconds(3600));

			Game g1 = new Game(newDate1);
			repositoryGame.save(g1);
			Game g2 = new Game(newDate2);
			repositoryGame.save(g2);
			Game g3 = new Game(newDate3);
			repositoryGame.save(g3);
			Game g4 = new Game(newDate4);
			repositoryGame.save(g4);
			Game g5 = new Game(newDate5);
			repositoryGame.save(g5);
			Game g6 = new Game(newDate6);
			repositoryGame.save(g6);
			Game g7 = new Game(newDate7);
			repositoryGame.save(g7);
			Game g8 = new Game(newDate8);
			repositoryGame.save(g8);

			//p1= j.bauer@ctu.gov
			//p2= c.obrian@ctu.gov
			//p3= kim_bauer@gmail.com
			//p4= t.almeida@ctu.gov
			GamePlayer gp1 = new GamePlayer(p1,g1);
			GamePlayer gp2 = new GamePlayer(p2,g1);

			GamePlayer gp3 = new GamePlayer(p1,g2); //j.bauer@ctu.gov
			GamePlayer gp4 = new GamePlayer(p2,g2); //c.obrian@ctu.gov

			GamePlayer gp5 = new GamePlayer(p2,g3);
			GamePlayer gp6 = new GamePlayer(p4,g3);

			GamePlayer gp7 = new GamePlayer(p2,g4);
			GamePlayer gp8 = new GamePlayer(p1,g4);

			GamePlayer gp9 = new GamePlayer(p4,g5);
			GamePlayer gp10 = new GamePlayer(p1,g5);

			GamePlayer gp11= new GamePlayer(p1,g6);
			GamePlayer gp12 = new GamePlayer(p2,g6);

			GamePlayer gp13 = new GamePlayer(p1,g7);
			GamePlayer gp14 = new GamePlayer(p2,g7);

			GamePlayer gp15 = new GamePlayer(p1,g8);
			GamePlayer gp16 = new GamePlayer(p2,g8);
			repositoryGamePlayer.save(gp1);
			repositoryGamePlayer.save(gp2);
			repositoryGamePlayer.save(gp3);
			repositoryGamePlayer.save(gp4);
			repositoryGamePlayer.save(gp5);
			repositoryGamePlayer.save(gp6);
			repositoryGamePlayer.save(gp7);
			repositoryGamePlayer.save(gp8);
			repositoryGamePlayer.save(gp9);
			repositoryGamePlayer.save(gp10);
			repositoryGamePlayer.save(gp11);
			repositoryGamePlayer.save(gp12);
			repositoryGamePlayer.save(gp13);
			repositoryGamePlayer.save(gp14);
			repositoryGamePlayer.save(gp15);
			repositoryGamePlayer.save(gp16);

			List<String> ShipL_1 = new ArrayList<String>();
			ShipL_1.add("H2");
			ShipL_1.add("H3");
			ShipL_1.add("H4");
			Ship s1 = new Ship(gp1, "Destroyer", ShipL_1);
			shipRepository.save(s1);
			List<String> ShipL_2 = new ArrayList<String>();
			ShipL_2.add("E1");
			ShipL_2.add("F1");
			ShipL_2.add("G1");
			Ship s2 = new Ship(gp1, "Submarine", ShipL_2);
			shipRepository.save(s2);
			List<String> ShipL_3 = new ArrayList<String>();
			ShipL_3.add("B4");
			ShipL_3.add("B5");
			Ship s3 = new Ship(gp1, "Patrol Boat", ShipL_3);
			shipRepository.save(s3);
			List<String> ShipL_4 = new ArrayList<String>();
			ShipL_4.add("B5");
			ShipL_4.add("C5");
			ShipL_4.add("D5");
			Ship s4 = new Ship(gp2, "Destroyer", ShipL_4);
			shipRepository.save(s4);
			List<String> ShipL_5 = new ArrayList<String>();
			ShipL_5.add("F1");
			ShipL_5.add("F2");
			Ship s5 = new Ship(gp2, "Patrol Boat", ShipL_5);
			shipRepository.save(s5);

			List<String> ShipL_6 = new ArrayList<String>();
			ShipL_6.add("B5");
			ShipL_6.add("C5");
			ShipL_6.add("D5");
			Ship s6 = new Ship(gp3, "Destroyer", ShipL_6);
			shipRepository.save(s6);
			List<String> ShipL_7 = new ArrayList<String>();
			ShipL_7.add("C6");
			ShipL_7.add("C7");
			Ship s7 = new Ship(gp3, "Patrol Boat", ShipL_7);
			shipRepository.save(s7);
			List<String> ShipL_8 = new ArrayList<String>();
			ShipL_8.add("A2");
			ShipL_8.add("A3");
			ShipL_8.add("A4");
			Ship s8 = new Ship(gp4, "Submarine", ShipL_8);
			shipRepository.save(s8);
			List<String> ShipL_9 = new ArrayList<String>();
			ShipL_9.add("G6");
			ShipL_9.add("H6");
			Ship s9 = new Ship(gp4, "Patrol Boat", ShipL_9);
			shipRepository.save(s9);

			//p1= j.bauer@ctu.gov
			//p2= c.obrian@ctu.gov
			//p3= kim_bauer@gmail.com
			//p4= t.almeida@ctu.gov
			Score Score1 = new Score(p1, g1, (float)1.0, newDate1);
			scoreRepository.save(Score1);
			Score Score2 = new Score(p1, g2, (float)0.5, newDate1);
			scoreRepository.save(Score2);
			Score Score3 = new Score(p2, g2, (float)0.5, newDate1);
			scoreRepository.save(Score3);
			Score Score4 = new Score(p2, g3, (float)1.0, newDate1);
			scoreRepository.save(Score4);

			List<String> listSalvo_1 = new ArrayList<String>();
			listSalvo_1.add("B5");
			listSalvo_1.add("C5");
			listSalvo_1.add("F1");
			Salvo salvo1 = new Salvo(1, gp1, listSalvo_1);
			salvoRepository.save(salvo1);
			List<String> listSalvo_2 = new ArrayList<String>();
			listSalvo_2.add("B4");
			listSalvo_2.add("B5");
			listSalvo_2.add("B6");
			Salvo salvo2 = new Salvo(1, gp2, listSalvo_2);
			salvoRepository.save(salvo2);
			List<String> listSalvo_3 = new ArrayList<String>();
			listSalvo_3.add("F2");
			listSalvo_3.add("D5");
			Salvo salvo3 = new Salvo(2, gp1, listSalvo_3);
			salvoRepository.save(salvo3);
			List<String> listSalvo_4 = new ArrayList<String>();
			listSalvo_4.add("E1");
			listSalvo_4.add("H3");
			listSalvo_4.add("A2");
			Salvo salvo4 = new Salvo(2, gp2, listSalvo_4);
			salvoRepository.save(salvo4);

		};
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByEmail(inputName);
			if (player != null) {
				return new User(player.getEmail(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().fullyAuthenticated().
                and().httpBasic();
    }*/

    /*@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/**").hasAuthority("USER")
				.and()
				.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/app/login");

		http.logout().logoutUrl("/app/logout");
	}*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/rest")
				.permitAll()
				.anyRequest()
				.fullyAuthenticated()
				.and()
				.formLogin()
				.usernameParameter("username")
				.passwordParameter("password")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout")
				.permitAll();

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}

}
