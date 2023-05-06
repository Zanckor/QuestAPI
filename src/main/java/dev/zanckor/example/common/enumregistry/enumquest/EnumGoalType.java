package dev.zanckor.example.common.enumregistry.enumquest;


import dev.zanckor.api.enuminterface.enumquest.IEnumQuestGoal;
import dev.zanckor.api.enuminterface.enumquest.IEnumTargetType;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractGoal;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.example.common.handler.questgoal.*;
import dev.zanckor.example.common.handler.targettype.*;

public enum EnumGoalType implements IEnumQuestGoal {
    INTERACT_ENTITY(new InteractEntityGoal()),
    KILL(new KillGoal()),
    MOVE_TO(new MoveToGoal()),
    COLLECT(new CollectGoal()),
    COLLECT_WITH_NBT(new CollectNBTGoal()),
    XP(new XpGoal());


    AbstractGoal goal;
    EnumGoalType(AbstractGoal abstractGoal) {
        this.goal = abstractGoal;
        registerEnumGoal(this.getClass());
    }

    @Override
    public AbstractGoal getQuest() {
        return goal;
    }


    public enum EnumTargetType implements IEnumTargetType{
        TARGET_TYPE_INTERACT_ENTITY(new EntityTargetType()),
        TARGET_TYPE_KILL(new EntityTargetType()),
        TARGET_TYPE_MOVE_TO(new MoveToTargetType()),
        TARGET_TYPE_COLLECT(new ItemTargetType()),
        TARGET_TYPE_COLLECT_WITH_NBT(new ItemTargetType()),
        TARGET_TYPE_XP(new XPTargetType());

        AbstractTargetType targetType;
        EnumTargetType(AbstractTargetType targetType) {
            this.targetType = targetType;
            registerTargetType(this.getClass());
        }

        @Override
        public AbstractTargetType getTargetType() {
            return targetType;
        }
    }
}
