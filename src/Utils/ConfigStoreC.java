package Utils;

/**
 * This class basically just acts as a struct to hold different labeled properies that should be stored in a non-human readable config file
 */
public class ConfigStoreC {
    public String file_chooser_dir = "";
    public ConfigStoreC() {}
    public ConfigStoreC(String file_chooser_dir) {
        this.file_chooser_dir = file_chooser_dir;
    }//end full constructor
}//end class ConfigStoreC
