package com.game.service;

import com.game.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

public interface PlayerService {

    List<Player> getPlayersList(Specification<Player> specification);

    Page<Player> getPlayersList(Specification<Player> specification, Pageable pageable);

    Player createPlayer(Player player);

    Player getPlayer(Long id);

    Player updatePlayer(Long id, Player player);

    boolean deletePlayer(Long id);

    boolean checkName(String name);

    boolean checkTitle(String title);

    boolean checkExperience(Integer experience);

    boolean checkBirthday(Date birthday);

}
