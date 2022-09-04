package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.*;
import com.game.repository.PlayerCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.*;

@RestController
@RequestMapping("rest")
public class PlayerRestController {
    @Autowired
    PlayerCrudRepository playerRepo;


    @GetMapping(
        path = "players"
    )
    public ResponseEntity<List<Player>> getPlayersList(
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
            @RequestParam(value = "order", required = false) PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {;

        PageRequest pr = PageRequest.of(pageNumber == null ? 0 : pageNumber,
                pageSize == null ? 3 : pageSize,
                Sort.by(Sort.Direction.ASC, order == null ? "id" : order.getFieldName()));

        Specification<Player> sp = getPlayerSpecification(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);

        Iterable<Player> data = playerRepo.findAll(sp, pr);
        List<Player> players = new ArrayList<>();
        for (Player player : data) {
            players.add(player);
        }
        return ResponseEntity.ok(players);
    }

    private static Specification<Player> getPlayerSpecification(String name, String title, Race race, Profession profession, Long after, Long before, Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel) {
        Specification<Player> sp = new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> preds = new ArrayList<>();
                if(name != null && !name.isEmpty())
                    preds.add(criteriaBuilder.like(root.get("name").as(String.class), "%"+ name +"%"));
                if(title != null && !title.isEmpty())
                    preds.add(criteriaBuilder.like(root.get("title").as(String.class), "%"+ title +"%"));
                if(race != null)
                    preds.add(criteriaBuilder.equal(root.get("race").as(Race.class), race));
                if(profession != null)
                    preds.add(criteriaBuilder.equal(root.get("profession").as(Profession.class), profession));
                if(after != null && after != 0l)
                    preds.add(criteriaBuilder.greaterThanOrEqualTo(root.get("birthday").as(Date.class), new Date(after)));
                if(before != null && before != 0l)
                    preds.add(criteriaBuilder.lessThanOrEqualTo(root.get("birthday").as(Date.class), new Date(before)));
                if(banned != null)
                    preds.add(criteriaBuilder.equal(root.get("banned").as(Boolean.class), banned));
                if(minExperience != null && minExperience != 0l)
                    preds.add(criteriaBuilder.ge(root.get("experience").as(Integer.class), minExperience));
                if(maxExperience != null && maxExperience != 0l)
                    preds.add(criteriaBuilder.le(root.get("experience").as(Integer.class), maxExperience));
                if(minLevel != null && minLevel != 0l)
                    preds.add(criteriaBuilder.ge(root.get("level").as(Integer.class), minLevel));
                if(maxLevel != null && maxLevel != 0l)
                    preds.add(criteriaBuilder.le(root.get("level").as(Integer.class), maxLevel));
                if(preds.isEmpty())
                    return null;
                else
                    return criteriaBuilder.and(preds.stream().toArray(Predicate[]::new));
            }
        };
        return sp;
    }

    @GetMapping(
            path = "players/count"
    )
    public ResponseEntity<Integer> getPlayersCount(
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
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel
    ) {
        Specification<Player> sp = getPlayerSpecification(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);

        long count = playerRepo.count(sp);

        return ResponseEntity.ok((int)count);
    }

    @PostMapping(
            path = "players",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Player> CreatePlayer(@RequestBody PlayerRequestData playerData) {

        Player player = new Player();

        if(!player.updateByPlayerData(playerData, false))
            return ResponseEntity.badRequest().build();

        player = playerRepo.save(player);

        return ResponseEntity.ok(player);
    }

    @GetMapping(
            path = "players/{id}"
    )
    public ResponseEntity<Player> GetPlayer(@PathVariable Long id) {
        if(id == null || id <= 0)
            return ResponseEntity.badRequest().build();
        Optional<Player> player = playerRepo.findById(id);
        if(!player.isPresent())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(player.get());
    }

    @PostMapping(
            path = "players/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Player> UpdatePlayer(@PathVariable Long id,
                                               @RequestBody PlayerRequestData playerData) {

        if(id == null || id <= 0)
            return ResponseEntity.badRequest().build();
        Optional<Player> playerFind = playerRepo.findById(id);
        if(!playerFind.isPresent())
            return ResponseEntity.notFound().build();

        Player player = playerFind.get();

        if(!player.updateByPlayerData(playerData, true))
            return ResponseEntity.badRequest().build();

        player = playerRepo.save(player);

        return ResponseEntity.ok(player);
    }

    @DeleteMapping(
            path = "players/{id}"
    )
    public ResponseEntity<Void> DeletePlayer(@PathVariable Long id) {
        if(id == null || id <= 0)
            return ResponseEntity.badRequest().build();
        Optional<Player> playerFind = playerRepo.findById(id);
        if(!playerFind.isPresent())
            return ResponseEntity.notFound().build();

        playerRepo.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
