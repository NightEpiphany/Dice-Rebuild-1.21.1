package com.moigferdsrte.entity.refine.sticky;

import com.moigferdsrte.entity.refine.DiceHitBoxEntity;
import com.moigferdsrte.entity.refine.RefineRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

public class StickyDiceCubeEntity extends Entity {

    private static final TrackedData<Integer> POINT = DataTracker.registerData(StickyDiceCubeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static TrackedData<Float> ROLL_FLOOR = DataTracker.registerData(StickyDiceCubeEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static TrackedData<Float> PREVIOUS_Y_VEL = DataTracker.registerData(StickyDiceCubeEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static TrackedData<Boolean> TYPE = DataTracker.registerData(StickyDiceCubeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> OWNER = DataTracker.registerData(StickyDiceCubeEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> LUCK_INDICATOR = DataTracker.registerData(StickyDiceCubeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> COLLIDE = DataTracker.registerData(StickyDiceCubeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ILLUMINANCE = DataTracker.registerData(StickyDiceCubeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final String ROLL_FLOOR_TAG = "roll";
    public static final String PREVIOUS_Y_VEL_TAG = "previous_y_velocity";
    public static final String POINT_TAG = "roll_point";
    public static final String TYPE_TAG = "dice_type";
    public static final String OWNER_TAG = "dice_owner";
    public static final String LUCK_TAG = "luck";
    public static final String ILLUMINANCE_TAG = "illuminance";

    private final DiceHitBoxEntity hitbox;

    public StickyDiceCubeEntity(EntityType<StickyDiceCubeEntity> type, World world) {
        super(type, world);
        this.hitbox = new DiceHitBoxEntity(this);
    }

    public StickyDiceCubeEntity(World world, double x, double y, double z) {
        this(RefineRegistry.STICKY_DICE, world);
        this.setPosition(x, y, z);
    }

    @Override
    public void onBubbleColumnCollision(boolean drag) {
        super.onBubbleColumnCollision(false);
    }

    public void setOwner(String ownerTag) {
        this.getDataTracker().set(OWNER, ownerTag);
    }

    public boolean hasOwner() {
        return !this.getDataTracker().get(OWNER).isEmpty();
    }

    public boolean sameOwner(Entity entity) {
        if (!entity.getWorld().getGameRules().getBoolean(RefineRegistry.DICE_PRIVACY)) return true;
        else if (entity instanceof PlayerEntity player && player.isCreative()) return true;
        return this.hasOwner() && this.getDataTracker().get(OWNER).equals(entity.getUuid().toString()) || !this.hasOwner();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            if (this.sameOwner(player) && this.hasOwner()|| !this.hasOwner() && !player.getWorld().getGameRules().getBoolean(RefineRegistry.DICE_PRIVACY)) {
                this.getDataTracker().set(POINT, (this.getDataTracker().get(POINT) == 6 ? 1 : this.getDataTracker().get(POINT) + 1));
                this.playSound(this.getDiceType() ? SoundEvents.BLOCK_SLIME_BLOCK_PLACE : SoundEvents.BLOCK_HONEY_BLOCK_PLACE, 1.0F,  1.0F);
            }
            return ActionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(POINT, 1);
        builder.add(ROLL_FLOOR,0f);
        builder.add(PREVIOUS_Y_VEL,0f);
        builder.add(TYPE, false);
        builder.add(OWNER, "");
        builder.add(LUCK_INDICATOR, 0);
        builder.add(COLLIDE, true);
        builder.add(ILLUMINANCE, false);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.getDataTracker().set(ROLL_FLOOR, nbt.getFloat(ROLL_FLOOR_TAG));
        this.getDataTracker().set(PREVIOUS_Y_VEL, nbt.getFloat(PREVIOUS_Y_VEL_TAG));
        this.getDataTracker().set(POINT, nbt.getInt(POINT_TAG));
        this.getDataTracker().set(TYPE, nbt.getBoolean(TYPE_TAG));
        this.getDataTracker().set(OWNER, nbt.getString(OWNER_TAG));
        this.getDataTracker().set(LUCK_INDICATOR, nbt.getInt(LUCK_TAG));
        this.getDataTracker().set(ILLUMINANCE, nbt.getBoolean(ILLUMINANCE_TAG));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat(ROLL_FLOOR_TAG, this.getDataTracker().get(ROLL_FLOOR));
        nbt.putFloat(PREVIOUS_Y_VEL_TAG, this.getDataTracker().get(PREVIOUS_Y_VEL));
        nbt.putInt(POINT_TAG, this.getDataTracker().get(POINT));
        nbt.putBoolean(TYPE_TAG, this.getDiceType());
        nbt.putString(OWNER_TAG, this.getDataTracker().get(OWNER));
        nbt.putInt(LUCK_TAG, this.getDataTracker().get(LUCK_INDICATOR));
        nbt.putBoolean(ILLUMINANCE_TAG, this.getDataTracker().get(ILLUMINANCE));
    }

    private void spawnParticles(int amount) {
        if (amount > 0) {
            for (int j = 0; j < amount; j++) {
                this.getWorld()
                        .addParticle(
                                this.getDiceType() ? ParticleTypes.ITEM_SLIME : ParticleTypes.FALLING_HONEY, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 0.0, 0.0, 0.0
                        );
            }
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getDiceType() ? SoundEvents.ENTITY_SLIME_JUMP : SoundEvents.BLOCK_HONEY_BLOCK_BREAK, 1.0F, 1.0F);
    }

    @Override
    protected void playSecondaryStepSound(BlockState state) {
        this.playSound(this.getDiceType() ? SoundEvents.ENTITY_SLIME_JUMP : SoundEvents.BLOCK_HONEY_BLOCK_BREAK, 1.0F, 1.0F);
    }

    @Override
    protected void playCombinationStepSounds(BlockState primaryState, BlockState secondaryState) {
        this.playSound(this.getDiceType() ? SoundEvents.ENTITY_SLIME_JUMP : SoundEvents.BLOCK_HONEY_BLOCK_BREAK, 1.0F, 1.0F);
    }

    public boolean getDiceType(){
        return this.getDataTracker().get(TYPE);
    }

    public void setDiceType(boolean set){
        this.getDataTracker().set(TYPE, set);
    }

    @Override
    public boolean collidesWithStateAtPos(BlockPos pos, BlockState state) {
        return super.collidesWithStateAtPos(pos, state);
    }

    @Override
    public boolean collidesWith(Entity other) {
        if(this.getVelocity().length() > 0.1){
            Vec3d vecPush = this.getVelocity().multiply(0.05f);
            Vec3d vecFinal = other.getVelocity().add(vecPush);
            vecFinal = new Vec3d(MathHelper.clamp(vecFinal.getX(),-0.05,0.05),MathHelper.clamp(vecFinal.getY(),-5,5),MathHelper.clamp(vecFinal.getZ(),-5,5));
            other.setVelocity(vecFinal);
            this.setVelocity(this.getVelocity().multiply(1/1600f));
            this.setNoGravity(true);
            other.updateVelocity(0.02F,other.getVelocity());
        }
        if(other instanceof StickyDiceCubeEntity)
        {
            return true;
        }
        return super.collidesWith(other);
    }

    @Override
    public void onLanding() {
        super.onLanding();
    }

    public int getRandomPoint(){
        return this.dataTracker.get(POINT);
    }

    public boolean getCollide() {
        return this.getDataTracker().get(COLLIDE);
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return this.getCollide();
    }

    public void setIlluminance(boolean set) {
        this.getDataTracker().set(ILLUMINANCE, set);
    }

    public boolean getIlluminance() {
        return this.getDataTracker().get(ILLUMINANCE);
    }

    @Override
    public boolean handleAttack(Entity attacker) {

        if (attacker.isPlayer() && !attacker.isSpectator() && !this.isRemoved())
        {
            if (attacker.isSneaking() && this.isAttackable() && this.sameOwner(attacker)) {
                killIt(attacker instanceof PlayerEntity player && !player.isCreative());
            }
            else if (this.sameOwner(attacker)){
                this.luckIndicatorModifier(attacker);
                this.playSound(this.getDiceType() ? SoundEvents.ENTITY_SLIME_JUMP : SoundEvents.BLOCK_HONEY_BLOCK_PLACE, 1.0F, 1.0F);
                this.addVelocity(0, 0.2f + attacker.getWorld().getRandom().nextInt(1) / 20f, 0);
            }
        }
        return super.handleAttack(attacker);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if(source.isDirect() && source.getAttacker() == null){

            killIt(true);
        }
        return super.damage(source, amount);
    }

    public void killIt(boolean dropItem) {

        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), this.getDiceType() ? SoundEvents.BLOCK_SLIME_BLOCK_BREAK : SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundCategory.AMBIENT, 1.0F, 1.0F);

        if(dropItem) {this.dropStack(this.getPickBlockStack());}

        this.discardWithChild();
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack() {
        return this.getDiceType() ? new ItemStack(RefineRegistry.SLIME_DICE, 1) : new ItemStack(RefineRegistry.HONEY_DICE, 1);
    }

    public void setRandomPoint(int point){
        this.getDataTracker().set(POINT, point);
    }

    @Override
    public void tick() {
        if (!this.isOnGround() && !this.isTouchingWater()){
            Random random = new Random();
            if (this.getLuckIndicator() >= 0)
                switch (this.getLuckIndicator()) {
                    case 0 -> this.setRandomPoint(random.nextInt(6) + 1);
                    case 1 -> this.setRandomPoint(random.nextInt(5) + 2);
                    case 2 -> this.setRandomPoint(random.nextInt(4) + 3);
                    case 3 -> this.setRandomPoint(random.nextInt(3) + 4);
                    case 4 -> this.setRandomPoint(random.nextInt(2) + 5);
                    default -> this.setRandomPoint(6);
                }
            else
                switch (this.getLuckIndicator()) {
                    case -1 -> this.setRandomPoint(random.nextInt(5) + 1);
                    case -2 -> this.setRandomPoint(random.nextInt(4) + 1);
                    case -3 -> this.setRandomPoint(random.nextInt(3) + 1);
                    case -4 -> this.setRandomPoint(random.nextInt(2) + 1);
                    default -> this.setRandomPoint(1);
                }
        }
        if (this.getWorld().isClient) {
            if (this.isOnGround()) {
                if (this.age % 50 == 0) {
                    this.spawnParticles(1);
                }
            } else {
                this.spawnParticles(2);
            }
        }

        if (this.isInLava()) this.discardWithChild();
        this.addVelocity(new Vec3d(0,-0.09,0));
        this.move(MovementType.SELF, this.getVelocity());
        if(this.isTouchingWater())
        {

            this.addVelocity(new Vec3d(0,0.23,0));
            this.setVelocity(this.getVelocity().multiply(0.75,0.75,0.75));
        }
        if(this.isOnGround() || this.collidesWithStateAtPos(this.getBlockPos(), this.getBlockStateAtPos())) {
            this.setVelocity(this.getVelocity().multiply(0,0,0));
            if(MathHelper.abs((float) this.getVelocity().getY()) < 0.01) {
                this.addVelocity(0,-getDataTracker().get(PREVIOUS_Y_VEL),0);
            }
        } else {
            this.getDataTracker().set(ROLL_FLOOR,getDataTracker().get(ROLL_FLOOR)+(float)this.getVelocity().lengthSquared());
            this.setVelocity(this.getVelocity().multiply(0.08,0.69f,0.08));
        }
        this.getDataTracker().set(PREVIOUS_Y_VEL,(float)this.getVelocity().getY());
        this.hitbox.setPosition(this.getPos());
        this.hitbox.updateTrackedPosition(hitbox.getX(),hitbox.getY(),hitbox.getZ());
        this.hitbox.prevX = this.prevX;
        this.hitbox.prevY = this.prevY;
        this.hitbox.prevZ = this.prevZ;
        this.hitbox.lastRenderX = this.lastRenderX;
        this.hitbox.lastRenderY = this.lastRenderY;
        this.hitbox.lastRenderZ = this.lastRenderZ;
        hitbox.tick();
        super.tick();
    }

    public void luckIndicatorModifier(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            if (player.hasStatusEffect(StatusEffects.LUCK)) {
                this.setLuckIndicator(Objects.requireNonNull(player.getStatusEffect(StatusEffects.LUCK)).getAmplifier());
            }else if (player.hasStatusEffect(StatusEffects.UNLUCK)) {
                this.setLuckIndicator(Objects.requireNonNull(player.getStatusEffect(StatusEffects.UNLUCK)).getAmplifier() * -1);
            }else this.setLuckIndicator(0);
        }
    }

    public void discardWithChild(){
        this.hitbox.discard();
        this.discard();
    }

    public int getLuckIndicator() {
        if (this.hasOwner()) return this.getDataTracker().get(LUCK_INDICATOR);
        return 0;
    }

    public void setLuckIndicator(int indicator) {
        if (this.hasOwner()) this.getDataTracker().set(LUCK_INDICATOR, indicator);
        else this.getDataTracker().set(LUCK_INDICATOR, 0);
    }
}
