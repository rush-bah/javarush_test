package com.game.entity;


import javax.persistence.*;
import java.util.Date;


public final class PlayerRequestData {
    public String name;// Имя персонажа (до 12 знаков включительно)
    public String title;// Титул персонажа (до 30 знаков включительно)
    public Race race;// Расса персонажа
    public Profession profession;// Профессия персонажа
    public Integer experience;// Опыт персонажа. Диапазон значений 0..10,000,000
    public Long birthday;// Дата регистрации
    public Boolean banned;
}
