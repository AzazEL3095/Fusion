/**
 * 
 */
package alexndr.api.content.tiles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alexndr.plugins.Fusion.blocks.SimpleFurnace;

/**
 * Inheritable re-write of TileEntityFurnace, for use with SimpleCore machines.
 * @author Sinhika
 *
 */
public class TileEntitySimpleFurnace extends TileEntityLockable implements
		ITickable, ISidedInventory 
{
    protected static int[] slotsTop = new int[] {0};  // input
    protected static int[] slotsBottom = new int[] {2, 1};  // output, fuel
    protected static int[] slotsSides = new int[] {1};  // fuel
    
    /** The ItemStacks that hold the items currently being used in the furnace */
    protected ItemStack[] furnaceItemStacks;
    
    /** The number of ticks that the furnace will keep burning */
    protected int furnaceBurnTime;
    
    /** The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for */
    protected int currentItemBurnTime;
    protected int cookTime;
    protected int totalCookTime;
    protected int maxCookTime;
    protected String furnaceCustomName;
    protected String furnaceName;
    protected String furnaceGuiId;
    
    protected Class<? extends SimpleFurnace> furnaceBlockClass;
    
	/**
	 * 
	 */
	public TileEntitySimpleFurnace(String tileName, Class<? extends SimpleFurnace> fclass, int max_cook_time,
								   String guiID, int furnace_stack_count) 
	{
		this.furnaceName = tileName;
		this.furnaceBlockClass = fclass;
		this.maxCookTime = max_cook_time;
		this.furnaceGuiId = guiID;
		this.furnaceItemStacks = new ItemStack[furnace_stack_count];
	}

	/*------- MUCH OF THE FOLLOWING IS CUT & PASTED FROM TileEntityFurnace -------------*/
	
	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getSizeInventory()
	 */
	@Override
	public int getSizeInventory() {
		return this.furnaceItemStacks.length;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getStackInSlot(int)
	 */
	@Override
	public ItemStack getStackInSlot(int index) {
        return this.furnaceItemStacks[index];
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#decrStackSize(int, int)
	 */
	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (this.furnaceItemStacks[index] != null) {
			if (this.furnaceItemStacks[index].stackSize <= count) {
				ItemStack itemstack1 = this.furnaceItemStacks[index];
				this.furnaceItemStacks[index] = null;
				return itemstack1;
			} 
			else {
				ItemStack itemstack = this.furnaceItemStacks[index].splitStack(count);

				if (this.furnaceItemStacks[index].stackSize == 0) {
					this.furnaceItemStacks[index] = null;
				}

				return itemstack;
			}
		} 
		else {
			return null;
		}
	} // end decrStackSize()

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#removeStackFromSlot(int)
	 */
	@Override
	public ItemStack removeStackFromSlot(int index) {
        if (this.furnaceItemStacks[index] != null)
        {
            ItemStack itemstack = this.furnaceItemStacks[index];
            this.furnaceItemStacks[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#setInventorySlotContents(int, net.minecraft.item.ItemStack)
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) 
	{
        boolean flag = stack != null && stack.isItemEqual(this.furnaceItemStacks[index]) 
        				&& ItemStack.areItemStackTagsEqual(stack, this.furnaceItemStacks[index]);
        this.furnaceItemStacks[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        if (index == 0 && !flag)
        {
            this.totalCookTime = this.getCookTime(stack);
            this.cookTime = 0;
            this.markDirty();
        }
	} // end ()

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getInventoryStackLimit()
	 */
	@Override
	public int getInventoryStackLimit() {
        return 64;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#isUseableByPlayer(net.minecraft.entity.player.EntityPlayer)
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#isItemValidForSlot(int, net.minecraft.item.ItemStack)
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		switch(index) {
		case 1:
			return isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack);
		case 2:
			return false;
		default:
			return true;
		}
	} // end ()

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getField(int)
	 */
	@Override
	public int getField(int id) {
        switch (id)
        {
            case 0:
                return this.furnaceBurnTime;
            case 1:
                return this.currentItemBurnTime;
            case 2:
                return this.cookTime;
            case 3:
                return this.totalCookTime;
            case 4:
            	return this.maxCookTime;
            default:
                return 0;
        }
	} // end ()

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#setField(int, int)
	 */
	@Override
	public void setField(int id, int value) {
        switch (id)
        {
            case 0:
                this.furnaceBurnTime = value;
                break;
            case 1:
                this.currentItemBurnTime = value;
                break;
            case 2:
                this.cookTime = value;
                break;
            case 3:
                this.totalCookTime = value;
                break;
            case 4:
            	this.maxCookTime = value;
        }
	} // end ()

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getFieldCount()
	 */
	@Override
	public int getFieldCount() {
        return 5;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#clear()
	 */
	@Override
	public void clear() {
        for (int i = 0; i < this.furnaceItemStacks.length; ++i)
        {
            this.furnaceItemStacks[i] = null;
        }
	} // end clear()

	/* (non-Javadoc)
	 * @see net.minecraft.world.IWorldNameable#getName()
	 */
	@Override
	public String getName() {
	       return this.hasCustomName() ? this.furnaceCustomName : this.furnaceName;	
	}

	/* (non-Javadoc)
	 * @see net.minecraft.world.IWorldNameable#hasCustomName()
	 */
	@Override
	public boolean hasCustomName() {
        return this.furnaceCustomName != null && this.furnaceCustomName.length() > 0;
	}
	
    public void setCustomInventoryName(String p_145951_1_)
    {
        this.furnaceCustomName = p_145951_1_;
    }

	/* (non-Javadoc)
	 * @see net.minecraft.world.IInteractionObject#createContainer(net.minecraft.entity.player.InventoryPlayer, net.minecraft.entity.player.EntityPlayer)
	 * Override this for custom classes.
	 */
	@Override
	public Container createContainer(InventoryPlayer playerInventory,
			EntityPlayer playerIn) 
	{
        return new ContainerFurnace(playerInventory, this);
	}

	/* (non-Javadoc)
	 * @see net.minecraft.world.IInteractionObject#getGuiID()
	 */
	@Override
	public String getGuiID() {
		return furnaceGuiId;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.ISidedInventory#getSlotsForFace(net.minecraft.util.EnumFacing)
	 */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? slotsBottom : (side == EnumFacing.UP ? slotsTop : slotsSides);
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.ISidedInventory#canInsertItem(int, net.minecraft.item.ItemStack, net.minecraft.util.EnumFacing)
	 */
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn,
			EnumFacing direction) 
	{
        return this.isItemValidForSlot(index, itemStackIn);
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.ISidedInventory#canExtractItem(int, net.minecraft.item.ItemStack, net.minecraft.util.EnumFacing)
	 */
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) 
	{
        if (direction == EnumFacing.DOWN && index == 1)
        {
            Item item = stack.getItem();

            // TODO rewrite this to work with SimpleBucket items, or Universal buckets.
            if (item != Items.WATER_BUCKET && item != Items.BUCKET)
            {
                return false;
            }
        }

        return true;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.util.ITickable#update()
	 */
	@Override
	public void update() 
	{
        boolean flag = this.isBurning();
        boolean flag1 = false;

        if (this.isBurning())
        {
            --this.furnaceBurnTime;
        }

        if (!this.worldObj.isRemote)
        {
            if (this.isBurning() || this.furnaceItemStacks[1] != null && this.furnaceItemStacks[0] != null)
            {
                if (!this.isBurning() && this.canSmelt())
                {
                    this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);

                    if (this.isBurning())
                    {
                        flag1 = true;

                        if (this.furnaceItemStacks[1] != null)
                        {
                            --this.furnaceItemStacks[1].stackSize;

                            if (this.furnaceItemStacks[1].stackSize == 0)
                            {
                                this.furnaceItemStacks[1] = furnaceItemStacks[1].getItem().getContainerItem(furnaceItemStacks[1]);
                            }
                        }
                    }
                }

                if (this.isBurning() && this.canSmelt())
                {
                    ++this.cookTime;

                    if (this.cookTime == this.totalCookTime)
                    {
                        this.cookTime = 0;
                        this.totalCookTime = this.getCookTime(this.furnaceItemStacks[0]);
                        this.smeltItem();
                        flag1 = true;
                    }
                }
                else
                {
                    this.cookTime = 0;
                }
            }
            else if (!this.isBurning() && this.cookTime > 0)
            {
                this.cookTime = MathHelper.clamp_int(this.cookTime - 2, 0, this.totalCookTime);
            }

            if (flag != this.isBurning())
            {
                flag1 = true;
                Method m;
				try {
					m = furnaceBlockClass.getDeclaredMethod("setState", boolean.class, 
																	World.class, BlockPos.class);
					m.invoke(null, this.isBurning(), this.worldObj, this.pos);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
            } // end-if
        } // end-if

        if (flag1)
        {
            this.markDirty();
        }
	} // end update()
	
	@Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Items", 10);
        this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot");

            if (j >= 0 && j < this.furnaceItemStacks.length)
            {
                this.furnaceItemStacks[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
        }

        this.furnaceBurnTime = compound.getShort("BurnTime");
        this.cookTime = compound.getShort("CookTime");
        this.totalCookTime = compound.getShort("CookTimeTotal");
        this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);

        if (compound.hasKey("CustomName", 8))
        {
            this.furnaceCustomName = compound.getString("CustomName");
        }
    } // end readFromNBT()

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setShort("BurnTime", (short)this.furnaceBurnTime);
        compound.setShort("CookTime", (short)this.cookTime);
        compound.setShort("CookTimeTotal", (short)this.totalCookTime);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.furnaceItemStacks.length; ++i)
        {
            if (this.furnaceItemStacks[i] != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                this.furnaceItemStacks[i].writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        compound.setTag("Items", nbttaglist);

        if (this.hasCustomName())
        {
            compound.setString("CustomName", this.furnaceCustomName);
        }
        
        return compound;
    } // end writeToNBT()

    /**
     * Furnace isBurning
     */
    public boolean isBurning()
    {
        return this.furnaceBurnTime > 0;
    }

    @SideOnly(Side.CLIENT)
    public static boolean isBurning(IInventory p_174903_0_)
    {
        return p_174903_0_.getField(0) > 0;
    }

    // override this as necessary in custom furnace classes.
    public int getCookTime(ItemStack stack)
    {
        return maxCookTime;
    }

    /**
     * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, 
     * etc. Obviously will have to be overriden for special multi-slot furnaces like the Fusion Furnace.
     */
    protected boolean canSmelt()
    {
        if (this.furnaceItemStacks[0] == null)
        {
            return false;
        }
        else
        {
            ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItemStacks[0]);
            if (itemstack == null) return false;
            if (this.furnaceItemStacks[2] == null) return true;
            if (!this.furnaceItemStacks[2].isItemEqual(itemstack)) return false;
            int result = furnaceItemStacks[2].stackSize + itemstack.stackSize;
            return result <= getInventoryStackLimit() && result <= this.furnaceItemStacks[2].getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
        }
    } // end canSmelt()

    /**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result 
     * stack. Override for special multi-slot furnaces like the Fusion Furnace.
     */
    public void smeltItem()
    {
        if (this.canSmelt())
        {
            ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItemStacks[0]);

            if (this.furnaceItemStacks[2] == null)
            {
                this.furnaceItemStacks[2] = itemstack.copy();
            }
            else if (this.furnaceItemStacks[2].getItem() == itemstack.getItem())
            {
                this.furnaceItemStacks[2].stackSize += itemstack.stackSize; // Forge BugFix: Results may have multiple items
            }
            // TODO rewrite to work with SimpleBucket
            if (this.furnaceItemStacks[0].getItem() == Item.getItemFromBlock(Blocks.SPONGE) 
            		&& this.furnaceItemStacks[0].getMetadata() == 1 
            		&& this.furnaceItemStacks[1] != null 
            		&& this.furnaceItemStacks[1].getItem() == Items.BUCKET)
            {
                this.furnaceItemStacks[1] = new ItemStack(Items.WATER_BUCKET);
            }

            --this.furnaceItemStacks[0].stackSize;

            if (this.furnaceItemStacks[0].stackSize <= 0)
            {
                this.furnaceItemStacks[0] = null;
            }
        }
    } // end smeltItem()

    /**
     * Returns the number of ticks that the supplied fuel item will keep the furnace burning, or 0 
     * if the item isn't fuel.
     */
    public static int getItemBurnTime(ItemStack burnItem)
    {
        if (burnItem == null)
        {
            return 0;
        }
        else
        {
            Item item = burnItem.getItem();

            if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.AIR)
            {
                Block block = Block.getBlockFromItem(item);

                if (block == Blocks.WOODEN_SLAB)
                {
                    return 150;
                }

                if (block.getDefaultState().getMaterial() == Material.WOOD)
                {
                    return 300;
                }

                if (block == Blocks.COAL_BLOCK)
                {
                    return 16000;
                }
            }

            if (item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword)item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe)item).getMaterialName().equals("WOOD")) return 200;
            if (item == Items.STICK) return 100;
            if (item == Items.COAL) return 1600;
            if (item == Items.LAVA_BUCKET) return 20000;
            if (item == Item.getItemFromBlock(Blocks.SAPLING)) return 100;
            if (item == Items.BLAZE_ROD) return 2400;
            return net.minecraftforge.fml.common.registry.GameRegistry.getFuelValue(burnItem);
        }
    } // end getItemBurnTime()

    public static boolean isItemFuel(ItemStack p_145954_0_)
    {
        /**
         * Returns the number of ticks that the supplied fuel item will keep the furnace burning, or 0 if the item isn't
         * fuel
         */
        return getItemBurnTime(p_145954_0_) > 0;
    }

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

    net.minecraftforge.items.IItemHandler handlerTop = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.UP);
    net.minecraftforge.items.IItemHandler handlerBottom = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.DOWN);
    net.minecraftforge.items.IItemHandler handlerSide = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.WEST);

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            if (facing == EnumFacing.DOWN)
                return (T) handlerBottom;
            else if (facing == EnumFacing.UP)
                return (T) handlerTop;
            else
                return (T) handlerSide;
        return super.getCapability(capability, facing);
    }
    
} // end class
