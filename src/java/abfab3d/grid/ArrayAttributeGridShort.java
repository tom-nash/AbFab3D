/*****************************************************************************
 *                        Shapeways, Inc Copyright (c) 2011
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/

package abfab3d.grid;

// External Imports

/**
 * A grid backed by arrays.
 *
 * Likely better performance for memory access that is not slice aligned.
 *
 * Uses the X3D coordinate system.  Y-up.  Grid is located
 * on positive right side octant.
 *
 * @author Alan Hudson
 */
public class ArrayAttributeGridShort extends BaseAttributeGrid {
    protected short[] data;

    /**
     * Constructor.
     *
     * @param w The width in world coords
     * @param h The height in world coords
     * @param d The depth in world coords
     * @param pixel The size of the pixels
     * @param sheight The slice height in meters
     */
    public ArrayAttributeGridShort(double w, double h, double d, double pixel, double sheight) {
        this((int) (Math.ceil(w / pixel)) + 1,
        	 (int) (Math.ceil(h / sheight)) + 1,
             (int) (Math.ceil(d / pixel)) + 1, 
             pixel,
             sheight);
    }

    /**
     * Constructor.
     *
     * @param w The number of voxels in width
     * @param h The number of voxels in height
     * @param d The number of voxels in depth
     * @param pixel The size of the pixels
     * @param sheight The slice height in meters
     */
    public ArrayAttributeGridShort(int w, int h, int d, double pixel, double sheight) {
        super(w,h,d,pixel,sheight);

        data = new short[height * width * depth];
    }

    /**
     * Copy Constructor.
     *
     * @param grid The grid
     */
    public ArrayAttributeGridShort(ArrayAttributeGridShort grid) {
        super(grid.getWidth(), grid.getHeight(), grid.getDepth(),
            grid.getVoxelSize(), grid.getSliceHeight());
        this.data = grid.data.clone();
    }

    /**
     * Create an empty grid of the specified size.  Reuses
     * the grid type and material type(byte, short, int).
     *
     * @param w The number of voxels in width
     * @param h The number of voxels in height
     * @param d The number of voxels in depth
     * @param pixel The size of the pixels
     * @param sheight The slice height in meters
     */
    public Grid createEmpty(int w, int h, int d, double pixel, double sheight) {
        Grid ret_val = new ArrayAttributeGridShort(w,h,d,pixel,sheight);

        return ret_val;
    }

    /**
     * Get the data of the voxel
     *
     * @param x The x grid coordinate
     * @param y The y grid coordinate
     * @param z The z grid coordinate
     */
    public VoxelData getData(int x, int y, int z) {
        int idx = y * sliceSize + x * depth + z;

        byte state = (byte) ((data[idx] & 0xFFFF) >> 14);
        short mat = (short) (0x3FFF & data[idx]);

        VoxelDataShort vd = new VoxelDataShort(state, mat);

        return vd;
    }

    /**
     * Get the data of the voxel
     *
     * @param x The x world coordinate
     * @param y The y world coordinate
     * @param z The z world coordinate
     */
    public VoxelData getData(double x, double y, double z) {
        int slice = (int) (y / sheight);
        int s_x = (int) (x / pixelSize);
        int s_z = (int) (z / pixelSize);

        int idx = slice * sliceSize + s_x * depth + s_z;

        byte state = (byte) ((data[idx] & 0xFFFF) >> 14);
        short mat = (short) (0x3FFF & data[idx]);

        return new VoxelDataShort(state, mat);
    }

    /**
     * Get the state of the voxel
     *
     * @param x The x world coordinate
     * @param y The y world coordinate
     * @param z The z world coordinate
     */
    public byte getState(double x, double y, double z) {
        int slice = (int) (y / sheight);
        int s_x = (int) (x / pixelSize);
        int s_z = (int) (z / pixelSize);

        int idx = slice * sliceSize + s_x * depth + s_z;

        byte state = (byte) ((data[idx] & 0xFFFF) >> 14);

        return state;
    }

