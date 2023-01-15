package com.game.service;

import com.game.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;

public interface PlayerService {

    List<Player> getPlayerList(Specification<Player> specification);

    Page<Player> getPlayerList(Specification<Player> specification, Pageable pageable);

    Player createPlayer(Player player) throws InvalidParamsCustomException;

    Player getPlayerById(Long id) throws PlayerNotFoundCustomException;

    Player updatePlayerById(Long id, Player player) throws PlayerNotFoundCustomException;

    boolean deletePlayerById(Long id);

    boolean checkPlayerName(String name);

    boolean checkTitle(String title);

    boolean checkExperience(Integer experience);

    boolean checkDoB(Date birthday);
}
