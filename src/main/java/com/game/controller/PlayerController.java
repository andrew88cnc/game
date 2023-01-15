package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import com.game.specification.PlayerSpecification;
import com.game.specification.SearchCriteria;
import com.game.specification.SearchOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayerController {
    private PlayerService playerService;

    @Autowired
    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getPlayersList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        return playerService.getPlayersList(
                setSpecification(name, title, race, profession, after, before, banned,
                        minExperience, maxExperience, minLevel, maxLevel), pageable).getContent();
    }


    @GetMapping("/players/count")
    @ResponseStatus(HttpStatus.OK)
    public Integer getPlayersCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {

        return playerService.getPlayersList(
                setSpecification(name, title, race, profession, after, before, banned,
                        minExperience, maxExperience, minLevel, maxLevel)).size();
    }


    @PostMapping("/players")
    public ResponseEntity<?> createPlayer(@RequestBody Player player) {
        Player newPlayer = playerService.createPlayer(player);
        if (newPlayer == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(newPlayer, HttpStatus.OK);
        }
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable(name = "id") Long id) {
        if (id <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player player = playerService.getPlayer(id);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else return new ResponseEntity<>(player, HttpStatus.OK);
    }


    @DeleteMapping("/players/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable("id") Long id) {
        if (id <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (playerService.deletePlayer(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") Long id, @RequestBody Player player) {
        if (id <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (player.getName() != null && !playerService.checkName(player.getName()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (player.getTitle() != null && !playerService.checkTitle(player.getTitle()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (player.getExperience() != null && !playerService.checkExperience(player.getExperience()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (player.getBirthday() != null && !playerService.checkBirthday(player.getBirthday()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Player newPlayer = playerService.updatePlayer(id, player);
        if (newPlayer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else return new ResponseEntity<>(newPlayer, HttpStatus.OK);
    }

    private Specification<Player> setSpecification(String name,
                                                   String title,
                                                   Race race,
                                                   Profession profession,
                                                   Long after,
                                                   Long before,
                                                   Boolean banned,
                                                   Integer minExperience,
                                                   Integer maxExperience,
                                                   Integer minLevel,
                                                   Integer maxLevel
    ) {

        PlayerSpecification generalSpecification = new PlayerSpecification();
        if (name != null)
            generalSpecification.add(new SearchCriteria("name", name, SearchOperation.MATCH));
        if (title != null)
            generalSpecification.add(new SearchCriteria("title", title, SearchOperation.MATCH));
        if (race != null)
            generalSpecification.add(new SearchCriteria("race", race, SearchOperation.EQUAL));
        if (profession != null)
            generalSpecification.add(new SearchCriteria("profession", profession, SearchOperation.EQUAL));
        if (after != null)
            generalSpecification.add(new SearchCriteria("birthday", new Date(after), SearchOperation.GREATER_THAN_EQUAL));
        if (before != null)
            generalSpecification.add(new SearchCriteria("birthday", new Date(before), SearchOperation.LESS_THAN_EQUAL));
        if (banned != null)
            generalSpecification.add(new SearchCriteria("banned", banned, SearchOperation.EQUAL));
        if (minExperience != null)
            generalSpecification.add(new SearchCriteria("experience", minExperience, SearchOperation.GREATER_THAN_EQUAL));
        if (maxExperience != null)
            generalSpecification.add(new SearchCriteria("experience", maxExperience, SearchOperation.LESS_THAN_EQUAL));
        if (minLevel != null)
            generalSpecification.add(new SearchCriteria("level", minLevel, SearchOperation.GREATER_THAN_EQUAL));
        if (maxLevel != null)
            generalSpecification.add(new SearchCriteria("level", maxLevel, SearchOperation.LESS_THAN_EQUAL));

        return generalSpecification;

    }


}
