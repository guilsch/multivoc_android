package com.guilsch.vocavox;

import android.graphics.drawable.Drawable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This class contains all the global parameters of the application. The -_DEFAULT parameters are
 * default constant parameters.
 *
 *
 * @author Guilhem Schena
 */
public class Param {

    public static final String FILE_NAME_PREFIX = "words_database_";
    public static String FOLDER_PATH = "storage/emulated/0/Vocavox/";
    public static final String FOLDER_ID_DEFAULT = "";
    public static final String FILE_ID_UNDEFINED = "undefined";
    public static String DATA_FILE;
    public static String DATA_PATH;
    public static final int LAST_LANG_DEFAULT = 0;

    public static final String MIME_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static void setDataPath() {
        DATA_PATH = FOLDER_PATH + DATA_FILE;
    }


    public static final String[] TARGET_LANGUAGES = {"English", "German", "French", "Italian", "Russian", "Spanish"};
    public static final Language[] TARGET_LANGUAGES_FULL = {
            new Language("English", "en", R.drawable.flag_ic_gb),
            new Language("Deutsch", "de", R.drawable.flag_ic_de),
            new Language("Français", "fr", R.drawable.flag_ic_fr),
            new Language("Italiano", "it", R.drawable.flag_ic_it),
            new Language("Русский", "ru", R.drawable.flag_ic_ru),
            new Language("Español", "es", R.drawable.flag_ic_es)
    };
    public static final String[] USER_LANGUAGES = {"English", "German", "French", "Italian", "Russian", "Spanish"};
    public static final String[] USER_LANGUAGES_ISO = {"en", "de", "fr", "it", "ru", "es"};

    public static String TARGET_LANGUAGE_FULL;
    public static String TARGET_LANGUAGE;
    public static String TARGET_LANGUAGE_ISO;

    public static String USER_LANGUAGE;
    public static String USER_LANGUAGE_ISO;
    public static String USER_LANGUAGE_DEFAULT = "English";
    public static String USER_LANGUAGE_ISO_DEFAULT = "en";

    public static Drawable FLAG_ICON_TARGET;
    public static Drawable FLAG_ICON_USER;

    public static final String ITEM1_FIELD_NAME = "Item 1";
    public static final String ITEM2_FIELD_NAME = "Item 2";
    public static final String PACK_FIELD_NAME = "Pack";
    public static final String STATE_FIELD_NAME = "State";
    public static final String NEXT_DATE_FIELD_NAME = "Next Date";
    public static final String CREATION_DATE_FIELD_NAME = "Creation Date";
    public static final String REPETITIONS_FIELD_NAME = "Repetitions";
    public static final String EF_FIELD_NAME = "Easiness Factor";
    public static final String INTERVAL_FIELD_NAME = "Interval";
    public static final String UUID_FIELD_NAME = "UUID";

    public static final List<String> FIELDS = Arrays.asList(UUID_FIELD_NAME, ITEM1_FIELD_NAME, ITEM2_FIELD_NAME,
            PACK_FIELD_NAME, STATE_FIELD_NAME, CREATION_DATE_FIELD_NAME, NEXT_DATE_FIELD_NAME, REPETITIONS_FIELD_NAME,
            EF_FIELD_NAME, INTERVAL_FIELD_NAME);
    public static final int FIELDS_NB = FIELDS.size(); // 10


    public static final int ACTIVE = 1;
    public static final int INACTIVE = 2;
    public static final int IN_PAUSE = 3;

    public static final int DEFAULT_STATE = INACTIVE;
    public static final String DEFAULT_PACK = "";
    public static final Date DEFAULT_NEXT_DATE = Utils.giveCurrentDate();
    public static final Date DEFAULT_CREATION_DATE = Utils.giveCurrentDate();
    public static final int DEFAULT_REP = 0;
    public static final int DEFAULT_EF = 0;
    public static final int DEFAULT_INTER = 0;

    public static String FILE_ID;

    // Settings
    public static final int LANG_DIRECTION_FREQ_DEFAULT = 0;
    public static final boolean AUTOMATIC_SPEECH_DEFAULT = true;


    public static String APP_PREF = "app_pref";

    // Deck data
    public static Deck GLOBAL_DECK;
    public static int CARDS_TO_REVIEW_NB;
    public static int CARDS_TO_LEARN_NB;
    public static int CARDS_NB;
    public static int ACTIVE_CARDS_NB;


    // Shared preferences variables

    // Variables
    public static String EN_FILE_ID = "Undefined";
    public static String FR_FILE_ID = "Undefined";
    public static String GE_FILE_ID = "Undefined";
    public static String IT_FILE_ID = "Undefined";
    public static String RU_FILE_ID = "Undefined";
    public static String SP_FILE_ID = "Undefined";
    public static String FOLDER_ID = "Undefined";
    public static int LANG_DIRECTION_FREQ;
    public static int LAST_LANG;
    public static boolean AUTOMATIC_SPEECH;

    // Keys
    public static final String FOLDER_PATH_KEY = "folder_path_key";
    public static final String EN_FILE_ID_KEY = "en_file_id_key";
    public static final String FR_FILE_ID_KEY = "fr_file_id_key";
    public static final String GE_FILE_ID_KEY = "ge_file_id_key";
    public static final String IT_FILE_ID_KEY = "it_file_id_key";
    public static final String RU_FILE_ID_KEY = "ru_file_id_key";
    public static final String SP_FILE_ID_KEY = "sp_file_id_key";
    public static final String FOLDER_ID_KEY = "folder_id_key";
    public static final String LANG_DIRECTION_FREQ_KEY = "lang_direction_freq_key";
    public static final String LAST_LANG_KEY = "last_lang_key";
    public static final String AUTOMATIC_SPEECH_KEY = "automatic_speech_key";

    // First launch check
    public static Boolean FIRST_LAUNCH = Boolean.TRUE;
    public static final String FIRST_LAUNCH_KEY = "first_launch_key";

    // Debug
    public static final boolean DEBUG = Boolean.TRUE;
    public static final String DEBUG_FILE_NAME = "debug_file.txt";

    // Card comparator
    public static final int SORT_ORDER_AZ = 0;
    public static final int SORT_ORDER_ZA = 1;
    public static final int SORT_BY_CREATION_DATE = 1;
    public static final int SORT_BY_TRAINING_DATE = 2;
    public static final int SORT_ALPHABETICALLY_USER = 3;
    public static final int SORT_ALPHABETICALLY_TARGET = 4;

    // Setting activity, import file dialog
    public static final String CONFIRM_KEY = "confirm_dialog_key";
    public static final int CONFIRM_IMPORT = 201;
    public static final int CONFIRM_CANCEL = 202;
    public static final int CONFIRM_DELETE_DATA_FILE = 203;

    public static Boolean DATA_FILE_ERROR = false;
}
