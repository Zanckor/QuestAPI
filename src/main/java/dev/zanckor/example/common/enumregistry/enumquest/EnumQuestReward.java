package dev.zanckor.example.common.enumregistry.enumquest;

import dev.zanckor.api.enuminterface.enumquest.IEnumQuestReward;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractReward;
import dev.zanckor.example.common.handler.questreward.*;

public enum EnumQuestReward implements IEnumQuestReward {
    ITEM(new ItemReward()),
    COMMAND(new CommandReward()),
    QUEST(new QuestReward()),
    XP(new XpReward()), LEVEL(new XpReward()), POINTS(new XpReward()),
    LOOT_TABLE(new LootTableReward());

    AbstractReward reward;

    EnumQuestReward(AbstractReward reward) {
        this.reward = reward;
        registerEnumReward(this.getClass());
    }

    @Override
    public AbstractReward getReward() {
        return reward;
    }
}
