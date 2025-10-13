package com.sy.model.game;

import lombok.Data;

import java.util.List;

@Data
public class CardDto {
    private List<Characters> characters;
    private List<Card> heros;
    private Card hero;
}
