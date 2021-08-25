/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.bukkit.reflection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Reflection {

	public static Class<?> getNMSClass(String nmsClassString) {
		try {
			String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
			String name = "net.minecraft.server." + version + nmsClassString;
			Class<?> nmsClass = Class.forName(name);
			return nmsClass;
		} catch (ClassNotFoundException ex) {
		}
		return null;
	}

	public static Class<?> getOBCClass(String nmsClassString) {
		try {
			String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
			String name = "org.bukkit.craftbukkit." + version + nmsClassString;
			Class<?> nmsClass = Class.forName(name);
			return nmsClass;
		} catch (ClassNotFoundException ex) {}
		return null;
	}

	
	public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
		Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
		for (Constructor<?> constructor : clazz.getConstructors()) {
			if (!DataType.compare(DataType.getPrimitive(constructor.getParameterTypes()), primitiveTypes)) {
				continue;
			}
			return constructor;
		}
		throw new NoSuchMethodException("There is no such constructor in this class with the specified parameter types");
	}

	public static Constructor<?> getConstructor(String className, PackageType packageType, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
		return getConstructor(packageType.getClass(className), parameterTypes);
	}

	public static <T extends Enum<T>> T getEnumValue(final String value, final Class<T> enumClass) {
		return Enum.valueOf(enumClass, value);
	}

	public static Method getMethod(String className, PackageType packageType, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
		return getMethod(packageType.getClass(className), methodName, parameterTypes);
	}
	
	public static void setDeclaredField(Object obj, String field, Object value) {
		try {
			Field f = obj.getClass().getField(field);
			f.setAccessible(true);
			f.set(null, value);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1).replace("_", ".");
	}

	public static Object getHandle(Object obj) {
		try {
			return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
		for (Method m : clazz.getMethods()) {
			if (m.getName().equals(name) && (args.length == 0 || ClassListEqual(args, m.getParameterTypes()))) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}
	
	public static Field getField(Class<?> clazz, boolean declared, String fieldName) throws NoSuchFieldException, SecurityException {
		Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
		field.setAccessible(true);
		return field;
	}

	/**
	 * Returns a field of a desired class with the given name
	 * 
	 * @param className Name of the desired target class
	 * @param packageType Package where the desired target class is located
	 * @param declared Whether the desired field is declared or not
	 * @param fieldName Name of the desired field
	 * @return The field of the desired target class with the specified name
	 * @throws NoSuchFieldException If the desired field of the desired class cannot be found
	 * @throws SecurityException If the desired field cannot be made accessible
	 * @throws ClassNotFoundException If the desired target class with the specified name and package cannot be found
	 * @see #getField(Class, boolean, String)
	 */
	public static Field getField(String className, PackageType packageType, boolean declared, String fieldName) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
		return getField(packageType.getClass(className), declared, fieldName);
	}

	public static Object getValue(Object instance, Class<?> clazz, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		return getField(clazz, declared, fieldName).get(instance);
	}

	public static Object getValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
		return getValue(instance, packageType.getClass(className), declared, fieldName);
	}

	public static Object getValue(Object instance, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		return getValue(instance, instance.getClass(), declared, fieldName);
	}

	public static void setValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		getField(clazz, declared, fieldName).set(instance, value);
	}

	public static void setValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
		setValue(instance, packageType.getClass(className), declared, fieldName, value);
	}

	public static void setValue(Object instance, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		setValue(instance, instance.getClass(), declared, fieldName, value);
	}

	public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
		boolean equal = true;
		if (l1.length != l2.length) return false;
		for (int i = 0; i < l1.length; i++) {
			if (l1[i] != l2[i]) {
				equal = false;
				break;
			}
		}
		return equal;
	}
	
	public enum PackageType {
		MINECRAFT_SERVER("net.minecraft.server." + getServerVersion()),
		CRAFTBUKKIT("org.bukkit.craftbukkit." + getServerVersion()),
		CRAFTBUKKIT_BLOCK(CRAFTBUKKIT, "block"),
		CRAFTBUKKIT_CHUNKIO(CRAFTBUKKIT, "chunkio"),
		CRAFTBUKKIT_COMMAND(CRAFTBUKKIT, "command"),
		CRAFTBUKKIT_CONVERSATIONS(CRAFTBUKKIT, "conversations"),
		CRAFTBUKKIT_ENCHANTMENS(CRAFTBUKKIT, "enchantments"),
		CRAFTBUKKIT_ENTITY(CRAFTBUKKIT, "entity"),
		CRAFTBUKKIT_EVENT(CRAFTBUKKIT, "event"),
		CRAFTBUKKIT_GENERATOR(CRAFTBUKKIT, "generator"),
		CRAFTBUKKIT_HELP(CRAFTBUKKIT, "help"),
		CRAFTBUKKIT_INVENTORY(CRAFTBUKKIT, "inventory"),
		CRAFTBUKKIT_MAP(CRAFTBUKKIT, "map"),
		CRAFTBUKKIT_METADATA(CRAFTBUKKIT, "metadata"),
		CRAFTBUKKIT_POTION(CRAFTBUKKIT, "potion"),
		CRAFTBUKKIT_PROJECTILES(CRAFTBUKKIT, "projectiles"),
		CRAFTBUKKIT_SCHEDULER(CRAFTBUKKIT, "scheduler"),
		CRAFTBUKKIT_SCOREBOARD(CRAFTBUKKIT, "scoreboard"),
		CRAFTBUKKIT_UPDATER(CRAFTBUKKIT, "updater"),
		CRAFTBUKKIT_UTIL(CRAFTBUKKIT, "util");

		private final String path;

		private PackageType(String path) {
			this.path = path;
		}

		private PackageType(PackageType parent, String path) {
			this(parent + "." + path);
		}

		public String getPath() {
			return path;
		}

		public Class<?> getClass(String className) throws ClassNotFoundException {
			return Class.forName(this + "." + className);
		}

		@Override
		public String toString() {
			return path;
		}

		public static String getServerVersion() {
			return Bukkit.getServer().getClass().getPackage().getName().substring(23);
		}
	}
	
	public enum DataType {
		BYTE(byte.class, Byte.class),
		SHORT(short.class, Short.class),
		INTEGER(int.class, Integer.class),
		LONG(long.class, Long.class),
		CHARACTER(char.class, Character.class),
		FLOAT(float.class, Float.class),
		DOUBLE(double.class, Double.class),
		BOOLEAN(boolean.class, Boolean.class);

		private static final Map<Class<?>, DataType> CLASS_MAP = new HashMap<Class<?>, DataType>();
		private final Class<?> primitive;
		private final Class<?> reference;

		static {
			for (DataType type : values()) {
				CLASS_MAP.put(type.primitive, type);
				CLASS_MAP.put(type.reference, type);
			}
		}

		private DataType(Class<?> primitive, Class<?> reference) {
			this.primitive = primitive;
			this.reference = reference;
		}

		public Class<?> getPrimitive() {
			return primitive;
		}

		public Class<?> getReference() {
			return reference;
		}

		public static DataType fromClass(Class<?> clazz) {
			return CLASS_MAP.get(clazz);
		}

		public static Class<?> getPrimitive(Class<?> clazz) {
			DataType type = fromClass(clazz);
			return type == null ? clazz : type.getPrimitive();
		}

		public static Class<?> getReference(Class<?> clazz) {
			DataType type = fromClass(clazz);
			return type == null ? clazz : type.getReference();
		}

		public static Class<?>[] getPrimitive(Class<?>[] classes) {
			int length = classes == null ? 0 : classes.length;
			Class<?>[] types = new Class<?>[length];
			for (int index = 0; index < length; index++) {
				types[index] = getPrimitive(classes[index]);
			}
			return types;
		}

		public static Class<?>[] getReference(Class<?>[] classes) {
			int length = classes == null ? 0 : classes.length;
			Class<?>[] types = new Class<?>[length];
			for (int index = 0; index < length; index++) {
				types[index] = getReference(classes[index]);
			}
			return types;
		}

		public static Class<?>[] getPrimitive(Object[] objects) {
			int length = objects == null ? 0 : objects.length;
			Class<?>[] types = new Class<?>[length];
			for (int index = 0; index < length; index++) {
				types[index] = getPrimitive(objects[index].getClass());
			}
			return types;
		}

		public static Class<?>[] getReference(Object[] objects) {
			int length = objects == null ? 0 : objects.length;
			Class<?>[] types = new Class<?>[length];
			for (int index = 0; index < length; index++) {
				types[index] = getReference(objects[index].getClass());
			}
			return types;
		}

		public static boolean compare(Class<?>[] primary, Class<?>[] secondary) {
			if (primary == null || secondary == null || primary.length != secondary.length) {
				return false;
			}
			for (int index = 0; index < primary.length; index++) {
				Class<?> primaryClass = primary[index];
				Class<?> secondaryClass = secondary[index];
				if (primaryClass.equals(secondaryClass) || primaryClass.isAssignableFrom(secondaryClass)) {
					continue;
				}
				return false;
			}
			return true;
		}
	}

	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//predefined options

	public static ItemStack asBukkitCopy(Object nmsStack) {
		try {
			Method m = Reflection.getOBCClass("inventory.CraftItemStack").getMethod("asBukkitCopy", Reflection.getNMSClass("ItemStack"));
			return (ItemStack) m.invoke(null, nmsStack);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object asNMSCopy(ItemStack stack) {
		try {
			Method m = Reflection.getOBCClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class);
			return m.invoke(null, stack);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * @param v1 version number to compare with
	 * @param v2 version number
	 * @return -1 if v2 is lower than v1, 0 if equal and 1 if v2 is higher than v1
	 */
	public static int compareVersions(String v1, String v2) {
		String[] va1 = v1.split(Pattern.quote("."));
		String[] va2 = v2.split(Pattern.quote("."));

		for ( int i = 0; i < Math.min(va2.length, va1.length); i++ ) {
			int num1 = Integer.parseInt(va1[i]);
			int num2 = Integer.parseInt(va2[i]);

			if ( num1 < num2 ) return 1;
			if ( num1 > num2 ) return -1;
		}

		return 0;
	}

	public static boolean isMcLowerOrEqualTo(String version) {
		return compareVersions(version, getVersion()) <= 0;
	}

	public static boolean isMcLowerThan(String version) {
		return compareVersions(version, getVersion()) < 0;
	}

	public static boolean isMcGreaterThan(String version) {
		return compareVersions(version, getVersion()) > 0;
	}

	public static boolean isMcGreaterOrEqualTo(String version) {
		return compareVersions(version, getVersion()) >= 0;
	}

}
