package rearth.oritech.init.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryWrapper;
import rearth.oritech.init.BlockContent;
import rearth.oritech.init.ItemContent;
import rearth.oritech.init.datagen.loot.NbtBlockLootFunction;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BlockLootGenerator extends FabricBlockLootTableProvider {
    
    public static Set<Block> autoRegisteredDrops = new HashSet<>();
    
    public BlockLootGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }
    
    @Override
    public void generate() {
        addOreDrop(BlockContent.NICKEL_ORE, ItemContent.RAW_NICKEL);
        addOreDrop(BlockContent.DEEPSLATE_NICKEL_ORE, ItemContent.RAW_NICKEL);
        addOreDrop(BlockContent.DEEPSLATE_PLATINUM_ORE, ItemContent.RAW_PLATINUM);
        addOreDrop(BlockContent.ENDSTONE_PLATINUM_ORE, ItemContent.RAW_PLATINUM);
        
        for (var block : autoRegisteredDrops) {
            addDrop(block);
        }
        
        addDrop(BlockContent.ITEM_PIPE_CONNECTION, BlockContent.ITEM_PIPE);
        addDrop(BlockContent.FLUID_PIPE_CONNECTION, BlockContent.FLUID_PIPE);
        addDrop(BlockContent.ENERGY_PIPE_CONNECTION, BlockContent.ENERGY_PIPE);

        addCustomDataDrop(BlockContent.SMALL_TANK_BLOCK);
        addCustomDataDrop(BlockContent.SMALL_STORAGE_BLOCK);
        
        LootCondition.Builder cropDropBuilder = BlockStatePropertyLootCondition.builder(BlockContent.WITHER_CROP_BLOCK)
                                           .properties(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, 7));
        addDrop(BlockContent.WITHER_CROP_BLOCK, cropDrops(BlockContent.WITHER_CROP_BLOCK, Blocks.TWISTING_VINES_PLANT.asItem(), BlockContent.WITHER_CROP_BLOCK.asItem(), cropDropBuilder));
    }
    
    private void addOreDrop(Block block, Item item) {
        addDrop(block, oreDrops(block, item));
    }

    private void addCustomDataDrop(Block block) {
        // similar to shulkerBoxDrops
        addDrop(block, LootTable.builder().pool(
            (LootPool.Builder)this.addSurvivesExplosionCondition(block, LootPool.builder()
                                  .rolls(ConstantLootNumberProvider.create(1.0F))
                                  .with(ItemEntry.builder(block).apply(NbtBlockLootFunction.builder())))));
    }
}