    /**
     * Get the state of the voxel.
     *
     * @param x The x world coordinate
     * @param y The y world coordinate
     * @param z The z world coordinate
     */
    public byte getState(int x, int y, int z) {
        int idx = y * sliceSize + x * depth + z;

        byte state = (byte) ((data[idx] & 0xFFFF) >> 14);

        return state;
    }

    /**
     * Get the state of the voxel
     *
     * @param x The x world coordinate
     * @param y The y world coordinate
     * @param z The z world coordinate
     */
    public int getAttribute(double x, double y, double z) {
        int slice = (int) (y / sheight);
        int s_x = (int) (x / pixelSize);
        int s_z = (int) (z / pixelSize);

        int idx = slice * sliceSize + s_x * depth + s_z;

        short mat = (short) (0x3FFF & data[idx]);

        return mat;
    }

    /**
     * Get the material of the voxel.
     *
     * @param x The x world coordinate
     * @param y The y world coordinate
     * @param z The z world coordinate
     */
    public int getAttribute(int x, int y, int z) {
        int idx = y * sliceSize + x * depth + z;

        short mat = (short) (0x3FFF & data[idx]);

        return mat;
    }

    /**
     * Set the value of a voxel.
     *
     * @param x The x world coordinate
     * @param y The y world coordinate
     * @param z The z world coordinate
     * @param state The voxel state
     * @param material The material
     */
    public void setData(double x, double y, double z, byte state, int material) {
        int slice = (int) (y / sheight);
        int s_x = (int) (x / pixelSize);
        int s_z = (int) (z / pixelSize);

        int idx = slice * sliceSize + s_x * depth + s_z;

        data[idx] = (short) (0xFFFF & (((short)state) << 14 | ((short)material)));
    }

    /**
     * Set the value of a voxel.
     *
     * @param x The x grid coordinate
     * @param y The y grid coordinate
     * @param z The z grid coordinate
     * @param state The voxel state
     * @param material The material
     */
    public void setData(int x, int y, int z, byte state, int material) {
        int idx = y * sliceSize + x * depth + z;

        data[idx] = (short) (0xFFFF & (((short)state) << 14 | (short)material));
    }

   /**
     * Set the material value of a voxel.  Leaves the state unchanged.
     *
     * @param x The x world coordinate
     * @param y The y world coordinate
     * @param z The z world coordinate
     * @param material The materialID
     */
    public void setAttribute(int x, int y, int z, int material) {
        int idx = y * sliceSize + x * depth + z;

        byte state = (byte) ((data[idx] & 0xFFFF) >> 14);

        data[idx] = (short) (0xFFFF & (((short)state) << 14 | (short)material));
    }

    /**
     * Set the state value of a voxel.  Leaves the material unchanged.
     *
     * @param x The x world coordinate
     * @param y The y world coordinate
     * @param z The z world coordinate
     * @param state The value.  0 = nothing. > 0 materialID
     */
    public void setState(int x, int y, int z, byte state) {
        int idx = y * sliceSize + x * depth + z;

        short mat = (short) (0x3FFF & data[idx]);

        data[idx] = (short) (0xFFFF & (((short)state) << 14 | (short)mat));
    }

    /**
     * Set the state value of a voxel.  Leaves the material unchanged.
     *
     * @param x The x world coordinate
     * @param y The y world coordinate
     * @param z The z world coordinate
     * @param state The value.  0 = nothing. > 0 materialID
     */
    public void setState(double x, double y, double z, byte state) {
        int slice = (int) (y / sheight);
        int s_x = (int) (x / pixelSize);
        int s_z = (int) (z / pixelSize);

        int idx = slice * sliceSize + s_x * depth + s_z;

        short mat = (short) (0x3FFF & data[idx]);

        data[idx] = (short) (0xFFFF & (((short)state) << 14 | (short)mat));
    }

    /**
     * Clone the object.
     */
    public Object clone() {
        ArrayAttributeGridShort ret_val = new ArrayAttributeGridShort(this);

        return ret_val;
    }
}
