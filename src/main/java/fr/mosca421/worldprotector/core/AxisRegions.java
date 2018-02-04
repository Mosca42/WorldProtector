package fr.mosca421.worldprotector.core;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AxisRegions
{
    public final int minX;
    public final int minY;
    public final int minZ;
    public final int maxX;
    public final int maxY;
    public final int maxZ;

    public AxisRegions(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public AxisRegions(BlockPos pos)
    {
        this((int)pos.getX(), (int)pos.getY(), (int)pos.getZ(), (int)(pos.getX() + 1), (int)(pos.getY() + 1), (int)(pos.getZ() + 1));
    }

    public AxisRegions(BlockPos pos1, BlockPos pos2)
    {
        this((int)pos1.getX(), (int)pos1.getY(), (int)pos1.getZ(), (int)pos2.getX(), (int)pos2.getY(), (int)pos2.getZ());
    }

    @SideOnly(Side.CLIENT)
    public AxisRegions(Vec3d min, Vec3d max)
    {
        this((int)min.x, (int)min.y, (int)min.z, (int)max.x, (int)max.y, (int)max.z);
    }

    public AxisRegions setMaxY(int y2)
    {
        return new AxisRegions(this.minX, this.minY, this.minZ, this.maxX, y2, this.maxZ);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof AxisRegions))
        {
            return false;
        }
        else
        {
            AxisRegions axisregions = (AxisRegions)p_equals_1_;
            return Integer.compare(axisregions.minX, this.minX) != 0 ? false : (Integer.compare(axisregions.minY, this.minY) != 0 ? false : (Integer.compare(axisregions.minZ, this.minZ) != 0 ? false : (Integer.compare(axisregions.maxX, this.maxX) != 0 ? false : (Integer.compare(axisregions.maxY, this.maxY) != 0 ? false : Integer.compare(axisregions.maxZ, this.maxZ) == 0))));
        }
    }

    public int hashCode()
    {
        long i = Integer.toUnsignedLong(this.minX);
        int j = (int)(i ^ i >>> 32);
        i = Integer.toUnsignedLong(this.minY);
        j = 31 * j + (int)(i ^ i >>> 32);
        i = Integer.toUnsignedLong(this.minZ);
        j = 31 * j + (int)(i ^ i >>> 32);
        i = Integer.toUnsignedLong(this.maxX);
        j = 31 * j + (int)(i ^ i >>> 32);
        i = Integer.toUnsignedLong(this.maxY);
        j = 31 * j + (int)(i ^ i >>> 32);
        i = Integer.toUnsignedLong(this.maxZ);
        j = 31 * j + (int)(i ^ i >>> 32);
        return j;
    }

    public AxisRegions contract(int p_191195_1_, int p_191195_3_, int p_191195_5_)
    {
        int d0 = this.minX;
        int d1 = this.minY;
        int d2 = this.minZ;
        int d3 = this.maxX;
        int d4 = this.maxY;
        int d5 = this.maxZ;

        if (p_191195_1_ < 0.0D)
        {
            d0 -= p_191195_1_;
        }
        else if (p_191195_1_ > 0.0D)
        {
            d3 -= p_191195_1_;
        }

        if (p_191195_3_ < 0.0D)
        {
            d1 -= p_191195_3_;
        }
        else if (p_191195_3_ > 0.0D)
        {
            d4 -= p_191195_3_;
        }

        if (p_191195_5_ < 0.0D)
        {
            d2 -= p_191195_5_;
        }
        else if (p_191195_5_ > 0.0D)
        {
            d5 -= p_191195_5_;
        }

        return new AxisRegions(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Adds a coordinate to the bounding box, extending it if the point lies outside the current ranges.
     */
    public AxisRegions addCoord(int x, int y, int z)
    {
        int d0 = this.minX;
        int d1 = this.minY;
        int d2 = this.minZ;
        int d3 = this.maxX;
        int d4 = this.maxY;
        int d5 = this.maxZ;

        if (x < 0.0D)
        {
            d0 += x;
        }
        else if (x > 0.0D)
        {
            d3 += x;
        }

        if (y < 0.0D)
        {
            d1 += y;
        }
        else if (y > 0.0D)
        {
            d4 += y;
        }

        if (z < 0.0D)
        {
            d2 += z;
        }
        else if (z > 0.0D)
        {
            d5 += z;
        }

        return new AxisRegions(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Creates a new bounding box that has been expanded. If negative values are used, it will shrink.
     */
    public AxisRegions expand(int x, int y, int z)
    {
        int d0 = this.minX - x;
        int d1 = this.minY - y;
        int d2 = this.minZ - z;
        int d3 = this.maxX + x;
        int d4 = this.maxY + y;
        int d5 = this.maxZ + z;
        return new AxisRegions(d0, d1, d2, d3, d4, d5);
    }

    public AxisRegions expandXyz(int value)
    {
        return this.expand(value, value, value);
    }

    public AxisRegions intersect(AxisRegions p_191500_1_)
    {
        int d0 = Math.max(this.minX, p_191500_1_.minX);
        int d1 = Math.max(this.minY, p_191500_1_.minY);
        int d2 = Math.max(this.minZ, p_191500_1_.minZ);
        int d3 = Math.min(this.maxX, p_191500_1_.maxX);
        int d4 = Math.min(this.maxY, p_191500_1_.maxY);
        int d5 = Math.min(this.maxZ, p_191500_1_.maxZ);
        return new AxisRegions(d0, d1, d2, d3, d4, d5);
    }

    public AxisRegions union(AxisRegions other)
    {
        int d0 = Math.min(this.minX, other.minX);
        int d1 = Math.min(this.minY, other.minY);
        int d2 = Math.min(this.minZ, other.minZ);
        int d3 = Math.max(this.maxX, other.maxX);
        int d4 = Math.max(this.maxY, other.maxY);
        int d5 = Math.max(this.maxZ, other.maxZ);
        return new AxisRegions(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Offsets the current bounding box by the specified amount.
     */
    public AxisRegions offset(int x, int y, int z)
    {
        return new AxisRegions(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    public AxisRegions offset(BlockPos pos)
    {
        return new AxisRegions(this.minX + (int)pos.getX(), this.minY + (int)pos.getY(), this.minZ + (int)pos.getZ(), this.maxX + (int)pos.getX(), this.maxY + (int)pos.getY(), this.maxZ + (int)pos.getZ());
    }

    public AxisRegions move(Vec3d p_191194_1_)
    {
        return this.offset((int)p_191194_1_.x,(int) p_191194_1_.y, (int)p_191194_1_.z);
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and Z dimensions, calculate the offset between them
     * in the X dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public int calculateXOffset(AxisRegions other, int offsetX)
    {
        if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ)
        {
            if (offsetX > 0.0D && other.maxX <= this.minX)
            {
                int d1 = this.minX - other.maxX;

                if (d1 < offsetX)
                {
                    offsetX = d1;
                }
            }
            else if (offsetX < 0.0D && other.minX >= this.maxX)
            {
                int d0 = this.maxX - other.minX;

                if (d0 > offsetX)
                {
                    offsetX = d0;
                }
            }

            return offsetX;
        }
        else
        {
            return offsetX;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the X and Z dimensions, calculate the offset between them
     * in the Y dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public int calculateYOffset(AxisRegions other, int offsetY)
    {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ)
        {
            if (offsetY > 0.0D && other.maxY <= this.minY)
            {
                int d1 = this.minY - other.maxY;

                if (d1 < offsetY)
                {
                    offsetY = d1;
                }
            }
            else if (offsetY < 0.0D && other.minY >= this.maxY)
            {
                int d0 = this.maxY - other.minY;

                if (d0 > offsetY)
                {
                    offsetY = d0;
                }
            }

            return offsetY;
        }
        else
        {
            return offsetY;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and X dimensions, calculate the offset between them
     * in the Z dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public int calculateZOffset(AxisRegions other, int offsetZ)
    {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY)
        {
            if (offsetZ > 0.0D && other.maxZ <= this.minZ)
            {
                int d1 = this.minZ - other.maxZ;

                if (d1 < offsetZ)
                {
                    offsetZ = d1;
                }
            }
            else if (offsetZ < 0.0D && other.minZ >= this.maxZ)
            {
                int d0 = this.maxZ - other.minZ;

                if (d0 > offsetZ)
                {
                    offsetZ = d0;
                }
            }

            return offsetZ;
        }
        else
        {
            return offsetZ;
        }
    }

    /**
     * Checks if the bounding box intersects with another.
     */
    public boolean intersectsWith(AxisRegions other)
    {
        return this.intersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
    }

    public boolean intersects(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        return this.minX < x2 && this.maxX > x1 && this.minY < y2 && this.maxY > y1 && this.minZ < z2 && this.maxZ > z1;
    }

    @SideOnly(Side.CLIENT)
    public boolean intersects(Vec3d min, Vec3d max)
    {
        return this.intersects((int)Math.min(min.x, max.x), (int)Math.min(min.y, max.y), (int)Math.min(min.z, max.z), (int)Math.max(min.x, max.x), (int)Math.max(min.y, max.y), (int)Math.max(min.z, max.z));
    }

    /**
     * Returns if the supplied Vec3D is completely inside the bounding box
     */
    public boolean isVecInside(Vec3d vec)
    {
        return vec.x > this.minX && vec.x < this.maxX ? (vec.y > this.minY && vec.y < this.maxY ? vec.z > this.minZ && vec.z < this.maxZ : false) : false;
    }

    /**
     * Returns the average length of the edges of the bounding box.
     */
    public int getAverageEdgeLength()
    {
        int d0 = this.maxX - this.minX;
        int d1 = this.maxY - this.minY;
        int d2 = this.maxZ - this.minZ;
        return (int) ((d0 + d1 + d2) / 3.0D);
    }

    public AxisRegions contract(int value)
    {
        return this.expandXyz(-value);
    }

    @Nullable
    public RayTraceResult calculateIntercept(Vec3d vecA, Vec3d vecB)
    {
        Vec3d vec3d = this.collideWithXPlane(this.minX, vecA, vecB);
        EnumFacing enumfacing = EnumFacing.WEST;
        Vec3d vec3d1 = this.collideWithXPlane(this.maxX, vecA, vecB);

        if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.EAST;
        }

        vec3d1 = this.collideWithYPlane(this.minY, vecA, vecB);

        if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.DOWN;
        }

        vec3d1 = this.collideWithYPlane(this.maxY, vecA, vecB);

        if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.UP;
        }

        vec3d1 = this.collideWithZPlane(this.minZ, vecA, vecB);

        if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.NORTH;
        }

        vec3d1 = this.collideWithZPlane(this.maxZ, vecA, vecB);

        if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.SOUTH;
        }

        return vec3d == null ? null : new RayTraceResult(vec3d, enumfacing);
    }

    @VisibleForTesting
    boolean isClosest(Vec3d p_186661_1_, @Nullable Vec3d p_186661_2_, Vec3d p_186661_3_)
    {
        return p_186661_2_ == null || p_186661_1_.squareDistanceTo(p_186661_3_) < p_186661_1_.squareDistanceTo(p_186661_2_);
    }

    @Nullable
    @VisibleForTesting
    Vec3d collideWithXPlane(int p_186671_1_, Vec3d p_186671_3_, Vec3d p_186671_4_)
    {
        Vec3d vec3d = p_186671_3_.getIntermediateWithXValue(p_186671_4_, p_186671_1_);
        return vec3d != null && this.intersectsWithYZ(vec3d) ? vec3d : null;
    }

    @Nullable
    @VisibleForTesting
    Vec3d collideWithYPlane(int p_186663_1_, Vec3d p_186663_3_, Vec3d p_186663_4_)
    {
        Vec3d vec3d = p_186663_3_.getIntermediateWithYValue(p_186663_4_, p_186663_1_);
        return vec3d != null && this.intersectsWithXZ(vec3d) ? vec3d : null;
    }

    @Nullable
    @VisibleForTesting
    Vec3d collideWithZPlane(int p_186665_1_, Vec3d p_186665_3_, Vec3d p_186665_4_)
    {
        Vec3d vec3d = p_186665_3_.getIntermediateWithZValue(p_186665_4_, p_186665_1_);
        return vec3d != null && this.intersectsWithXY(vec3d) ? vec3d : null;
    }

    @VisibleForTesting
    public boolean intersectsWithYZ(Vec3d vec)
    {
        return vec.y >= this.minY && vec.y <= this.maxY && vec.z >= this.minZ && vec.z <= this.maxZ;
    }

    @VisibleForTesting
    public boolean intersectsWithXZ(Vec3d vec)
    {
        return vec.x >= this.minX && vec.x <= this.maxX && vec.z >= this.minZ && vec.z <= this.maxZ;
    }

    @VisibleForTesting
    public boolean intersectsWithXY(Vec3d vec)
    {
        return vec.x >= this.minX && vec.x <= this.maxX && vec.y >= this.minY && vec.y <= this.maxY;
    }

    public String toString()
    {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }

    @SideOnly(Side.CLIENT)
    public Vec3d getCenter()
    {
        return new Vec3d(this.minX + (this.maxX - this.minX) * 0.5D, this.minY + (this.maxY - this.minY) * 0.5D, this.minZ + (this.maxZ - this.minZ) * 0.5D);
    }
}