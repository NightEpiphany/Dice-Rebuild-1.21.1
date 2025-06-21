package com.moigferdsrte.entity.refine;

import com.moigferdsrte.particle.DiceCubeParticle;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

public class DiceCubeEntity extends Entity {

    /**
     * The entity class for the rolling dice.
     * <p>The dice has the bouncing properties, the dynamic motion animation is in the {@link DiceCubeEntityRenderer}.</p>
     * <p>Entity class is used for some properties declaration & static pose set.</p>
     * <p>The raw event trigger is {@link Entity#isOnGround()}</p>
     * <p>The current dice point calculate event trigger is {@link #isOnGroundForA2MillSeconds()}</p>
     * @author: NightEpiphany
     */

    //TODO:Explosive Dice

    public static TrackedData<Float> ROLL_FLOOR = DataTracker.registerData(DiceCubeEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static TrackedData<Float> PREVIOUS_Y_VEL = DataTracker.registerData(DiceCubeEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Byte> COLOR = DataTracker.registerData(DiceCubeEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> EVENT_USE = DataTracker.registerData(DiceCubeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> POINT = DataTracker.registerData(DiceCubeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<String> OWNER = DataTracker.registerData(DiceCubeEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> LUCK_INDICATOR = DataTracker.registerData(DiceCubeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> COLLIDE = DataTracker.registerData(DiceCubeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ILLUMINANCE = DataTracker.registerData(DiceCubeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int diceAge;
    private long onGroundStartTime;
    private static final long REQUIRED_TIME = 200;

    public static final String ROLL_FLOOR_TAG = "roll";
    public static final String PREVIOUS_Y_VEL_TAG = "previous_y_velocity";
    public static final String EVENT_USE_TAG = "for_event_use";
    public static final String POINT_TAG = "roll_point";
    public static final String COLOR_TAG = "color";
    public static final String OWNER_TAG = "dice_owner";
    public static final String LUCK_TAG = "luck";
    public static final String AGE_TAG = "age";
    public static final String ILLUMINANCE_TAG = "illuminance";

    private final DiceHitBoxEntity hitbox;

    public DiceCubeEntity(EntityType<DiceCubeEntity> type, World world) {
        super(type, world);
        this.hitbox = new DiceHitBoxEntity(this);
    }

    public DiceCubeEntity(World world, double x, double y, double z) {
        this(RefineRegistry.DICE, world);
        this.setPosition(x, y, z);
        this.setColor(generateDefaultColor());
    }

    @Override
    public void onBubbleColumnCollision(boolean drag) {
        super.onBubbleColumnCollision(false);
    }

    public void initGroundTimer(){
        this.onGroundStartTime = 0;
    }

    public boolean isOnGroundForA2MillSeconds() {
        if (this.isOnGround()) {
            if (onGroundStartTime == 0) {
                onGroundStartTime = System.currentTimeMillis();
            }

            return System.currentTimeMillis() - onGroundStartTime >= REQUIRED_TIME;
        } else {
            onGroundStartTime = 0;
        }

        return false;
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        super.onPlayerCollision(player);
    }

    public boolean isNotFarFrom(Entity entity, double distant) {
        return Math.abs(entity.getX() - this.getX()) <= distant
                && Math.abs(entity.getY() - this.getY()) <= distant
                && Math.abs(entity.getZ() - this.getZ()) <= distant;
    }

    @Override
    public void onLanding() {
        this.initGroundTimer();
        if (this.isOnGroundForA2MillSeconds()) super.onLanding();
    }

    public void setOwner(String ownerTag) {
        this.setNeverDiscardNaturally();
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

    public int getLuckIndicator() {
        if (this.hasOwner()) return this.getDataTracker().get(LUCK_INDICATOR);
        return 0;
    }

    public void setLuckIndicator(int indicator) {
        if (this.hasOwner()) this.getDataTracker().set(LUCK_INDICATOR, indicator);
        else this.getDataTracker().set(LUCK_INDICATOR, 0);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.getDataTracker().get(EVENT_USE) && player.isSneaking()) {
            if (this.sameOwner(player) && this.hasOwner() || !this.hasOwner() && !player.getWorld().getGameRules().getBoolean(RefineRegistry.DICE_PRIVACY)) {
                this.getDataTracker().set(POINT, (this.getDataTracker().get(POINT) == 6 ? 1 : this.getDataTracker().get(POINT) + 1));
                this.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0F, 1.0F);
            }
            return ActionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    private void spawnParticles(int amount) {
        int i = getDyedColor(this.getColor());
        if (i != -1 && amount > 0) {
            for (int j = 0; j < amount; j++) {
                this.getWorld()
                        .addParticle(
                                DiceCubeParticle.create(RefineRegistry.DICE_PARTICLE, i), this.getParticleX(0.525), this.getRandomBodyY(), this.getParticleZ(0.5), 0.0, 0.0, 0.0
                        );
            }
        }
    }

    @Override
    public void tick() {

        if (this.diceAge != -32768) this.diceAge++;

        if (!this.isOnGround() && !this.isTouchingWater() && !this.isForEventUse()) {
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

        if (this.getWorld().isClient && this.getIlluminance()) {
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
        if(this.isOnGround()) {
            this.setVelocity(this.getVelocity().multiply(0.1,1,0.1));
            if(MathHelper.abs((float) this.getVelocity().getY()) < 0.01){
                this.addVelocity(0,-getDataTracker().get(PREVIOUS_Y_VEL),0);
            }
        }
        else {
            this.getDataTracker().set(ROLL_FLOOR,getDataTracker().get(ROLL_FLOOR)+(float)this.getVelocity().lengthSquared());
            this.setVelocity(this.getVelocity().multiply(0.98,1,0.98));
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

        if (!this.getWorld().isClient
                && this.diceAge >= this.getWorld().getGameRules().getInt(RefineRegistry.EVENT_USE_DICE_DECAY_TIME)
                && this.isForEventUse()
        ) this.discardWithChild();

        super.tick();
    }

    public void setNeverDiscardNaturally() {
        this.diceAge = -32768;
    }

    public void setEventUse(boolean set) {
        this.getDataTracker().set(EVENT_USE, set);
    }

    public boolean isForEventUse() {
        return this.getDataTracker().get(EVENT_USE);
    }

    public boolean isForEventPlayerGambleUse() {
        return !this.isForEventUse() || this.hasOwner();
    }

    public boolean getCollide() {
        return this.getDataTracker().get(COLLIDE);
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return this.getCollide();
    }

    @Override
    public boolean canHit() {
        return true;
    }

    public int getRandomPoint() {
        return this.getDataTracker().get(POINT);
    }

    public void setRandomPoint(int point){
        this.getDataTracker().set(POINT, point);
    }

    public void setIlluminance(boolean set) {
        this.getDataTracker().set(ILLUMINANCE, set);
    }

    public boolean getIlluminance() {
        return this.getDataTracker().get(ILLUMINANCE);
    }

    public void luckIndicatorModifier(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            if (player.hasStatusEffect(StatusEffects.LUCK) && player.hasStatusEffect(StatusEffects.UNLUCK)) {
                this.setLuckIndicator(Objects.requireNonNull(player.getStatusEffect(StatusEffects.LUCK)).getAmplifier() - Objects.requireNonNull(player.getStatusEffect(StatusEffects.UNLUCK)).getAmplifier());
            }else if (player.hasStatusEffect(StatusEffects.LUCK)) {
                this.setLuckIndicator(Objects.requireNonNull(player.getStatusEffect(StatusEffects.LUCK)).getAmplifier());
            }else if (player.hasStatusEffect(StatusEffects.UNLUCK)) {
                this.setLuckIndicator(Objects.requireNonNull(player.getStatusEffect(StatusEffects.UNLUCK)).getAmplifier() * -1);
            }else this.setLuckIndicator(0);
        }
    }

    @Override
    public boolean handleAttack(Entity attacker) {

        if (attacker.isPlayer() && !attacker.isSpectator() && !this.isRemoved())
        {
            if (attacker.isSneaking() && this.isAttackable() && this.sameOwner(attacker) && this.isNotFarFrom(attacker, 3)) {
                if (this.isForEventUse() && this.age >= 120 || ! this.isForEventUse())
                    killIt(attacker instanceof PlayerEntity player && !player.isCreative() && this.isForEventPlayerGambleUse());
            }
            else {
                if (!this.isForEventUse() && this.sameOwner(attacker)) {
                    this.luckIndicatorModifier(attacker);
                    Vec3d bounceVec = attacker.getRotationVector().add(this.getVelocity());
                    if (attacker.fallDistance > 0.1) {
                        bounceVec = bounceVec.multiply(1, -1, 1);
                    }
                    this.addVelocity(bounceVec.x, 2 * bounceVec.y + 1.53f, bounceVec.z);
                }else this.addVelocity(0, 0, 0);
            }
        }
        return super.handleAttack(attacker);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if(source.isDirect() && source.getAttacker() == null) {

            killIt(!source.isOf(DamageTypes.IN_FIRE) && !source.isOf(DamageTypes.ON_FIRE));

        }
        return super.damage(source, amount);
    }

    public void killIt(boolean dropItem) {

        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.AMBIENT, 1.0F, 1.0F);

        if(dropItem) {this.dropStack(this.getPickBlockStack());}

        this.discardWithChild();
    }

    public void discardWithChild() {
        this.hitbox.discard();
        this.discard();
    }

    public static int getDyedColor(DyeColor color) {
        if (color == DyeColor.WHITE) {
            int i =  color.getEntityColor();
            return ColorHelper.Argb.getArgb(
                    255,
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getRed(i) * 1.025F), 255),
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getGreen(i) * 1.025F), 255),
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getBlue(i) * 1.025F), 255)
            );
        }else if (color == DyeColor.PINK) {
            int i = ColorHelper.Argb.fullAlpha(14381973);
            float brightnessFactor = 1.2125F;
            return ColorHelper.Argb.getArgb(
                    255,
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getRed(i) * brightnessFactor), 255),
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getGreen(i) * brightnessFactor), 255),
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getBlue(i) * brightnessFactor), 255)
            );
        }else if (color == DyeColor.CYAN) {
            int i = ColorHelper.Argb.fullAlpha(151115456);
            float brightnessFactor = 1.0525F;
            return ColorHelper.Argb.getArgb(
                    255,
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getRed(i) * brightnessFactor), 255),
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getGreen(i) * brightnessFactor), 255),
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getBlue(i) * brightnessFactor), 255)
            );
        }else if (color == DyeColor.YELLOW) {
            int i = ColorHelper.Argb.fullAlpha(16767545);
            float brightnessFactor = 1.2628F;
            return ColorHelper.Argb.getArgb(
                    255,
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getRed(i) * brightnessFactor), 255),
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getGreen(i) * brightnessFactor), 255),
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getBlue(i) * brightnessFactor), 255)
            );
        } else {
            int i = color.getEntityColor();
            float brightnessFactor = 1.25F;
            return ColorHelper.Argb.getArgb(
                    255,
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getRed(i) * brightnessFactor), 255),
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getGreen(i) * brightnessFactor), 255),
                    Math.min(MathHelper.floor((float) ColorHelper.Argb.getBlue(i) * brightnessFactor), 255)
            );
        }
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.dataTracker.get(COLOR) & 15);
    }

    public void setColor(DyeColor color) {
        byte b = this.dataTracker.get(COLOR);
        this.dataTracker.set(COLOR, (byte)(b & 240 | color.getId() & 15));
    }

    public static DyeColor generateDefaultColor() {
        return DyeColor.WHITE;
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack() {
        if (this.getColor() == DyeColor.WHITE) {
            return new ItemStack(RefineRegistry.WHITE_DICE, 1);
        }else if (this.getColor() == DyeColor.BLACK) {
            return new ItemStack(RefineRegistry.BLACK_DICE, 1);
        }else if (this.getColor() == DyeColor.BLUE) {
            return new ItemStack(RefineRegistry.BLUE_DICE, 1);
        }else if (this.getColor() == DyeColor.BROWN) {
            return new ItemStack(RefineRegistry.BROWN_DICE, 1);
        }else if (this.getColor() == DyeColor.CYAN) {
            return new ItemStack(RefineRegistry.CYAN_DICE, 1);
        }else if (this.getColor() == DyeColor.GRAY) {
            return new ItemStack(RefineRegistry.GRAY_DICE, 1);
        }else if (this.getColor() == DyeColor.LIGHT_BLUE) {
            return new ItemStack(RefineRegistry.LIGHT_BLUE_DICE, 1);
        }else if (this.getColor() == DyeColor.LIGHT_GRAY) {
            return new ItemStack(RefineRegistry.LIGHT_GRAY_DICE, 1);
        }else if (this.getColor() == DyeColor.LIME) {
            return new ItemStack(RefineRegistry.LIME_DICE, 1);
        }else if (this.getColor() == DyeColor.PURPLE) {
            return new ItemStack(RefineRegistry.PURPLE_DICE, 1);
        }else if (this.getColor() == DyeColor.PINK) {
            return new ItemStack(RefineRegistry.PINK_DICE, 1);
        }else if (this.getColor() == DyeColor.MAGENTA) {
            return new ItemStack(RefineRegistry.MAGENTA_DICE, 1);
        }else if (this.getColor() == DyeColor.ORANGE) {
            return new ItemStack(RefineRegistry.ORANGE_DICE, 1);
        }else if (this.getColor() == DyeColor.RED) {
            return new ItemStack(RefineRegistry.RED_DICE, 1);
        }else if (this.getColor() == DyeColor.GREEN) {
            return new ItemStack(RefineRegistry.GREEN_DICE, 1);
        }else {
            return new ItemStack(RefineRegistry.YELLOW_DICE, 1);
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(ROLL_FLOOR,0f);
        builder.add(PREVIOUS_Y_VEL,0f);
        builder.add(COLOR, (byte)0);
        builder.add(EVENT_USE, false);
        builder.add(POINT, 1);
        builder.add(OWNER, "");
        builder.add(LUCK_INDICATOR, 0);
        builder.add(COLLIDE, true);
        builder.add(ILLUMINANCE, false);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Override
    public boolean collidesWith(Entity other) {
        if(this.getVelocity().length() > 0.1){
            Vec3d vecPush = this.getVelocity().multiply(0.5f);
            Vec3d vecFinal = other.getVelocity().add(vecPush);
            vecFinal = new Vec3d(MathHelper.clamp(vecFinal.getX(),-5,5),MathHelper.clamp(vecFinal.getY(),-5,5),MathHelper.clamp(vecFinal.getZ(),-5,5));
            other.setVelocity(vecFinal);
            this.setVelocity(this.getVelocity().multiply(1/160f));
            other.updateVelocity(0.02F,other.getVelocity());
        }
        if(other instanceof DiceCubeEntity)
        {
            return true;
        }
        return super.collidesWith(other);
    }

    @Override
    public boolean collidesWithStateAtPos(BlockPos pos, BlockState state) {
        this.setNoGravity(true);
        return super.collidesWithStateAtPos(pos, state);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.getDataTracker().set(ROLL_FLOOR, nbt.getFloat(ROLL_FLOOR_TAG));
        this.getDataTracker().set(PREVIOUS_Y_VEL, nbt.getFloat(PREVIOUS_Y_VEL_TAG));
        this.getDataTracker().set(EVENT_USE, nbt.getBoolean(EVENT_USE_TAG));
        this.getDataTracker().set(POINT, nbt.getInt(POINT_TAG));
        this.setColor(DyeColor.byId(nbt.getByte(COLOR_TAG)));
        this.getDataTracker().set(OWNER, nbt.getString(OWNER_TAG));
        this.getDataTracker().set(LUCK_INDICATOR, nbt.getInt(LUCK_TAG));
        this.diceAge = nbt.getShort(AGE_TAG);
        this.getDataTracker().set(ILLUMINANCE, nbt.getBoolean(ILLUMINANCE_TAG));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat(ROLL_FLOOR_TAG, this.getDataTracker().get(ROLL_FLOOR));
        nbt.putFloat(PREVIOUS_Y_VEL_TAG, this.getDataTracker().get(PREVIOUS_Y_VEL));
        nbt.putBoolean(EVENT_USE_TAG, this.getDataTracker().get(EVENT_USE));
        nbt.putInt(POINT_TAG, this.getDataTracker().get(POINT));
        nbt.putByte(COLOR_TAG, (byte)this.getColor().getId());
        nbt.putString(OWNER_TAG, this.getDataTracker().get(OWNER));
        nbt.putInt(LUCK_TAG, this.getDataTracker().get(LUCK_INDICATOR));
        nbt.putShort(AGE_TAG, (short) this.diceAge);
        nbt.putBoolean(ILLUMINANCE_TAG, this.getDataTracker().get(ILLUMINANCE));
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(RefineRegistry.DICE_ROLLING, 1.0F, 1.0F);
    }

    @Override
    protected void playSecondaryStepSound(BlockState state) {
        this.playSound(RefineRegistry.DICE_ROLLING, 1.0F, 1.0F);
    }

    @Override
    protected void playCombinationStepSounds(BlockState primaryState, BlockState secondaryState) {
        this.playSound(RefineRegistry.DICE_ROLLING, 1.0F, 1.0F);
    }
}
