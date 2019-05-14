package com.codeofthewe.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    List<GamePlayer> findByPartidaPlayer(Player partidaPlayer);
}