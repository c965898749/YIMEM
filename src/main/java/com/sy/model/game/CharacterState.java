package com.sy.model.game;

import com.sy.tool.CharacterMetaState;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class CharacterState {

        // id
       private String id;
        // uuid
       private Integer uuid;
        // 等级
       private Integer lv;
        // 星级
       private BigDecimal star;
        // 是否上场
       private Integer onStage;
        // 拥有装备
//        equipment: EquipmentStateCreate[]


        /**
         * 所属组件
         * 一般来说是 HolCharacter 对象
         */


        // 名称
       private String  name;


        // 生命值
       private Integer hp;
        // 最大生命值
       private Integer maxHp;
        // 能量值
       private Integer energy;
        // 最大能量值
       private Integer maxEnergy;
        // 攻击力
       private Integer attack;
        // 防御力
       private Integer defence;
        // 速度
       private Integer speed;
        // 穿透
       private Integer pierce;
        // 治疗效率
       private BigDecimal curePercent;
        // 伤害率
       private BigDecimal hurtPercent;
        // 免伤率
       private BigDecimal FreeInjuryPercent;
        // 暴击
       private Integer critical;
        // 格挡
       private Integer block;

        // 所有buff
//        buff: BuffState[] = []
        // 所有装备
//        equipment: EquipmentState[] = []

        /**
         * 构造器
         * component 是所属组件
         */
        public CharacterState(Characters create) {
            this.lv = create.getLv();
            this.star = create.getStar();
            this.name =create.getName();
            this.onStage=create.getOnStage();

            this.maxEnergy = CharacterMetaState.Energy;
            this.maxHp =Integer.parseInt(create.getLv() * CharacterMetaState.HpGrowth * ((create.getStar().subtract(new BigDecimal(1))).doubleValue() * 0.15 + 1) * (create.getLv() / 80 + 0.8)+"");
            this.attack =Integer.parseInt(create.getLv() * CharacterMetaState.AttackGrowth * ((create.getStar().subtract(new BigDecimal(1))).doubleValue() * 0.15 + 1) * (create.getLv() / 80 + 0.8)+"");
            this.defence =Integer.parseInt(create.getLv() * CharacterMetaState.DefenceGrowth * ((create.getStar().subtract(new BigDecimal(1))).doubleValue() * 0.15 + 1) * (create.getLv() / 80 + 0.8)+"");
            this.speed =Integer.parseInt(create.getLv() * CharacterMetaState.SpeedGrowth * ((create.getStar().subtract(new BigDecimal(1))).doubleValue() * 0.15 + 1) * (create.getLv() / 80 + 0.8)+"");
            this.pierce =Integer.parseInt(create.getLv() * CharacterMetaState.PierceGrowth * ((create.getStar().subtract(new BigDecimal(1))).doubleValue() * 0.15 + 1) * (create.getLv() / 80 + 0.8)+"");

            this.critical = CharacterMetaState.Critical;
            this.block = CharacterMetaState.Block;

//            create.equipment.forEach(ec => this.addEquipment(ec))
//            meta.OnCreateState(this)

            this.hp = this.maxHp;
            this.energy = 20;
        }
//        constructor(create: CharacterStateCreate , component: HolCharacter) {
//        const meta: CharacterMetaState = CharacterEnum[create.id]
//            super(meta)
//            this.lv = create.lv
//            this.star = create.star
//            this.name = meta.name
//            this.component = component
//            this.create = create
//            this.onStage=create.onStage
//
//            this.maxEnergy = meta.Energy
//            this.maxHp = create.lv * meta.HpGrowth * ((create.star - 1) * 0.15 + 1) * (create.lv / 80 + 0.8)
//            this.attack = create.lv * meta.AttackGrowth * ((create.star - 1) * 0.15 + 1) * (create.lv / 80 + 0.8)
//            this.defence = create.lv * meta.DefenceGrowth * ((create.star - 1) * 0.15 + 1) * (create.lv / 80 + 0.8)
//            this.speed = create.lv * meta.SpeedGrowth * ((create.star - 1) * 0.15 + 1) * (create.lv / 80 + 0.8)
//            this.pierce = create.lv * meta.PierceGrowth * ((create.star - 1) * 0.15 + 1) * (create.lv / 80 + 0.8)
//            this.critical = meta.Critical
//            this.block = meta.Block
//
//            create.equipment.forEach(ec => this.addEquipment(ec))
//            meta.OnCreateState(this)
//
//            this.hp = this.maxHp
//            this.energy = 20
//        }

        // 合理化数据
//        reasonableData() {
//            if (this.hp > this.maxHp) this.hp = this.maxHp
//            if (this.energy > this.maxEnergy) this.energy = this.maxEnergy
//            if (this.hp < 0) this.hp = 0
//            if (this.energy < 0) this.energy = 0
//        }

        /**
         * 添加装备函数
         * 在构造时调用会将装备所添加的属性加到该对象上
         */
//        private addEquipment(equipment: EquipmentStateCreate) {
//        const equipmentState = new EquipmentState(equipment , this)
//            this.equipment.push(equipmentState)
//            equipmentState.AddPropertyToCharacter(equipmentState)
//        }
}
