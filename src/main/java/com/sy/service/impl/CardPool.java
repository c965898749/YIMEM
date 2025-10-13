package com.sy.service.impl;

import com.sy.model.game.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardPool {
    private List<Card> cards;
    private Random random;
    private int totalWeight;

    public CardPool() {
        this.cards = new ArrayList<>();
        this.random = new Random();
        this.totalWeight = 0;
    }

    public void addCard(Card card) {
        cards.add(card);
        totalWeight += card.getWeight(); // 累加总权重
    }

    public Card draw() {
        int index = random.nextInt(totalWeight); // 生成一个在0到总权重之间的随机数
        for (Card card : cards) {
            if (index < card.getWeight()) { // 如果随机数小于当前卡片的权重，则返回这张卡片
                return card;
            }
            index -= card.getWeight(); // 减去当前卡片的权重，继续检查下一张卡片
        }
        return null; // 理论上不会执行到这里，除非数据有问题
    }
}
