package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlayersRepo extends JpaRepository<Player, Long>, JpaSpecificationExecutor<Player> {}
