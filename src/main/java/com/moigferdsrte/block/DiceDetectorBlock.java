package com.moigferdsrte.block;

import com.moigferdsrte.entity.refine.DiceCubeEntity;
import com.moigferdsrte.entity.refine.sticky.StickyDiceCubeEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.List;

public class DiceDetectorBlock extends FacingBlock {

    public static final IntProperty POWER = Properties.POWER;
    public static final MapCodec<DiceDetectorBlock> CODEC = createCodec(DiceDetectorBlock::new);

    private final DiceValueIndicator indicator;

    public DiceDetectorBlock(Settings settings) {
        super(settings);
        this.indicator = new DiceValueIndicator(this, 0);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.SOUTH).with(POWER, 0));
    }

    public static int dicePointCalculation(BlockPos pos, BlockState state, World world) {
        int x = 0;
        int y = 0;
        int z = 0;
        switch (state.get(FACING)) {
            case DOWN -> y = -2;
            case UP -> y = 2;
            case EAST -> x = 2;
            case WEST -> x = -2;
            case SOUTH -> z = 2;
            case NORTH -> z = -2;
            default -> {
            }
        }
        Box box = new Box(pos).stretch(x, y, z);
        List<DiceCubeEntity> diceCubeEntities = world.getNonSpectatingEntities(DiceCubeEntity.class, box);
        List<StickyDiceCubeEntity> stickyDiceCubeEntities = world.getNonSpectatingEntities(StickyDiceCubeEntity.class, box);
        int res = 0;
        for (DiceCubeEntity dice : diceCubeEntities) res += dice.getRandomPoint();
        for (StickyDiceCubeEntity dice : stickyDiceCubeEntities) res += dice.getRandomPoint();
        if (res > 15) res = 15;
        return res;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWER);
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.getWeakRedstonePower(world, pos, direction);
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(FACING) == direction ? this.indicator.value : 0;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite().getOpposite());
    }

    @Override
    protected MapCodec<DiceDetectorBlock> getCodec() {
        return CODEC;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        world.setBlockState(pos, state.with(POWER, dicePointCalculation(pos, state, world)), Block.NOTIFY_LISTENERS);
        this.scheduleTick(world, pos);
        this.indicator.updateValue(pos, state, world);
        this.updateNeighbors(world, pos, state);
    }

    @Override
    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        int i = trigger(world, state, hit, projectile);
        if (projectile.getOwner() instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.incrementStat(Stats.TARGET_HIT);
            Criteria.TARGET_HIT.trigger(serverPlayerEntity, projectile, hit.getPos(), i);
        }
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        if (entity instanceof DiceCubeEntity || entity instanceof StickyDiceCubeEntity){
            entity.addVelocity(0, 0.1f, 0);
        }
    }

    private static int trigger(WorldAccess world, BlockState state, BlockHitResult hitResult, Entity entity) {
        int i = dicePointCalculation(hitResult.getBlockPos(), state, (World) world);
        int j = entity instanceof PersistentProjectileEntity ? 20 : 8;
        if (!world.getBlockTickScheduler().isQueued(hitResult.getBlockPos(), state.getBlock())) {
            setPower(world, state, i, hitResult.getBlockPos(), j);
        }

        return i;
    }

    private static void setPower(WorldAccess world, BlockState state, int power, BlockPos pos, int delay) {
        world.setBlockState(pos, state.with(POWER, power), Block.NOTIFY_ALL);
        world.scheduleBlockTick(pos, state.getBlock(), delay);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient() && !state.isOf(oldState.getBlock())) {
            if (state.get(POWER) > 0 && !world.getBlockTickScheduler().isQueued(pos, this)) {
                world.setBlockState(pos, state.with(POWER, 0), Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
            }
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWER) != 0) {
            world.setBlockState(pos, state.with(POWER, 0), Block.NOTIFY_ALL);
        }
    }

    protected void updateNeighbors(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        world.updateNeighbor(blockPos, this, pos);
        world.updateNeighborsExcept(blockPos, this, direction);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private void scheduleTick(WorldAccess world, BlockPos pos) {
        if (!world.isClient() && !world.getBlockTickScheduler().isQueued(pos, this)) {
            world.scheduleBlockTick(pos, this, 2);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        super.appendTooltip(stack, context, tooltip, options);
        tooltip.add(Text.literal("WIP").formatted(Formatting.BOLD));
    }
}
