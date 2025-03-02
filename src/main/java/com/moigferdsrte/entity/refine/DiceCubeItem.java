package com.moigferdsrte.entity.refine;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DiceCubeItem extends Item {

    private static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
        private final ItemDispenserBehavior defaultBehavior = new ItemDispenserBehavior();

        @Override
        public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            if (stack.isOf(ItemStack.EMPTY.getItem())){
                return this.defaultBehavior.dispense(pointer, stack);
            }
            Direction direction = pointer.state().get(DispenserBlock.FACING);
            ServerWorld serverWorld = pointer.world();
            Vec3d vec3d = pointer.centerPos();
            double d = vec3d.getX() + (double)direction.getOffsetX() * 1.125;
            double e = Math.floor(vec3d.getY()) + (double)direction.getOffsetY();
            double f = vec3d.getZ() + (double)direction.getOffsetZ() * 1.125;
            double g = 0.1f;
            DiceCubeEntity diceCubeEntity = new DiceCubeEntity(serverWorld,d, e + g, f);
            diceCubeEntity.setVelocity(
                    shoot(direction, 0.15f + MathHelper.cos((float) e), 0.75f + MathHelper.sin((float) e))
            );
            diceCubeEntity.setColor(set(stack));
            serverWorld.spawnEntity(diceCubeEntity);
            stack.decrement(1);
            return stack;
        }

        private Vec3d shoot(Direction direction, float impact, float speed){
            float x =0;
            float y =0;
            float z =0;
            if (direction == Direction.NORTH) z = -speed;
            else if (direction == Direction.EAST) x = speed;
            else if (direction == Direction.UP) y = speed;
            else if (direction == Direction.DOWN) y = -speed;
            else if (direction == Direction.SOUTH) z = speed;
            else x = -speed;
            Random random = new Random();
            float coefficient = random.nextInt(3) - 1 * random.nextFloat();
            return new Vec3d(x + coefficient * impact, y + coefficient * impact ,z + coefficient * impact);
        }

        private DyeColor set(ItemStack stack){
            if (stack.isOf(RefineRegistry.BLACK_DICE))
                return DyeColor.BLACK;
            else if (stack.isOf(RefineRegistry.BLUE_DICE))
                return DyeColor.BLUE;
            else if (stack.isOf(RefineRegistry.BROWN_DICE))
                return DyeColor.BROWN;
            else if (stack.isOf(RefineRegistry.CYAN_DICE))
                return DyeColor.CYAN;
            else if (stack.isOf(RefineRegistry.GRAY_DICE))
                return DyeColor.GRAY;
            else if (stack.isOf(RefineRegistry.GREEN_DICE))
                return DyeColor.GREEN;
            else if (stack.isOf(RefineRegistry.LIGHT_BLUE_DICE))
                return DyeColor.LIGHT_BLUE;
            else if (stack.isOf(RefineRegistry.LIGHT_GRAY_DICE))
                return DyeColor.LIGHT_GRAY;
            else if (stack.isOf(RefineRegistry.LIME_DICE))
                return DyeColor.LIME;
            else if (stack.isOf(RefineRegistry.PURPLE_DICE))
                return DyeColor.PURPLE;
            else if (stack.isOf(RefineRegistry.PINK_DICE))
                return DyeColor.PINK;
            else if (stack.isOf(RefineRegistry.ORANGE_DICE))
                return DyeColor.ORANGE;
            else if (stack.isOf(RefineRegistry.MAGENTA_DICE))
                return DyeColor.MAGENTA;
            else if (stack.isOf(RefineRegistry.RED_DICE))
                return DyeColor.RED;
            else if (stack.isOf(RefineRegistry.WHITE_DICE))
                return DyeColor.WHITE;
            else {
                return DyeColor.YELLOW;
            }
        }

        @Override
        protected void playSound(BlockPointer pointer) {
            pointer.world().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pointer.pos(), 0);
        }
    };


    private final DyeColor color;

    public DiceCubeItem(DyeColor color, Settings settings) {
        super(settings);
        this.color = color;
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user.getStackInHand(hand).getItem() instanceof DiceCubeItem)
        {
            user.getStackInHand(hand).decrementUnlessCreative(1,user);
            Vec3d spawnPos = user.getPos().add(0,user.getFacing() == Direction.DOWN && !user.isOnGround() && user.getWorld().getBlockState(user.getBlockPos().add(0, 2, 0)).isIn(BlockTags.AIR) ? user.getEyeHeight(user.getPose()) - 1.125 : user.getEyeHeight(user.getPose()),0).add(user.getRotationVector().multiply(1.5));
            DiceCubeEntity diceCubeEntity = new DiceCubeEntity(world,spawnPos.getX(),spawnPos.getY(),spawnPos.getZ());
            if (user.getEquippedStack(EquipmentSlot.OFFHAND).isOf(Items.GLOWSTONE_DUST))
                diceCubeEntity.setIlluminance(true);
            user.getEquippedStack(EquipmentSlot.OFFHAND).decrementUnlessCreative(1, user);
            diceCubeEntity.setVelocity(user.getRotationVector().multiply(1));
            diceCubeEntity.setColor(this.color);
            diceCubeEntity.setOwner(user.getUuidAsString());
            if (user.hasStatusEffect(StatusEffects.LUCK) && user.hasStatusEffect(StatusEffects.UNLUCK)) {
                int m = Objects.requireNonNull(user.getStatusEffect(StatusEffects.LUCK)).getAmplifier();
                int n = Objects.requireNonNull(user.getStatusEffect(StatusEffects.UNLUCK)).getAmplifier();
                diceCubeEntity.setLuckIndicator(m - n);
            }else if (user.hasStatusEffect(StatusEffects.LUCK)) {
                int i = Objects.requireNonNull(user.getStatusEffect(StatusEffects.LUCK)).getAmplifier();
                diceCubeEntity.setLuckIndicator(i);
            }else if (user.hasStatusEffect(StatusEffects.UNLUCK)) {
                int j = Objects.requireNonNull(user.getStatusEffect(StatusEffects.UNLUCK)).getAmplifier() * -1;
                diceCubeEntity.setLuckIndicator(j);
            }
            world.spawnEntity(diceCubeEntity);
            user.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);
            user.getItemCooldownManager().set(this,4);
            return TypedActionResult.success(user.getStackInHand(hand),true);
        }
        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (type.isCreative()) {
            tooltip.add(Text.translatable("dice.glowstone_dust_appendable").formatted(Formatting.GOLD));
        }
    }
}
