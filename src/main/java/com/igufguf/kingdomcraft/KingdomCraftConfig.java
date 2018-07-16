package com.igufguf.kingdomcraft;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.List;

/**
 * Copyrighted 2017 iGufGuf
 *
 * This file is part of KingdomCraft.
 *
 * Kingdomcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <http://www.gnu.org/licenses/>.
 *
 **/
public class KingdomCraftConfig {

	private static final double configversion = 1.2;

	private File file;
	private YamlConfiguration config;

	public KingdomCraftConfig(KingdomCraft plugin) {
		file = new File(plugin.getDataFolder(), "config.yml");

		if ( !file.exists() ) {
			saveDefault(plugin);
			config = YamlConfiguration.loadConfiguration(file);
		} else {
			config = YamlConfiguration.loadConfiguration(file);

			if ( config.get("version") == null || config.getDouble("version") < configversion ) {
				File newf = getFilename(new File(plugin.getDataFolder(), "config.old.yml"));
				if ( !file.renameTo(newf) ) {
					newf.delete();
				}

				saveDefault(plugin);

				file = new File(plugin.getDataFolder(), "config.yml");
				config = YamlConfiguration.loadConfiguration(file);

				System.out.println("!!! CONFIG UPDATE !!!\n\nCreated a new config.yml and renamed the old one to '" + newf.getName() + "', please update the new one asap!\n\n");
			}
		}
	}

	public String getString(String path) {
		return config.getString(path);
	}

	public int getInt(String path) {
		return config.getInt(path);
	}

	public double getDouble(String path) {
		return config.getDouble(path);
	}

	public boolean getBoolean(String path) {
		return (config.get(path) != null && config.getBoolean(path));
	}

	public List<String> getStringList(String path) {
		return config.getStringList(path);
	}

	public ConfigurationSection getSection(String path) {
		return config.getConfigurationSection(path);
	}

	public boolean has(String path) {
		return config.get(path) != null;
	}

	@Deprecated
	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private File getFilename(File file) {
		String baseName = FilenameUtils.getBaseName( file.getName() );
		String extension = FilenameUtils.getExtension( file.getName() );
		int counter = 1;
		while ( file.exists() ) {
			file = new File( file.getParent(), baseName + "-" + (counter++) + "." + extension );
		}
		return file;
	}

	private void saveDefault(Plugin plugin) {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = plugin.getResource("config.yml");

			byte[] buffer = new byte[4096];
			File f = new File(plugin.getDataFolder(), "config.yml");
			f.createNewFile();
			out = new FileOutputStream(f);

			int bytesread;
			while ( (bytesread = in.read(buffer)) > 0 ) {
				String str = new String(buffer, "UTF-8");

				if ( str.contains("{VERSION}") ) {
					String version = configversion + "";
					str = str.replace("{VERSION}", version);

					bytesread -= "{VERSION}".length() - version.length();
				}

				out.write(str.getBytes(), 0, bytesread);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if ( in != null ) in.close();
				if ( out != null ) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
