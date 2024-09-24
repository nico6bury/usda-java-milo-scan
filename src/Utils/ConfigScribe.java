package Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import IJM.IJProcess;

/**
 * This class is used for serializing and deserializing config settings.
 * 
 * How to add new entries to the config file?
 * In order to add new entries to the config file, all you have to do is add fields to ConfigStoreH.java or ConfigStoreC.java.
 * This class uses reflection to read the name, type, and value from those classes, so that's really all that's necessary. 
 * If desired, code could be added to write_config() in order to add default comments for newly added parameters, but that's not
 * necessary to just save and load new fields.  
 * Newly added variables to ConfigStore might need added code for reading them in and assigning other variables off of them.
 * 
 */
public class ConfigScribe {
	/** The name of the config file that will be human-readable and have options that can be set within the application. */
	public static final String h_config_name = "milo-scan.config";
	/** The name of the config file that doesn't have to be human-readable and has options to help with application functions. */
	public static final String c_config_name = ".config";
	/** Constructs the class */
	public ConfigScribe() {}

	/**
	 * Just stores both kinds of ConfigStores
	 */
	public class PairedConfigStores {
		public ConfigStoreH configStoreH;
		public ConfigStoreC configStoreC;
		public PairedConfigStores(ConfigStoreH ch, ConfigStoreC cc) {configStoreH = ch;configStoreC = cc;}
	}//end struct PairedConfigStores

