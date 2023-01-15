package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PlayerServiceImplement implements PlayerService{

    private PlayersRepo playersRepo;

    @Autowired
    public void setPlayerRepository(PlayersRepo playersRepo) {
        this.playersRepo = playersRepo;
    }

    @Override
    public List<Player> getPlayerList(Specification<Player> specification) {
        return playersRepo.findAll(specification);
    }

    @Override
    public Page<Player> getPlayerList(Specification<Player> specification, Pageable pageable) {
        return playersRepo.findAll(specification, pageable);
    }

    @Override
    public Player createPlayer(Player player) throws InvalidParamsCustomException {
        if (!checkPlayerParametersCreate(player)) throw new InvalidParamsCustomException();
        if (player.getBanned() == null) player.setBanned(false);
        setLevelAndExperienceUntilNextLevel(player);
        return playersRepo.saveAndFlush(player);
    }

    @Override
    public Player updatePlayerById(Long id, Player player) throws PlayerNotFoundCustomException {
        Player existPlayer;
        try {
            existPlayer = getPlayerById(id);
        } catch (PlayerNotFoundCustomException e) {
            throw e;
        }
        if (player.getName() != null && checkPlayerName(player.getName())) existPlayer.setName(player.getName());
        if (player.getTitle() != null && checkTitle(player.getTitle())) existPlayer.setTitle(player.getTitle());
        if (player.getRace() != null) existPlayer.setRace(player.getRace());
        if (player.getProfession() != null) existPlayer.setProfession(player.getProfession());
        if (player.getExperience() != null && checkExperience(player.getExperience())) existPlayer.setExperience(player.getExperience());
        if (player.getBirthday() != null && checkDoB(player.getBirthday())) existPlayer.setBirthday(player.getBirthday());
        if (player.getBanned() != null) existPlayer.setBanned(player.getBanned());
        setLevelAndExperienceUntilNextLevel(existPlayer);
        return playersRepo.save(existPlayer);
    }

    @Override
    public Player getPlayerById(Long id) throws PlayerNotFoundCustomException {
        if (playersRepo.findById(id).isPresent()) {
            return playersRepo.findById(id).get();
        } else throw new PlayerNotFoundCustomException();
    }

    @Override
    public boolean deletePlayerById(Long id) {
        if (playersRepo.findById(id).isPresent()) {
            playersRepo.deleteById(id);
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
    public boolean checkPlayerName(String name) {
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
    public boolean checkDoB(Date birthday) {
        if (birthday == null) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birthday);
        return calendar.get(Calendar.YEAR) >= 2_000 && calendar.get(Calendar.YEAR) <= 3_000;
    }

    public boolean checkPlayerParametersCreate(Player player) {
        return checkPlayerName(player.getName())
                && checkTitle(player.getTitle())
                && checkExperience(player.getExperience())
                && checkDoB(player.getBirthday());
    }
}
