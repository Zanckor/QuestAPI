package dev.zanckor.mod.common.network.message.npcmarker;

import dev.zanckor.mod.common.network.handler.ClientHandler;
import dev.zanckor.mod.common.util.GsonManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Supplier;

import static dev.zanckor.api.database.LocateHash.dialogPerCompoundTag;
import static dev.zanckor.api.database.LocateHash.dialogPerEntityType;

public class ValidNPCMarker {
    List<String> entityTypeList;
    Map<String, String> entityTagMap;

    public ValidNPCMarker() {
    }

    public ValidNPCMarker(FriendlyByteBuf buffer) {
        String entityStringList = buffer.readUtf();
        ArrayList<String> entityTypeList = new ArrayList<>(Arrays.asList(entityStringList.substring(1, entityStringList.length() - 1).split(",")));

        this.entityTypeList = entityTypeList;
        entityTagMap = buffer.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf);
    }

    public void encodeBuffer(FriendlyByteBuf buffer) {
        try {
            buffer.writeUtf(dialogPerEntityType.keySet().toString());

            //Convert String - File to String - String
            HashMap<String, String> entityTagMap = new HashMap<>();

            for (String key : dialogPerCompoundTag.keySet()) {
                String fileContent = Files.readString(dialogPerCompoundTag.get(key).toPath());

                entityTagMap.put(key, fileContent);
            }

            buffer.writeMap(entityTagMap, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void handler(ValidNPCMarker msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.setAvailableEntityTypeForQuest(msg.entityTypeList, msg.entityTagMap));
        });

        ctx.get().setPacketHandled(true);
    }
}