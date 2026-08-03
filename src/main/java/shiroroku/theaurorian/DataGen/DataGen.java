package shiroroku.theaurorian.DataGen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import shiroroku.theaurorian.TheAurorian;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = TheAurorian.MODID)
public class DataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new DataGenBlocks(output, helper));
        generator.addProvider(event.includeClient(), new DataGenItems(output, helper));
        DataGenBlocksTags blockTags = new DataGenBlocksTags(output, lookup, helper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new DataGenItemsTags(output, lookup, blockTags.contentsGetter(), helper));
        generator.addProvider(event.includeServer(), new DataGenBlocksLoot(output, lookup));
    }

}