	/**
	 * This method writes information to both config files.
	 * This method is written in such a way as to keep any lines in an existing config file
	 * that don't contain serialized information, so comments on separate lines are allowed.
	 * @param store_h A ConfigStoreH value to be written to the file.
	 * @param store_c A ConfigStoreC value to be written to the file.
	 * @return Returns either a meaningless string or an exception if something stopped execution from finishing.
	 */
	public Result<String> write_config(ConfigStoreH store_h, ConfigStoreC store_c) {
		String jar_location;
		try {
			// figure out path to write file to
			jar_location = new File(IJProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().toString();
			File config_h_filepath = new File(jar_location + File.separator + h_config_name);
			File config_c_filepath = new File(jar_location + File.separator + c_config_name);
			// make sure file exists
			boolean add_header_to_config = false;
			if (!config_h_filepath.exists()) { config_h_filepath.createNewFile(); add_header_to_config = true; }
			if (!config_c_filepath.exists()) { config_c_filepath.createNewFile(); }
			// get all the lines from the config file
			List<String> config_h_lines = Files.readAllLines(config_h_filepath.toPath());
			List<String> config_c_lines = Files.readAllLines(config_c_filepath.toPath());
			// if the human-readable config doesn't have a header, then we should add it
			if (add_header_to_config) {
				config_h_lines.add("# This is the human-readable config file for the USDA Milo Scan Program");
				config_h_lines.add("# This milo scan program was created by Nicholas Sixbury, based off the"); 
				config_h_lines.add("# flour scan project created by the efforts of Nicholas Sixbury, Daniel Brabec,");
				config_h_lines.add("# and Bill Rust, as part of work for the USDA-ARS in Manhattan,KS. The source");
				config_h_lines.add("# code is available at https://github.com/nico6bury/usda-java-milo-scan/");
				config_h_lines.add("# ");
				config_h_lines.add("# In this file, lines not parsed as variable serialization are automatically ignored.");
				config_h_lines.add("# Because of this, any line starting with \'#\' will automatically be interpretted as");
				config_h_lines.add("# a comment. Any comments will be untouched in the config file, so feel free to add");
				config_h_lines.add("# your own comments.");
				config_h_lines.add("# ");
				config_h_lines.add("# If this config file is ever deleted, then it should be re-generated on program");
				config_h_lines.add("# startup. All parameters will be set to default, and all default comments will be");
				config_h_lines.add("# added to the new config file, including this header comment.");
				config_h_lines.add("# ");
				config_h_lines.add("# Just about every parameter in this config file can also be set through a menu in");
				config_h_lines.add("# the program, so don't feel as though you have to use this config file to change");
				config_h_lines.add("# settings.");
				config_h_lines.add("");
			}//end if we need to add a header to the config file
			// get list of fields to use for looking stuff up in match map
			Field[] fields_h = ConfigStoreH.class.getFields();
			Field[] fields_c = ConfigStoreC.class.getFields();
			// find the lines at which things are written in existing config, if found at all
			HashMap<String, Integer> match_map_h = match_config_lines(config_h_lines, fields_h);
			HashMap<String, Integer> match_map_c = match_config_lines(config_c_lines, fields_c);
			// update lines in config file with values from parameters
			for (int i = 0; i < fields_h.length; i++) {
				// get the formatting figured out beforehand since it will be the same
				String f_line = fields_h[i].getName() + " = " + fields_h[i].get(store_h);
				// check whether or not current field is already recorded in file
				if (match_map_h.containsKey(fields_h[i].getName())) {
					// rewrite the line at index in match_map[fields[i].getName()]
					int index = match_map_h.get(fields_h[i].getName());
					config_h_lines.set(index, f_line);
				}//end if we can rewrite the corresponding line
				else {
					// add customized comments for each potential field
					if (fields_h[i].getName() == "proc_threshold") {
						config_h_lines.add("# This is the number used as the upper threshold in imagej particle analysis");
					} else if (fields_h[i].getName() == "area_threshold_lower") {
						config_h_lines.add("# Any file with average % area greater than this will get a flag of x");
					} else if (fields_h[i].getName() == "area_threshold_upper") {
						config_h_lines.add("# Any file with average % area greater than this will get a flag of xx");
					} else if (fields_h[i].getName() == "unsharp_sigma") {
						config_h_lines.add("# Sigma radius to use with unsharp mask to try and replicate epson setting.");
					} else if (fields_h[i].getName() == "unsharp_weight") {
						config_h_lines.add("# Mask weight to use with unsharp mask to try and replicate epson setting.");
					} else if (fields_h[i].getName() == "scan_x1") {
						config_h_lines.add("# x coordinate in inches of upper left corner of scan area");
					} else if (fields_h[i].getName() == "scan_y1") {
						config_h_lines.add("# y coordinate in inches of upper left corner of scan area");
					} else if (fields_h[i].getName() == "scan_x2") {
						config_h_lines.add("# x coordinate in inches of lower right corner of scan area");
					} else if (fields_h[i].getName() == "scan_y2") {
						config_h_lines.add("# y coordinate in inches of lower right corner of scan area");
					} else if (fields_h[i].getName() == "unsharp_skip") {
						config_h_lines.add("# if true, then the unsharp mask will be skipped");
					} else if (fields_h[i].getName() == "unsharp_rename") {
						config_h_lines.add("# if true, the unsharp masked image will be renamed as a new file. Otherwise, it will overwrite the original.");
					}
					// add a new line for fields[i]
					config_h_lines.add(f_line);
					// add extra line for spacing
					config_h_lines.add("");
				}//end else we'll have to add a new line for this field
			}//end looping over fields, matching and writing
			for (int i = 0; i < fields_c.length; i++) {
				// get the formatting figured out beforehand since it will be the same
				String f_line = fields_c[i].getName() + " = " + fields_c[i].get(store_c);
				// check whether or not current field is already recorded in file
				if (match_map_c.containsKey(fields_c[i].getName())) {
					// rewrite the line at index in match_map[fields[i].getName()]
					int index = match_map_c.get(fields_c[i].getName());
					config_c_lines.set(index, f_line);
				}//end if we can rewrite the corresponding line
				else {
					// add a new line for fields[i]
					config_c_lines.add(f_line);
				}//end else we'll have to add a new line for this field
			}//end looping over fields, matching and writing
			// clear files of text
			new FileWriter(config_h_filepath, false).close();
			new FileWriter(config_c_filepath, false).close();
			new PrintWriter(config_c_filepath).close();
			// write changes to files
			Files.write(config_h_filepath.toPath(), config_h_lines);
			Files.write(config_c_filepath.toPath(), config_c_lines);
		} catch (Exception e) { e.printStackTrace(); return new Result<String>(e);}

		return new Result<String>("No Exceptions Encountered.");
	}//end write_config(store_h, store_c)

	/**
	 * This method reads from both config files and creates ConfigStores based on the data parsed.
	 * This method is written in such a way as to ignore any lines that don't contain serialized information,
	 * so any comment lines will be ignored.
	 * @return Either a pair of ConfigStores, or an exception that prevented the method from finishing.
	 */
	public Result<PairedConfigStores> read_config() {
		String jar_location;
		try {
			// figure out path to read file from
			jar_location = new File(IJProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().toString();
			File config_h_filepath = new File(jar_location + File.separator + h_config_name);
			File config_c_filepath = new File(jar_location + File.separator + c_config_name);
			// get all the lines from the config file
			List<String> config_h_lines;
			List<String> config_c_lines;
			// get all lines from config files
			if (!config_h_filepath.exists()) { config_h_lines = new ArrayList<String>(); }
			else { config_h_lines = Files.readAllLines(config_h_filepath.toPath()); }
			if (!config_c_filepath.exists()) { config_c_lines = new ArrayList<String>(); }
			else { config_c_lines = Files.readAllLines(config_c_filepath.toPath()); }
			// get list of fields to use for looking stuff up in match map
			Field[] fields_h = ConfigStoreH.class.getFields();
			Field[] fields_c = ConfigStoreC.class.getFields();
			// find the lines at which things are written in existing config, if found at all
			HashMap<String, Integer> match_map_h = match_config_lines(config_h_lines, fields_h);
			HashMap<String, Integer> match_map_c = match_config_lines(config_c_lines, fields_c);
			// create store objects to read information into
			ConfigStoreH configStoreH = new ConfigStoreH();
			ConfigStoreC configStoreC = new ConfigStoreC();
			// update lines in config file with values from parameters
			for (int i = 0; i < fields_h.length; i++) {
				// check whether or not current field is recorded in file
				if (match_map_h.containsKey(fields_h[i].getName())) {
					// read line at index in match_map[fields[i].getName()]
					int index = match_map_h.get(fields_h[i].getName());
					String this_line = config_h_lines.get(index); // name = value
					String[] split_line = this_line.split(" = ");
					if (split_line.length == 2) {
						// try and parse depending on type of field
						if (fields_h[i].getType() == int.class) {
							int val = Integer.parseInt(split_line[1]);
							fields_h[i].setInt(configStoreH, val);
						}//end if it's an integer
						if (fields_h[i].getType() == double.class) {
							double val = Double.parseDouble(split_line[1]);
							fields_h[i].setDouble(configStoreH, val);
						}//end if it's a double
						if (fields_h[i].getType() == boolean.class) {
							boolean val = Boolean.parseBoolean(split_line[1]);
							fields_h[i].setBoolean(configStoreH, val);
						}//end if it's a boolean
					}//end if split line has expected length
					else {
						System.err.println("Some sort of exceptional case has happened when reading the config. I'm not really sure what to do now.");
					}//end else we need to try another method of parsing
				}//end if we found a line for this value
			}//end looping over fields, matching and writing
			for (int i = 0; i < fields_c.length; i++) {
				// check whether or not current field is recorded in file
				if (match_map_h.containsKey(fields_c[i].getName())) {
					// read line at index in match_map[fields[i].getName()]
					int index = match_map_c.get(fields_c[i].getName());
					String this_line = config_c_lines.get(index); // name = value
					String[] split_line = this_line.split(" = ");
					if (split_line.length == 2) {
						// we will just assume that the field is a string
						fields_c[i].set(configStoreC, split_line[1]);
					}//end if split line has expected length
					else {
						System.err.println("Some sort of exceptional case has happened when reading the config. I'm not really sure what to do now.");
					}//end else we need to try another method of parsing
				}//end if we found a line for this value
			}//end looping over fields, matching and writing
			// return pair wrapped in result
			return new Result<>(new PairedConfigStores(configStoreH, configStoreC));
		} catch (Exception e) { e.printStackTrace(); return new Result<PairedConfigStores>(e);}
	}//end read_config()

	/**
	 * This helper method finds the index in the provided lines at which each property of ConfigStore starts a line.
	 * @param lines The lines of text from a config file
	 * @return Returns a hashmap, with keys being strings denoting the names of properties of ConfigStore, and values being the index they're found at
	 */
	protected HashMap<String, Integer> match_config_lines(List<String> lines, Field[] fields) {
		HashMap<String, Integer> match_map = new HashMap<>();

		// get list of the names of properties of ConfigStore
		for (int i = 0; i < fields.length; i++) {
			String this_field_name = fields[i].getName();
			// loop through lines to see if one of them starts with this_field_name
			for (int j = 0; j < lines.size(); j++) {
				String this_line = lines.get(j);
				String this_trimmed_line = this_line.substring(0, Math.min(this_line.length(), this_field_name.length()));
				if (this_trimmed_line.equalsIgnoreCase(this_field_name)) {match_map.put(this_field_name, j); break;}
			}//end looping over each line
		}//end looping over each field

		return match_map;
	}//end match_config_lines
}//end class Config
