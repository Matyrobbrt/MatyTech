/**
 * This file is part of the MatyTech Minecraft (Java Edition) mod and is
 * licensed under the MIT license:
 *
 * MIT License
 *
 * Copyright (c) 2021 Matyrobbrt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.matyrobbrt.matytech.api.util;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

import net.minecraftforge.common.util.INBTSerializable;

public class Pos3D extends Vector3d implements INBTSerializable<CompoundNBT> {

	public Pos3D() {
		this(0, 0, 0);
	}

	public Pos3D(Vector3d vec) {
		super(vec.x, vec.y, vec.z);
	}

	public Pos3D(double x, double y, double z) {
		super(x, y, z);
	}

	/**
	 * Creates a Pos3D with an entity's posX, posY, and posZ values.
	 *
	 * @param entity - entity to create the Pos3D from
	 */
	public Pos3D(Entity entity) {
		this(entity.getX(), entity.getY(), entity.getZ());
	}

	public static Pos3D create(TileEntity tile) {
		return create(tile.getBlockPos());
	}

	public static Pos3D create(Vector3i vec) {
		return new Pos3D(Vector3d.atLowerCornerOf(vec));
	}

	public static Pos3D fromNBT(CompoundNBT tag) {
		return new Pos3D(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
	}

	public static Pos3D translateMatrix(double[] matrix, Pos3D translation) {
		double x = translation.x * matrix[0] + translation.y * matrix[1] + translation.z * matrix[2] + matrix[3];
		double y = translation.x * matrix[4] + translation.y * matrix[5] + translation.z * matrix[6] + matrix[7];
		double z = translation.x * matrix[8] + translation.y * matrix[9] + translation.z * matrix[10] + matrix[11];
		return new Pos3D(x, y, z);
	}

	public static AxisAlignedBB getAABB(Vector3d pos1, Vector3d pos2) {
		return new AxisAlignedBB(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
	}

	/**
	 * Writes this Pos3D's data to an CompoundNBT.
	 *
	 * @param nbtTags - tag compound to write to
	 *
	 * @return the tag compound with this Pos3D's data
	 */
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putDouble("x", x);
		nbt.putDouble("y", y);
		nbt.putDouble("z", z);
		return nbt;
	}

	/**
	 * We cannot reassign the fields, use {@link #fromNBT(CompoundNBT)}
	 *
	 * @param nbt
	 */
	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		// Cannot assign
	}

	/**
	 * Creates and returns a Pos3D with values representing the difference between
	 * this and the Pos3D in the parameters.
	 *
	 * @param vec - Vec3 to subtract
	 *
	 * @return difference of the two Pos3Ds
	 */
	public Pos3D diff(Vector3d vec) {
		return new Pos3D(x - vec.x, y - vec.y, z - vec.z);
	}

	/**
	 * Centres a block-derived Pos3D
	 */
	public Pos3D centre() {
		return translate(0.5, 0.5, 0.5);
	}

	/**
	 * Translates this Pos3D by the defined values.
	 *
	 * @param x - amount to translate on the x-axis
	 * @param y - amount to translate on the y-axis
	 * @param z - amount to translate on the z-axis
	 *
	 * @return the translated Pos3D
	 */
	public Pos3D translate(double x, double y, double z) {
		return new Pos3D(this.x + x, this.y + y, this.z + z);
	}

	/**
	 * Performs the same operation as translate(x, y, z), but with a Pos3D value
	 * instead.
	 *
	 * @param pos - Pos3D value to translate by
	 *
	 * @return translated Pos3D
	 */
	public Pos3D translate(Vector3d pos) {
		return translate(pos.x, pos.y, pos.z);
	}

	public Pos3D translate(Vector3d... positions) {
		double x = this.x;
		double y = this.y;
		double z = this.z;
		for (Vector3d position : positions) {
			x += position.x;
			y += position.y;
			z += position.z;
		}
		return new Pos3D(x, y, z);
	}

	public Pos3D translate(Direction direction, double amount) {
		return translate(direction.getNormal().getX() * amount, direction.getNormal().getY() * amount,
				direction.getNormal().getZ() * amount);
	}

	public Pos3D translateExcludingSide(Direction direction, double amount) {
		double xPos = x;
		double yPos = y;
		double zPos = z;
		if (direction.getAxis() != Axis.X) {
			xPos += amount;
		}
		if (direction.getAxis() != Axis.Y) {
			yPos += amount;
		}
		if (direction.getAxis() != Axis.Z) {
			zPos += amount;
		}
		return new Pos3D(xPos, yPos, zPos);
	}

	public Pos3D adjustPosition(Direction direction, Entity entity) {
		if (direction.getAxis() == Axis.X) {
			return new Pos3D(entity.getX(), y, z);
		} else if (direction.getAxis() == Axis.Y) { return new Pos3D(x, entity.getY(), z); } // Axis.Z
		return new Pos3D(x, y, entity.getZ());
	}

	/**
	 * Returns the distance between this and the defined Pos3D.
	 *
	 * @param pos - the Pos3D to find the distance to
	 *
	 * @return the distance between this and the defined Pos3D
	 */
	public double distance(Vector3d pos) {
		double subX = x - pos.x;
		double subY = y - pos.y;
		double subZ = z - pos.z;
		return MathHelper.sqrt(subX * subX + subY * subY + subZ * subZ);
	}

	@Nonnull
	@Override
	public Pos3D yRot(float yaw) {
		double yawRadians = Math.toRadians(yaw);
		double xPos = x;
		double zPos = z;
		if (yaw != 0) {
			double cos = Math.cos(yawRadians);
			double sin = Math.sin(yawRadians);
			xPos = x * cos - z * sin;
			zPos = z * cos + x * sin;
		}
		return new Pos3D(xPos, y, zPos);
	}

	@Nonnull
	@Override
	public Pos3D xRot(float pitch) {
		double pitchRadians = Math.toRadians(pitch);
		double yPos = y;
		double zPos = z;
		if (pitch != 0) {
			double cos = Math.cos(pitchRadians);
			double sin = Math.sin(pitchRadians);
			yPos = y * cos - z * sin;
			zPos = z * cos + y * sin;
		}
		return new Pos3D(x, yPos, zPos);
	}

	public Pos3D rotate(float yaw, float pitch) {
		return rotate(yaw, pitch, 0);
	}

	public Pos3D rotate(float yaw, float pitch, float roll) {
		double yawRadians = Math.toRadians(yaw);
		double pitchRadians = Math.toRadians(pitch);
		double rollRadians = Math.toRadians(roll);

		double xPos = x * Math.cos(yawRadians) * Math.cos(pitchRadians)
				+ z * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians)
						- Math.sin(yawRadians) * Math.cos(rollRadians))
				+ y * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians)
						+ Math.sin(yawRadians) * Math.sin(rollRadians));
		double zPos = x * Math.sin(yawRadians) * Math.cos(pitchRadians)
				+ z * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians)
						+ Math.cos(yawRadians) * Math.cos(rollRadians))
				+ y * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians)
						- Math.cos(yawRadians) * Math.sin(rollRadians));
		double yPos = -x * Math.sin(pitchRadians) + z * Math.cos(pitchRadians) * Math.sin(rollRadians)
				+ y * Math.cos(pitchRadians) * Math.cos(rollRadians);
		return new Pos3D(xPos, yPos, zPos);
	}

	@Nonnull
	@Override
	public Pos3D multiply(Vector3d pos) {
		return multiply(pos.x, pos.y, pos.z);
	}

	/**
	 * Scales this Pos3D by the defined x, y, and z values.
	 *
	 * @param x - x value to scale by
	 * @param y - y value to scale by
	 * @param z - z value to scale by
	 *
	 * @return scaled Pos3D
	 */
	@Nonnull
	@Override
	public Pos3D multiply(double x, double y, double z) {
		return new Pos3D(this.x * x, this.y * y, this.z * z);
	}

	@Nonnull
	@Override
	public Pos3D scale(double scale) {
		return multiply(scale, scale, scale);
	}

	public Pos3D floor() {
		return new Pos3D(Math.floor(x), Math.floor(y), Math.floor(z));
	}

	@Override
	public Pos3D clone() {
		return new Pos3D(x, y, z);
	}

	@Nonnull
	@Override
	public String toString() {
		return "[Pos3D: " + x + ", " + y + ", " + z + "]";
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Vector3d && ((Vector3d) obj).x == x && ((Vector3d) obj).y == y && ((Vector3d) obj).z == z;
	}

	@Override
	public int hashCode() {
		int code = 1;
		code = 31 * code + Double.hashCode(x);
		code = 31 * code + Double.hashCode(y);
		code = 31 * code + Double.hashCode(z);
		return code;
	}
}
