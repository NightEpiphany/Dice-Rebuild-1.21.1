package com.moigferdsrte.entity.extension;

import com.moigferdsrte.entity.refine.DiceCubeEntity;
import com.moigferdsrte.entity.refine.RefineRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProviders;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GamblerEntity extends IllagerEntity implements Angerable {

    @Nullable
    private UUID angryAt;
    private static final String COLOR_LOVER_KEY = "Color";
    private static final String GAMBLED_TIME_KEY = "GambledTime";
    private static final TrackedData<Boolean> GAMBLING = DataTracker.registerData(GamblerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ANGER = DataTracker.registerData(GamblerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> COLOR_PREFERENCE = DataTracker.registerData(GamblerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> RIVAL_POINT = DataTracker.registerData(GamblerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> GAMBLER_POINT = DataTracker.registerData(GamblerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> GAMBLED_TIME = DataTracker.registerData(GamblerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(25, 60);
    private int angerTime;
    private boolean wantsGamble = false;

    public static final String NAME_SUFFIX = "dice lover";

    public GamblerEntity(EntityType<? extends IllagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target == null) {
            this.getDataTracker().set(ANGER, false);
        } else if (target.hasStatusEffect(StatusEffects.BAD_OMEN) || target.hasStatusEffect(StatusEffects.TRIAL_OMEN) || target.hasStatusEffect(StatusEffects.RAID_OMEN)){
            this.getDataTracker().set(ANGER, true);
        }
        super.setTarget(target);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() instanceof PlayerEntity player && !player.isCreative()
                || source.getAttacker() instanceof PathAwareEntity entity && !entity.getType().isIn(EntityTypeTags.ILLAGER_FRIENDS)
        ){
            this.dataTracker.set(ANGER, true);
        }
        return super.damage(source, amount);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new RaiderEntity.PatrolApproachGoal(this, 10.0F));
        this.goalSelector.add(1, new GamblerRevengeGoal(this).setGroupRevenge());
        this.goalSelector.add(1,new GamblerAttackGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, MerchantEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new UniversalAngerGoal<>(this, true));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 15.0F, 1.0F));
        this.goalSelector.add(6, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(6, new LookAtEntityGoal(this, MobEntity.class, 15.0F));
    }

    public static DefaultAttributeContainer.Builder createGamblerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35F)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.25f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.12f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    public void tick() {
        if (!this.isAngry() && this.dataTracker.get(COLOR_PREFERENCE) != DyeColor.GRAY.getId()){
            this.equipStack(EquipmentSlot.MAINHAND, getDyedDice(this.getPreferenceColor()));
        }

        Box box = new Box(this.getBlockPos()).expand(10.5f).stretch(0.0, this.getWorld().getHeight(), 0.0);
        List<PlayerEntity> list = this.getWorld().getNonSpectatingEntities(PlayerEntity.class, box);
        if (!this.isAngry())
            for (PlayerEntity playerEntity : list) {
                if (playerEntity.getMainHandStack().isOf(Items.EMERALD)
                        || playerEntity.getMainHandStack().isOf(Items.EMERALD_BLOCK)
                        || playerEntity.getOffHandStack().isOf(Items.EMERALD)
                        || playerEntity.getOffHandStack().isOf(Items.EMERALD_BLOCK)
                ) {
                    this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.EMERALD));
                    this.wantsGamble = true;
                }
                else{ this.equipStack(EquipmentSlot.MAINHAND, getDyedDice(this.getPreferenceColor()));
                    this.wantsGamble = false;
                }
            }
        super.tick();
    }

    protected void updateEnchantments(ServerWorldAccess world, Random random, LocalDifficulty localDifficulty) {
        this.enchantMainHandItem(world, random, localDifficulty);

        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                this.enchantEquipment(world, random, equipmentSlot, localDifficulty);
            }
        }
    }

    protected void enchantMainHandItem(ServerWorldAccess world, Random random, LocalDifficulty localDifficulty) {
        this.enchantEquipment(world, EquipmentSlot.MAINHAND, random, 0.25F, localDifficulty);
    }

    protected void enchantEquipment(ServerWorldAccess world, Random random, EquipmentSlot slot, LocalDifficulty localDifficulty) {
        this.enchantEquipment(world, slot, random, 0.5F, localDifficulty);
    }

    private void enchantEquipment(ServerWorldAccess world, EquipmentSlot slot, Random random, float power, LocalDifficulty localDifficulty) {
        ItemStack itemStack = this.getEquippedStack(slot);
        if (!itemStack.isEmpty() && random.nextFloat() < power * localDifficulty.getClampedLocalDifficulty() && !itemStack.isIn(RefineRegistry.DICES) && !itemStack.isOf(Items.EMERALD)) {
            EnchantmentHelper.applyEnchantmentProvider(itemStack, world.getRegistryManager(), EnchantmentProviders.MOB_SPAWN_EQUIPMENT, localDifficulty, random);
            this.equipStack(slot, itemStack);
        }
    }


    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.dataTracker.get(ANGER)) this.gamble(player, hand, this);
        return super.interactMob(player, hand);
    }

    private static DyeColor playerDiceDye(PlayerEntity player) {
        if (player.getInventory().contains(new ItemStack(RefineRegistry.WHITE_DICE))) {
            return DyeColor.WHITE;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.BLACK_DICE))) {
            return DyeColor.BLACK;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.PINK_DICE))) {
            return DyeColor.PINK;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.PURPLE_DICE))) {
            return DyeColor.PURPLE;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.GREEN_DICE))) {
            return DyeColor.GREEN;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.CYAN_DICE))) {
            return DyeColor.CYAN;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.BROWN_DICE))) {
            return DyeColor.BROWN;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.BLUE_DICE))) {
            return DyeColor.BLUE;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.MAGENTA_DICE))) {
            return DyeColor.MAGENTA;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.LIGHT_BLUE_DICE))) {
            return DyeColor.LIGHT_BLUE;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.LIME_DICE))) {
            return DyeColor.LIME;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.GRAY_DICE))) {
            return DyeColor.GRAY;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.ORANGE_DICE))) {
            return DyeColor.ORANGE;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.RED_DICE))) {
            return DyeColor.RED;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.YELLOW_DICE))) {
            return DyeColor.YELLOW;
        }
        if (player.getInventory().contains(new ItemStack(RefineRegistry.LIGHT_GRAY_DICE))) {
            return DyeColor.LIGHT_GRAY;
        }
        return DyeColor.WHITE;
    }

    public void setGamblerPoint(int point) {
        this.getDataTracker().set(GAMBLER_POINT, point);
    }

    public void setRivalPoint(int point) {
        this.getDataTracker().set(RIVAL_POINT, point);
    }

    public int rivalRandomPoint() {
        java.util.Random random = new java.util.Random();
        int i =random.nextInt(6) + 1;
        this.setRivalPoint(i);
        return i;
    }

    public int gamblerRandomPoint() {
        java.util.Random random = new java.util.Random();
        int i =random.nextInt(6) + 1;
        this.setGamblerPoint(i);
        return i;
    }

    private void gamble(PlayerEntity player, Hand hand, GamblerEntity gambler) {
        ItemStack stack = player.getStackInHand(hand);
        World serverWorld = player.getWorld();
        player.getItemCooldownManager().set(stack.getItem(), 40);

        if (gambler.getGambledTime() >= gambler.getWorld().getGameRules().getInt(RefineRegistry.MAXIMUM_GAMBLE_TIME)) {
            player.sendMessage(Text.translatable("dice.gamble_maximum_reached").formatted(Formatting.GRAY));
            gambler.wantsGamble = false;
            return;
        }

        if (stack.isOf(Items.EMERALD)
                && player.getItemCooldownManager().isCoolingDown(stack.getItem())
                && player.getInventory().contains(RefineRegistry.STANDARD_DICES)
                && playerDiceDye(player) != this.getPreferenceColor()
                && !player.hasStatusEffect(StatusEffects.UNLUCK)
                && !player.hasStatusEffect(StatusEffects.LUCK)
                && !gambler.hasStatusEffect(StatusEffects.UNLUCK)
                && !gambler.hasStatusEffect(StatusEffects.LUCK)
                && !serverWorld.isClient
        ) {
            int countIn = stack.getCount();
            gambler.setGambling(true);
            stack.decrement(countIn);
            
            player.sendMessage(Text.translatable("dice.gamble_info", countIn).formatted(Formatting.WHITE));

            Vec3d spawnPos = player.getPos().add(0, player.getEyeHeight(player.getPose()), 0).add(player.getRotationVector().multiply(1.5));
            DiceCubeEntity diceCubeEntity = new DiceCubeEntity(serverWorld, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
            diceCubeEntity.setVelocity(player.getRotationVector().multiply(1));
            diceCubeEntity.setOwner(player.getUuidAsString());
            diceCubeEntity.setEventUse(true);
            diceCubeEntity.setColor(playerDiceDye(player));
            ItemStack stackDice = getDyedDice(playerDiceDye(player));
            if (!player.isCreative()) player.getInventory().removeStack(player.getInventory().indexOf(stackDice));
            player.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);

            Vec3d spawnPos2 = gambler.getPos().add(0, gambler.getEyeHeight(gambler.getPose()), 0).add(gambler.getRotationVector().multiply(1.5));
            DiceCubeEntity diceCubeEntity2 = new DiceCubeEntity(serverWorld, spawnPos2.getX(), spawnPos2.getY(), spawnPos2.getZ());
            diceCubeEntity2.setVelocity(player.getRotationVector().multiply(1));
            diceCubeEntity2.setEventUse(true);
            diceCubeEntity2.setColor(this.getPreferenceColor());
            gambler.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);



            serverWorld.spawnEntity(diceCubeEntity);
            serverWorld.spawnEntity(diceCubeEntity2);

            int playerPoint = gambler.rivalRandomPoint(), gamblerPoint = gambler.gamblerRandomPoint();

            if (!diceCubeEntity.isOnGround() && !diceCubeEntity.isTouchingWater()) diceCubeEntity.setRandomPoint(playerPoint);
            if (!diceCubeEntity2.isOnGround() && !diceCubeEntity2.isTouchingWater()) diceCubeEntity2.setRandomPoint(gamblerPoint);


            Timer timer = new Timer();

            TimerTask task = new TimerTask() {
                public void run() {

                    if (diceCubeEntity.isOnGround() && diceCubeEntity2.isOnGround()) {
                        if (diceCubeEntity.isTouchingWater()
                                || diceCubeEntity2.isTouchingWater()
                                || diceCubeEntity.isRemoved()
                                || diceCubeEntity2.isRemoved()
                                || !diceCubeEntity.isAlive()
                                || !diceCubeEntity2.isAlive()
                                || !diceCubeEntity.isOnGround()
                                || !diceCubeEntity2.isOnGround()
                                || diceCubeEntity.isInsideWall()
                                || diceCubeEntity2.isInsideWall()
                                || diceCubeEntity.isOnFire()
                                || diceCubeEntity2.isOnFire()
                                || diceCubeEntity.isInFluid()
                                || diceCubeEntity2.isInFluid()
                                || !diceCubeEntity.isNotFarFrom(gambler, gambler.getWorld().getGameRules().getInt(RefineRegistry.GAMBLER_DICE_VISIBLE_DISTANCE))
                                || !diceCubeEntity2.isNotFarFrom(gambler, gambler.getWorld().getGameRules().getInt(RefineRegistry.GAMBLER_DICE_VISIBLE_DISTANCE))

                        ) {
                            player.sendMessage(Text.translatable("dice.gamble_inform").formatted(Formatting.GRAY));
                            player.getInventory().insertStack(new ItemStack(Items.EMERALD, countIn));
                            gambler.playSound(SoundEvents.ENTITY_PILLAGER_HURT, 1.0F, 1.0F);
                            gambler.wantsGamble = false;
                            return;
                        }
                        diceCubeEntity.setRandomPoint(gambler.getDataTracker().get(RIVAL_POINT));
                        diceCubeEntity2.setRandomPoint(gambler.getDataTracker().get(GAMBLER_POINT));
                    }

                    gambler.addGambledTime();

                    if (gambler.getDataTracker().get(RIVAL_POINT) > gambler.getDataTracker().get(GAMBLER_POINT)) {
                        player.getInventory().insertStack(new ItemStack(Items.EMERALD, countIn * 2));
                        player.sendMessage(Text.translatable("dice.gamble_win").formatted(Formatting.GREEN));
                        //ClientPlayNetworking.send(new GambleCompetePayload(map));
                        gambler.playSound(SoundEvents.ENTITY_PILLAGER_HURT, 1.0F, 1.0F);
                        if (gambler.getWorld().getRandom().nextBoolean()) {
                            gambler.getDataTracker().set(ANGER, true);
                        } else {
                            gambler.getDataTracker().set(ANGER, false);
                        }
                    } else if (Objects.equals(gambler.getDataTracker().get(RIVAL_POINT), gambler.getDataTracker().get(GAMBLER_POINT))) {
                        player.getInventory().insertStack(new ItemStack(Items.EMERALD, countIn));
                        player.sendMessage(Text.translatable("dice.gamble_fair").formatted(Formatting.WHITE));
                        gambler.playSound(SoundEvents.ENTITY_EVOKER_CAST_SPELL, 1.0F, 1.0F);
                    } else {
                        player.sendMessage(Text.translatable("dice.gamble_lose").formatted(Formatting.RED));
                        gambler.playSound(SoundEvents.ENTITY_PILLAGER_CELEBRATE, 1.0F, 1.0F);
                    }
                    gambler.setGambling(false);
                }
            };
            timer.schedule(task, 6000);
        }else if (stack.isOf(Items.EMERALD) && player.hasStatusEffect(StatusEffects.LUCK)
                || stack.isOf(Items.EMERALD) && player.hasStatusEffect(StatusEffects.UNLUCK)
                || stack.isOf(Items.EMERALD) && gambler.hasStatusEffect(StatusEffects.LUCK)
                || stack.isOf(Items.EMERALD) && gambler.hasStatusEffect(StatusEffects.UNLUCK)
        ) {
            player.sendMessage(Text.translatable("dice.gamble_forbid_luck_amplifier").formatted(Formatting.YELLOW));
        } else if (stack.isOf(Items.EMERALD) && playerDiceDye(player) == this.getPreferenceColor() && player.getInventory().contains(RefineRegistry.STANDARD_DICES)) {
            player.sendMessage(Text.translatable("dice.gamble_need_different_dice").formatted(Formatting.YELLOW));
        } else if (stack.isOf(Items.EMERALD) && player.getInventory().contains(RefineRegistry.DICES) && !player.getInventory().contains(RefineRegistry.STANDARD_DICES)) {
            player.sendMessage(Text.translatable("dice.gamble_need_standard_dice").formatted(Formatting.YELLOW));
        } else if (stack.isOf(Items.EMERALD) && !player.getInventory().contains(RefineRegistry.STANDARD_DICES)) {
            player.sendMessage(Text.translatable("dice.gamble_need_dice").formatted(Formatting.YELLOW));
        }else super.interactMob(player, hand);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(GAMBLING, false);
        builder.add(ANGER, false);
        builder.add(COLOR_PREFERENCE, DyeColor.GRAY.getId());
        builder.add(RIVAL_POINT, 1);
        builder.add(GAMBLER_POINT, 1);
        builder.add(GAMBLED_TIME, 0);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains(COLOR_LOVER_KEY, NbtElement.NUMBER_TYPE)){
            this.dataTracker.set(COLOR_PREFERENCE, nbt.getInt(COLOR_LOVER_KEY));
        }if (nbt.contains(GAMBLED_TIME_KEY, NbtElement.NUMBER_TYPE)) {
            this.dataTracker.set(GAMBLED_TIME, nbt.getInt(GAMBLED_TIME_KEY));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt(COLOR_LOVER_KEY, this.dataTracker.get(COLOR_PREFERENCE));
        nbt.putInt(GAMBLED_TIME_KEY, this.dataTracker.get(GAMBLED_TIME));
    }

    public int getGambledTime() {
        return this.dataTracker.get(GAMBLED_TIME);
    }

    public void addGambledTime() {
        this.dataTracker.set(GAMBLED_TIME, this.getGambledTime() + 1);
    }

    public boolean isGambling(){
        return this.dataTracker.get(GAMBLING);
    }

    public boolean isAngry(){
        return this.dataTracker.get(ANGER);
    }

    public static ItemStack getDyedDice(DyeColor color) {
        if (color == DyeColor.WHITE){
            return new ItemStack(RefineRegistry.WHITE_DICE, 1);
        }else if (color == DyeColor.BLACK){
            return new ItemStack(RefineRegistry.BLACK_DICE, 1);
        }else if (color == DyeColor.PINK){
            return new ItemStack(RefineRegistry.PINK_DICE, 1);
        }else if (color == DyeColor.PURPLE){
            return new ItemStack(RefineRegistry.PURPLE_DICE, 1);
        }else if (color == DyeColor.GREEN){
            return new ItemStack(RefineRegistry.GREEN_DICE, 1);
        }else if (color == DyeColor.CYAN){
            return new ItemStack(RefineRegistry.CYAN_DICE, 1);
        }else if (color == DyeColor.BROWN){
            return new ItemStack(RefineRegistry.BROWN_DICE, 1);
        }else if (color == DyeColor.BLUE){
            return new ItemStack(RefineRegistry.BLUE_DICE, 1);
        }else if (color == DyeColor.MAGENTA){
            return new ItemStack(RefineRegistry.MAGENTA_DICE, 1);
        }else if (color == DyeColor.LIGHT_BLUE){
            return new ItemStack(RefineRegistry.LIGHT_BLUE_DICE, 1);
        }else if (color == DyeColor.LIME){
            return new ItemStack(RefineRegistry.LIME_DICE, 1);
        }else if (color == DyeColor.GRAY){
            return new ItemStack(RefineRegistry.GRAY_DICE, 1);
        }else if (color == DyeColor.ORANGE){
            return new ItemStack(RefineRegistry.ORANGE_DICE, 1);
        }else if (color == DyeColor.RED){
            return new ItemStack(RefineRegistry.RED_DICE, 1);
        }else if (color == DyeColor.YELLOW) {
            return new ItemStack(RefineRegistry.YELLOW_DICE, 1);
        }else if (color == DyeColor.LIGHT_GRAY) {
            return new ItemStack(RefineRegistry.LIGHT_GRAY_DICE, 1);
        }
        return new ItemStack(RefineRegistry.WHITE_DICE, 1);
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        if (name != null && name.getString().equalsIgnoreCase("black " + NAME_SUFFIX)) this.setColorPreference(DyeColor.BLACK);
        if (name != null && name.getString().equalsIgnoreCase("blue " + NAME_SUFFIX)) this.setColorPreference(DyeColor.BLUE);
        if (name != null && name.getString().equalsIgnoreCase("brown " + NAME_SUFFIX)) this.setColorPreference(DyeColor.BROWN);
        if (name != null && name.getString().equalsIgnoreCase("cyan " + NAME_SUFFIX)) this.setColorPreference(DyeColor.CYAN);
        if (name != null && name.getString().equalsIgnoreCase("gray " + NAME_SUFFIX)) this.setColorPreference(DyeColor.GRAY);
        if (name != null && name.getString().equalsIgnoreCase("green " + NAME_SUFFIX)) this.setColorPreference(DyeColor.GREEN);
        if (name != null && name.getString().equalsIgnoreCase("light blue " + NAME_SUFFIX)) this.setColorPreference(DyeColor.LIGHT_BLUE);
        if (name != null && name.getString().equalsIgnoreCase("light gray " + NAME_SUFFIX)) this.setColorPreference(DyeColor.LIGHT_GRAY);
        if (name != null && name.getString().equalsIgnoreCase("lime " + NAME_SUFFIX)) this.setColorPreference(DyeColor.LIME);
        if (name != null && name.getString().equalsIgnoreCase("magenta " + NAME_SUFFIX)) this.setColorPreference(DyeColor.MAGENTA);
        if (name != null && name.getString().equalsIgnoreCase("purple " + NAME_SUFFIX)) this.setColorPreference(DyeColor.PURPLE);
        if (name != null && name.getString().equalsIgnoreCase("pink " + NAME_SUFFIX)) this.setColorPreference(DyeColor.PINK);
        if (name != null && name.getString().equalsIgnoreCase("orange " + NAME_SUFFIX)) this.setColorPreference(DyeColor.ORANGE);
        if (name != null && name.getString().equalsIgnoreCase("red " + NAME_SUFFIX)) this.setColorPreference(DyeColor.RED);
        if (name != null && name.getString().equalsIgnoreCase("white " + NAME_SUFFIX)) this.setColorPreference(DyeColor.WHITE);
        if (name != null && name.getString().equalsIgnoreCase("yellow " + NAME_SUFFIX)) this.setColorPreference(DyeColor.YELLOW);
    }

    public void setColorPreference(DyeColor color){
        this.dataTracker.set(COLOR_PREFERENCE, color.getId());
    }

    public DyeColor getPreferenceColor(){
        switch (this.dataTracker.get(COLOR_PREFERENCE)){
            case 0 -> {
                return DyeColor.WHITE;
            }
            case 1 -> {
                return DyeColor.ORANGE;
            }
            case 2 -> {
                return DyeColor.MAGENTA;
            }
            case 3 -> {
                return DyeColor.LIGHT_BLUE;
            }
            case 4 -> {
                return DyeColor.YELLOW;
            }
            case 5 -> {
                return DyeColor.LIME;
            }
            case 6 -> {
                return DyeColor.PINK;
            }
            case 8 -> {
                return DyeColor.LIGHT_GRAY;
            }
            case 9 -> {
                return DyeColor.CYAN;
            }
            case 10 -> {
                return DyeColor.PURPLE;
            }
            case 11 -> {
                return DyeColor.BLUE;
            }
            case 12 -> {
                return DyeColor.BROWN;
            }
            case 13 -> {
                return DyeColor.GREEN;
            }
            case 14 -> {
                return DyeColor.RED;
            }
            case 15 -> {
                return DyeColor.BLACK;
            }
            default -> {
                return DyeColor.GRAY;
            }
        }
    }

    public void setGambling(boolean gamble){
        this.dataTracker.set(GAMBLING, gamble);
    }

    @Override
    public State getState() {
        if (this.isGambling()) return State.NEUTRAL;
        if (this.isAngry()) return State.ATTACKING;
        if (this.wantsGamble) return State.CELEBRATING;
        return State.CROSSED;
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        Random random = world.getRandom();
        this.initEquipment(random, difficulty);
        this.updateEnchantments(world, random, difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, getDyedDice(this.getPreferenceColor()));
        this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.EMERALD));
    }

    @Override
    protected void drop(ServerWorld world, DamageSource damageSource) {
        super.drop(world, damageSource);
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0F;
    }

    @Override
    public int getLimitPerChunk() {
        return 1;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.wantsGamble ? SoundEvents.ENTITY_VINDICATOR_CELEBRATE : SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ILLUSIONER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ILLUSIONER_HURT;
    }



    @Override
    public void addBonusForWave(ServerWorld world, int wave, boolean unused) {

    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
    }

    @Override
    public int getAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (ANGER.equals(data) && this.getWorld().isClient) {
            this.getWorld().playSound(this, this.getBlockPos(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        }

        super.onTrackedDataSet(data);
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    static class GamblerAttackGoal extends MeleeAttackGoal {

        public GamblerAttackGoal(GamblerEntity mob) {
            super(mob, 0.8f, false);
        }

        @Override
        public void start() {
            this.mob.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            super.start();
        }

        @Override
        public void stop() {
            this.mob.equipStack(EquipmentSlot.MAINHAND, getDyedDice(((GamblerEntity) this.mob).getPreferenceColor()));
            super.stop();
        }

        @Override
        public boolean canStart() {
            return this.mob.getDataTracker().get(ANGER);
        }
    }

    class GamblerRevengeGoal extends RevengeGoal {
        public GamblerRevengeGoal(final GamblerEntity gamblerEntity) {
            super(gamblerEntity);
        }

        @Override
        public boolean shouldContinue() {
            return GamblerEntity.this.hasNoRaid() && super.shouldContinue();
        }

        @Override
        protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
            if (mob instanceof GamblerEntity && this.mob.canSee(target)){
                mob.setTarget(target);
            }
        }
    }
}
