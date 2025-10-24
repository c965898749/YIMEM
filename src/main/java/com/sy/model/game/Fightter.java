package com.sy.model.game;

import lombok.Data;

@Data
public class Fightter {

    private Integer isSkill=0;//0否1是技能
    private String str;//谁做了什么事情
    private String direction;//那边的人
    private String goON;//是场下还是场上
    private Integer goIntoNum;//位置
    private Integer hp;//血量
    private Integer maxHp;
    private Integer attack;//攻击力
    private Integer speed;//速度
    private String buff;//需要播放什么buff动画
    private Integer isbuff;//buff动画需要关闭吗0关/1开
    private Integer isAction = 1;//我能不能动//0不能1能
    //目标
    private String isSkillFace;//0否1是技能
    private String strFace;//对面做了什么事情
    private String directionFace;//那边的人
    private String goONFace;//是场下还是场上
    private Integer goIntoNumFace;//位置
    private Integer hpFace;//血量
    private Integer maxHpFace;
    private Integer attackFace;//攻击力
    private Integer speedFace;//速度
    private String buffFace;//需要播放什么buff动画
    private Integer isbuffFace;//buff动画需要关闭吗0关/1开
    private Integer isActionFace = 1;//我能不能动//0不能1能
    private String isDead="0";//0否1是
    //现在场上有什么
}
