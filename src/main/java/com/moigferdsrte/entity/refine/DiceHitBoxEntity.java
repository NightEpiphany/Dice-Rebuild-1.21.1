package com.moigferdsrte.entity.refine;

import com.moigferdsrte.entity.refine.sticky.StickyDiceCubeEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.EntityTrackerEntry;

public class DiceHitBoxEntity extends Entity {

    private final Entity dice;

    public DiceHitBoxEntity(DiceCubeEntity dice) {
        super(dice.getType(), dice.getWorld());
        this.calculateDimensions();
        this.dice = dice;
    }

    public DiceHitBoxEntity(StickyDiceCubeEntity dice) {
        super(dice.getType(), dice.getWorld());
        this.calculateDimensions();
        this.dice = dice;
    }

    @Override
    public void tick() {
        super.tick();
        this.move(MovementType.SELF,dice.getVelocity());
        this.setPosition(dice.getPos());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(1,1);
    }

    public ItemStack getPickBlockStack() {
        return this.dice.getPickBlockStack();
    }

    public boolean canHit() {
        return true;
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        return dice.handleAttack(attacker);
    }

    public boolean isPartOf(Entity entity) {
        return this == entity || this.dice == entity;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }
    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
        throw new UnsupportedOperationException();
    }
    public boolean shouldSave() {
        return false;
    }
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
