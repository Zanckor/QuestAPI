package dev.zanckor.example.common.enumregistry.enumquest;


import dev.zanckor.api.enuminterface.enumquest.IEnumQuestGoal;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractGoal;
import dev.zanckor.api.filemanager.quest.abstracquest.AbstractTargetType;
import dev.zanckor.example.common.enumregistry.EnumRegistry;
import dev.zanckor.example.common.handler.questgoal.*;
import dev.zanckor.example.common.handler.targettype.*;

public enum EnumGoalType implements IEnumQuestGoal {
    INTERACT_ENTITY(new InteractEntityGoal(), new EntityTargetType()),
    KILL(new KillGoal(), new EntityTargetType()),
    MOVE_TO(new MoveToGoal(), new MoveToTargetType()),
    COLLECT(new CollectGoal(), new ItemTargetType()),
    COLLECT_WITH_NBT(new CollectNBTGoal(), new ItemTargetType()),
    XP(new XpGoal(), new XPTargetType()),
    LOCATE_STRUCTURE(new LocateStructureGoal(), new LocateStructureTargetType());


    AbstractGoal goal;
    AbstractTargetType targetType;

    EnumGoalType(AbstractGoal abstractGoal, AbstractTargetType targetType) {
        this.goal = abstractGoal;
        this.targetType = targetType;

        registerEnumGoal(this.getClass());
    }

    @Override
    public AbstractGoal getQuest() {
        return goal;
    }

    @Override
    public AbstractTargetType getTargetType() {
        return targetType;
    }
}
