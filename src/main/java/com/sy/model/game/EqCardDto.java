package com.sy.model.game;

import lombok.Data;

import java.util.List;

@Data
public class EqCardDto {
    private List<EqCharacters> characters;
    private List<EqCard> heros;
    private EqCard hero;
}
