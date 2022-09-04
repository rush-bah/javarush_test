package com.game.entity;


import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Table(name = "Player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;// ID игрока
    private String name;// Имя персонажа (до 12 знаков включительно)
    private String title;// Титул персонажа (до 30 знаков включительно)
    @Enumerated(EnumType.STRING)
    private Race race;// Расса персонажа
    @Enumerated(EnumType.STRING)
    private Profession profession;// Профессия персонажа
    private Integer experience;// Опыт персонажа. Диапазон значений 0..10,000,000
    private Integer level;// Уровень персонажа
    private Integer untilNextLevel;// Остаток опыта до следующего уровня
    private Date birthday;// Дата регистрации
    private Boolean banned;

    public void calculateLvl(){
        level = (int)(((long)Math.sqrt(2500l + 200*experience) - 50)/100);
        untilNextLevel = 50*(level+1)*(level+2)-experience;
    }

    public boolean updateByPlayerData(PlayerRequestData playerData, boolean onlyNotEmpty){
        //checking
        if(!onlyNotEmpty)
            if (playerData.name == null || playerData.title == null || playerData.race == null || playerData.profession == null || playerData.birthday == null || playerData.experience == null)
                return false;
        if (playerData.name != null)
            if(playerData.name.isEmpty() || playerData.name.length() > 12)
                return false;
        if(playerData.title != null)
            if(playerData.title.length() > 30)
                return false;
        if(playerData.experience != null)
            if(playerData.experience < 0 || playerData.experience > 10_000_000l)
                return false;
        if(playerData.birthday != null) {
            if (playerData.birthday < 0)
                return false;
            Date date = new Date(playerData.birthday);
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int year = localDate.getYear();
            if (year < 2000 || year > 3000)
                return false;
        }
        //setting
        if (playerData.name != null)
            name = playerData.name;
        if (playerData.title != null)
            title = playerData.title;
        if (playerData.race != null)
            race = playerData.race;
        if (playerData.profession != null)
            profession = playerData.profession;
        if (playerData.birthday != null)
            birthday = new Date(playerData.birthday);
        if (playerData.experience != null){
            experience = playerData.experience;
            calculateLvl();
        }
        if(playerData.banned != null || !onlyNotEmpty)
            banned = playerData.banned != null ? playerData.banned : false;
        return true;
    }
}
