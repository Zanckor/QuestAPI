package dev.zanckor.example.common.enumregistry.enumquest;

import dev.zanckor.api.enuminterface.enumquest.IEnumQuestRequirement;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractQuestRequirement;
import dev.zanckor.example.common.handler.questrequirement.XpRequirement;

public enum EnumQuestRequirement implements IEnumQuestRequirement {
    XP(new XpRequirement());

    AbstractQuestRequirement questRequirement;

    EnumQuestRequirement(AbstractQuestRequirement abstractQuestRequirement) {
        questRequirement = abstractQuestRequirement;
        registerEnumQuestReq(this.getClass());
    }

    @Override
    public AbstractQuestRequirement getRequirement() {
        return questRequirement;
    }
}
