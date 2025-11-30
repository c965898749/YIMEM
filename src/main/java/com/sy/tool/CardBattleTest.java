package com.sy.tool;

import com.sy.model.game.BattleLog;
import com.sy.model.game.Guardian;
import com.sy.model.game.Profession;
import com.sy.model.game.Race;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡牌战斗系统测试类
 */
public class CardBattleTest {
    public static void main(String[] args) {
        // 创建战斗工具实例
        CardBattleTool battleTool = new CardBattleTool();

        // 创建测试队伍A
        List<Guardian> teamA = createTestTeamA();

        // 创建测试队伍B
        List<Guardian> teamB = createTestTeamB();

        // 开始战斗
        System.out.println("=== 开始卡牌5v5战斗 ===");
        System.out.println("A队成员：" + getTeamNames(teamA));
        System.out.println("B队成员：" + getTeamNames(teamB));
        System.out.println("=======================\n");

        String battleId = battleTool.startBattle(teamA, teamB);

        // 获取并打印战斗日志
        List<BattleLog> battleLogs = battleTool.getBattleLogs(battleId);
        printBattleLogs(battleLogs);

        // 输出战斗结果
        System.out.println("\n=== 战斗结果 ===");
        int teamAHp = calculateTeamTotalHp(teamA);
        int teamBHp = calculateTeamTotalHp(teamB);

        System.out.println("A队剩余总血量：" + teamAHp);
        System.out.println("B队剩余总血量：" + teamBHp);

        if (teamAHp > teamBHp) {
            System.out.println("A队胜利！");
        } else if (teamBHp > teamAHp) {
            System.out.println("B队胜利！");
        } else {
            System.out.println("平局！");
        }

        // 清理缓存
        battleTool.clearBattleLogs(battleId);
    }

    /**
     * 创建测试队伍A
     */
    private static List<Guardian> createTestTeamA() {
        List<Guardian> team = new ArrayList<>();

        // 牛魔王（妖族，战士，位置1）
        team.add(new Guardian("牛魔王", Profession.WARRIOR, Race.DEMON_RACE, 5, 1));

        // 洛神（仙族，法师，位置2）
        team.add(new Guardian("洛神", Profession.IMMORTAL, Race.IMMORTAL_RACE, 5, 2));

        // 阎罗王（鬼族，刺客，位置3）
        team.add(new Guardian("阎罗王", Profession.IMMORTAL, Race.GHOST_RACE, 5, 3));

        // 圣灵天将（仙族，战士，位置4）
        team.add(new Guardian("圣灵天将", Profession.WARRIOR, Race.IMMORTAL_RACE, 5, 4));

        // 瑶池仙女（仙族，牧师，位置5）
        team.add(new Guardian("瑶池仙女", Profession.WARRIOR, Race.IMMORTAL_RACE, 5, 5));

        return team;
    }

    /**
     * 创建测试队伍B
     */
    private static List<Guardian> createTestTeamB() {
        List<Guardian> team = new ArrayList<>();

        // 齐天大圣（妖族，战士，位置1）
        team.add(new Guardian("厚土娘娘", Profession.WARRIOR, Race.IMMORTAL_RACE, 5, 1));

        // 厚土娘娘（仙族，坦克，位置2）
        team.add(new Guardian("齐天大圣", Profession.WARRIOR, Race.IMMORTAL_RACE, 5, 2));

        // 妲己（妖族，法师，位置3）
        team.add(new Guardian("妲己", Profession.IMMORTAL, Race.DEMON_RACE, 5, 3));

        // 白骨精（鬼族，刺客，位置4）
        team.add(new Guardian("镇元子", Profession.IMMORTAL, Race.IMMORTAL_RACE, 5, 4));

        // 孟婆（鬼族，牧师，位置5）
        team.add(new Guardian("孟婆", Profession.IMMORTAL, Race.GHOST_RACE, 5, 5));

        return team;
    }

    /**
     * 获取队伍成员名称
     */
    private static String getTeamNames(List<Guardian> team) {
        StringBuilder sb = new StringBuilder();
        for (Guardian g : team) {
            sb.append(g.getName()).append("、");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 打印战斗日志
     */
    private static void printBattleLogs(List<BattleLog> battleLogs) {
        int currentRound = 0;
        for (BattleLog log : battleLogs) {
            if (log.getRound() != currentRound) {
                currentRound = log.getRound();
                System.out.println("\n--- 第" + currentRound + "回合 ---");
            }
            System.out.println(log.toString());
        }
    }

    /**
     * 计算队伍总血量
     */
    private static int calculateTeamTotalHp(List<Guardian> team) {
        int totalHp = 0;
        for (Guardian g : team) {
            if (g.isAlive()) {
                totalHp += g.getCurrentHp();
            }
        }
        return totalHp;
    }
}
