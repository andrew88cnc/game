package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService{

    private PlayerRepository playerRepository;

    @Autowired
    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public List<Player> getPlayersList(Specification<Player> specification) {
        return playerRepository.findAll(specification);
    }

    @Override
    public Page<Player> getPlayersList(Specification<Player> specification, Pageable pageable) {
        return playerRepository.findAll(specification, pageable);
    }

    @Override
    public Player createPlayer(Player player) throws InvalidPlayerCustomException {
        if (!checkPlayerParametersCreate(player)) throw new InvalidPlayerCustomException();
        if (player.getBanned() == null) player.setBanned(false);
        setLevelAndExperienceUntilNextLevel(player);
        return playerRepository.saveAndFlush(player);
    }

    @Override
    public Player updatePlayer(Long id, Player player) throws PlayerNotFoundCustomException {
        Player existPlayer;
        try {
            existPlayer = getPlayer(id);
        } catch (PlayerNotFoundCustomException e) {
            throw e;
        }
        if (player.getName() != null && checkName(player.getName())) existPlayer.setName(player.getName());
        if (player.getTitle() != null && checkTitle(player.getTitle())) existPlayer.setTitle(player.getTitle());
        if (player.getRace() != null) existPlayer.setRace(player.getRace());
        if (player.getProfession() != null) existPlayer.setProfession(player.getProfession());
        if (player.getExperience() != null && checkExperience(player.getExperience())) existPlayer.setExperience(player.getExperience());
        if (player.getBirthday() != null && checkBirthday(player.getBirthday())) existPlayer.setBirthday(player.getBirthday());
        if (player.getBanned() != null) existPlayer.setBanned(player.getBanned());
        setLevelAndExperienceUntilNextLevel(existPlayer);
        return playerRepository.save(existPlayer);
    }

    @Override
    public Player getPlayer(Long id) throws PlayerNotFoundCustomException {
        if (playerRepository.findById(id).isPresent()) {
            return playerRepository.findById(id).get();
        } else throw new PlayerNotFoundCustomException();
    }

    @Override
    public boolean deletePlayer(Long id) {
        if (playerRepository.findById(id).isPresent()) {
            playerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void setLevelAndExperienceUntilNextLevel(Player player) {
        player.setLevel(calculateLevel(player));
        player.setUntilNextLevel(calculateExperienceUntilNextLevel(player));
    }

    private int calculateLevel(Player player) {
        int exp = player.getExperience();
        return (int) ((Math.sqrt(2500 + 200 * exp) - 50) / 100);
    }

    private int calculateExperienceUntilNextLevel(Player player) {
        int exp = player.getExperience();
        int lvl = calculateLevel(player);
        return 50 * (lvl + 1) * (lvl + 2) - exp;
    }

    @Override
    public boolean checkName(String name) {
        return name != null && name.length() >= 1 && name.length() <= 12;
    }

    @Override
    public boolean checkTitle(String title) {
        return title != null && title.length() >= 1 && title.length() <= 30;
    }

    @Override
    public boolean checkExperience(Integer experience) {
        return experience != null && experience >= 0 && experience <= 10_000_000;
    }

    @Override
    public boolean checkBirthday(Date birthday) {
        if (birthday == null) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birthday);
        return calendar.get(Calendar.YEAR) >= 2_000 && calendar.get(Calendar.YEAR) <= 3_000;
    }

    public boolean checkPlayerParametersCreate(Player player) {
        return checkName(player.getName())
                && checkTitle(player.getTitle())
                && checkExperience(player.getExperience())
                && checkBirthday(player.getBirthday());
    }
}
