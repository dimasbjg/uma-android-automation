package com.steve1316.uma_android_automation.bot.career_setup

import android.graphics.Bitmap
import com.steve1316.automation_library.utils.MessageLog
import com.steve1316.automation_library.utils.SettingsHelper
import com.steve1316.uma_android_automation.MainActivity
import com.steve1316.uma_android_automation.bot.CampaignBreakpointException
import com.steve1316.uma_android_automation.bot.DialogHandlerResult
import com.steve1316.uma_android_automation.bot.Game
import com.steve1316.uma_android_automation.bot.dailies.parseCounter
import com.steve1316.uma_android_automation.bot.solver.MandatoryRaces
import com.steve1316.uma_android_automation.components.ButtonAutoSelect
import com.steve1316.uma_android_automation.components.ButtonBack
import com.steve1316.uma_android_automation.components.ButtonBorrowSupportCard
import com.steve1316.uma_android_automation.components.ButtonCancel
import com.steve1316.uma_android_automation.components.ButtonCareerStartArrowRight
import com.steve1316.uma_android_automation.components.ButtonCareerStartDeckArrowLeft
import com.steve1316.uma_android_automation.components.ButtonCareerStartDeckArrowRight
import com.steve1316.uma_android_automation.components.ButtonCareerStartHomeCareer
import com.steve1316.uma_android_automation.components.ButtonCareerStartIndependentAgendaEdit
import com.steve1316.uma_android_automation.components.ButtonCareerStartIndependentStart
import com.steve1316.uma_android_automation.components.ButtonCareerStartLegacyReset
import com.steve1316.uma_android_automation.components.ButtonCareerStartStorySkipOff
import com.steve1316.uma_android_automation.components.ButtonCareerStartStorySkipOne
import com.steve1316.uma_android_automation.components.ButtonCareerStartStorySkipTwo
import com.steve1316.uma_android_automation.components.ButtonClose
import com.steve1316.uma_android_automation.components.ButtonCompleteCareer
import com.steve1316.uma_android_automation.components.ButtonConfirm
import com.steve1316.uma_android_automation.components.ButtonHomeFullStats
import com.steve1316.uma_android_automation.components.ButtonMenuBarHome
import com.steve1316.uma_android_automation.components.ButtonMyAgendas
import com.steve1316.uma_android_automation.components.ButtonNext
import com.steve1316.uma_android_automation.components.ButtonNo
import com.steve1316.uma_android_automation.components.ButtonOk
import com.steve1316.uma_android_automation.components.ButtonOverwrite
import com.steve1316.uma_android_automation.components.ButtonRaceAgendaLoadList
import com.steve1316.uma_android_automation.components.ButtonSelectLegacy
import com.steve1316.uma_android_automation.components.ButtonSkip
import com.steve1316.uma_android_automation.components.ButtonStartCareer
import com.steve1316.uma_android_automation.components.Checkbox
import com.steve1316.uma_android_automation.components.ComponentInterface
import com.steve1316.uma_android_automation.components.DialogBorrowCard
import com.steve1316.uma_android_automation.components.DialogConfirmAutoSelect
import com.steve1316.uma_android_automation.components.DialogConfirmRestoreRP
import com.steve1316.uma_android_automation.components.DialogDateChanged
import com.steve1316.uma_android_automation.components.DialogDisplaySettings
import com.steve1316.uma_android_automation.components.DialogFinalConfirmation
import com.steve1316.uma_android_automation.components.DialogIndependentTraining
import com.steve1316.uma_android_automation.components.DialogInterface
import com.steve1316.uma_android_automation.components.DialogOverwrite
import com.steve1316.uma_android_automation.components.DialogUtils
import com.steve1316.uma_android_automation.components.IconCareerStartTpCostHalved
import com.steve1316.uma_android_automation.components.LabelCareerStartIndependentTrainingComplete
import com.steve1316.uma_android_automation.components.LabelCareerStartIndependentTrainingTimeLeft
import com.steve1316.uma_android_automation.components.LabelCareerStartLegacySelectHeader
import com.steve1316.uma_android_automation.components.LabelCareerStartPotentialLvl
import com.steve1316.uma_android_automation.components.LabelCareerStartScenarioSelectHeader
import com.steve1316.uma_android_automation.components.LabelCareerStartSupportFormationHeader
import com.steve1316.uma_android_automation.components.LabelCareerStartTraineeSelectHeader
import com.steve1316.uma_android_automation.types.TrackDistance
import com.steve1316.uma_android_automation.utils.Anchor
import com.steve1316.uma_android_automation.utils.CustomImageUtils
import com.steve1316.uma_android_automation.utils.ScreenLayout
import com.steve1316.uma_android_automation.utils.ScrollList
import org.json.JSONArray
import org.json.JSONObject
import org.opencv.core.Point

// //////////////////////////////////////////////////////////////////////////////////////////////////
// //////////////////////////////////////////////////////////////////////////////////////////////////
// Screen geometry, measured at 1080x1920 as offsets from a template on the same screen so it holds wherever the screen puts that template

/** The orange keyword in each scenario's description on Scenario Select, keyed by the `general.scenario` value. */
private val SCENARIO_KEYWORDS: Map<String, String> =
    mapOf(
        "URA Finale" to "urafinale",
        "Unity Cup" to "unitycup",
        "Trackblazer" to "twinklestar",
        "Grand Live" to "grandconcert",
    )

/** The scenario description box on Scenario Select, from the Next button. */
private val SCENARIO_DESCRIPTION = intArrayOf(-455, -316, 900, 180)

/** The "88/100" TP counter in the top bar on Scenario Select, from the screen's header. */
private val SCENARIO_TP_COUNTER = intArrayOf(279, -219, 135, 47)

/** The outfit and name lines of the selected trainee on Trainee Select, from the screen's header. */
private val TRAINEE_OUTFIT_LINE = intArrayOf(-53, 321, 420, 32)
private val TRAINEE_NAME_LINE = intArrayOf(-53, 353, 420, 36)

/** A trainee card's portrait sits this far above its "Potential Lvl" label. */
private const val TRAINEE_PORTRAIT_DY = -110

/** The "Deck N" title on Support Formation, from the screen's header. */
private val DECK_TITLE = intArrayOf(-74, 71, 300, 60)

/** The card type count columns under the deck on Support Formation, as their center dx from the Borrow button, in game order. */
private val DECK_TYPE_COLUMNS: List<Pair<String, Int>> =
    listOf("Speed" to -606, "Stamina" to -510, "Power" to -414, "Guts" to -318, "Wit" to -222, "Friend" to -126, "Group" to -30)

/** The "xN" count under each type icon, from the Borrow button. */
private const val DECK_COUNT_DY = 270
private const val DECK_COUNT_W = 90
private const val DECK_COUNT_H = 42

/** The Normal Career tab on Final Confirmation, from the Cancel button. */
private const val NORMAL_CAREER_TAB_DX = -20
private const val NORMAL_CAREER_TAB_DY = -1450

/** The Independent Training tab on Final Confirmation, from the Cancel button. */
private const val INDEPENDENT_TAB_DX = 492
private const val INDEPENDENT_TAB_DY = -1450

/** A My Agendas card's title on its green header bar, from the card's Load List button. The shared reader thresholds the crop, which wipes out the white title. */
private val AGENDA_HEADER = intArrayOf(-830, -215, 560, 45)

/** The "0:49:38 left" timer on the Training screen, from the "Time Left" pill. */
private val TRAINING_TIME_LEFT = intArrayOf(191, -43, 200, 50)

/** Extra seconds waited past the timer, so the results have time to show. */
private const val TRAINING_END_BUFFER_SECONDS = 10

/** The timer as hours, minutes and seconds, such as 0:49:38. OCR may read a colon as a period. */
private val TIME_LEFT = Regex("(\\d{1,2})[:.](\\d{2})[:.](\\d{2})")

/** How many times My Agendas is scrolled looking for the agenda. Each scroll moves about one card. */
private const val MAX_AGENDA_SCROLLS = 10

/** How far one scroll of My Agendas drags, about one card's height. */
private const val AGENDA_SCROLL_DY = 330

/** The shortest agenda header that may match a longer title by its start. The header crop cuts long titles off, while a short read like "Agenda" must not match "Agenda 6". */
private const val MIN_AGENDA_PREFIX = 10

/** The "Spend N TP to begin training?" line on Final Confirmation, from the Cancel button. */
private val FINAL_CONFIRMATION_SPEND_LINE = intArrayOf(-40, -205, 560, 50)

/** The current TP left of the arrow on Final Confirmation, from the Cancel button. */
private val FINAL_CONFIRMATION_CURRENT_TP = intArrayOf(198, -137, 100, 55)

/** The first number in a line of OCR text, such as the 15 in "Spend 15 TP to begin training?". */
private val FIRST_NUMBER = Regex("""\d+""")

/** Screens the loop may pass through before giving up. */
private const val MAX_TICKS = 120

/** Arrow taps allowed on the scenario and deck carousels. */
private const val MAX_CAROUSEL_TAPS = 10

/** The trainee grid's sort pill ("Stars", "Name", ...) and its ascending / descending toggle, from the Next button. */
private val TRAINEE_SORT_PILL = intArrayOf(60, -150, 230, 44)
private val TRAINEE_ORDER_TOGGLE = intArrayOf(360, -150, 110, 44)

/** The Sort tab and the Name option on the Display Settings dialog, from its OK button. */
private const val DISPLAY_SORT_TAB_DX = -494
private const val DISPLAY_SORT_TAB_DY = -1570
private const val DISPLAY_SORT_NAME_DX = -238
private const val DISPLAY_SORT_NAME_DY = -912

/** Cards below this line sit behind the grid's Filters / sort pill bar. Measured from the Next button. */
private const val TRAINEE_GRID_READ_BOTTOM_DY = -161

/** Scroll pages allowed on the trainee grid. */
private const val MAX_TRAINEE_PAGES = 30

/** The TP a new career costs. The TP Cost Halved event halves it. */
private const val CAREER_TP_COST = 30

// //////////////////////////////////////////////////////////////////////////////////////////////////
// //////////////////////////////////////////////////////////////////////////////////////////////////
// Helpers

/**
 * Converts offsets measured at 1080x1920 from a template into a screen region.
 *
 * @param imageUtils The bot's image utilities, for scaling to the device.
 * @param anchor The template's center on screen.
 * @param offsets The region as dx, dy, width and height from the anchor.
 * @returns The region as x, y, width and height.
 */
internal fun anchoredRegion(imageUtils: CustomImageUtils, anchor: Point, offsets: IntArray): IntArray =
    intArrayOf(imageUtils.relX(anchor.x, offsets[0]), imageUtils.relY(anchor.y, offsets[1]), imageUtils.relWidth(offsets[2]), imageUtils.relHeight(offsets[3]))

/**
 * OCRs a region measured at 1080x1920 from a template.
 *
 * @param imageUtils The bot's image utilities.
 * @param bitmap The screenshot.
 * @param anchor The template's center on screen.
 * @param offsets The region as dx, dy, width and height from the anchor.
 * @param debugName Name for debug images.
 * @returns The text read.
 */
internal fun ocrAnchored(imageUtils: CustomImageUtils, bitmap: Bitmap, anchor: Point, offsets: IntArray, debugName: String): String {
    val (x, y, w, h) = anchoredRegion(imageUtils, anchor, offsets).toList()
    return imageUtils.performOCROnRegion(bitmap, x, y, w, h, useThreshold = false, ocrEngine = "mlkit", debugName = debugName)
}

/**
 * Taps a point measured at 1080x1920 from a template.
 *
 * @param game Reference to the bot's [Game] instance.
 * @param anchor The template's center on screen.
 * @param dx The x offset from the anchor.
 * @param dy The y offset from the anchor.
 */
internal fun tapAnchored(game: Game, anchor: Point, dx: Int, dy: Int) = game.tap(game.imageUtils.relX(anchor.x, dx).toDouble(), game.imageUtils.relY(anchor.y, dy).toDouble())

/**
 * Finds the empty legacy slots from the plus buttons on Legacy Select. Slot 1 is on the left half of the screen and slot 2 on the right.
 *
 * @param plusXs The x of each plus button found.
 * @param screenWidth The screen width.
 * @returns The empty slots, in order.
 */
internal fun emptyLegacySlots(plusXs: List<Double>, screenWidth: Int): List<Int> = plusXs.map { legacySlotOf(it, screenWidth) }.distinct().sorted()

/**
 * Tells which legacy slot a point on Legacy Select belongs to.
 *
 * @param x The point's x.
 * @param screenWidth The screen width.
 * @returns 1 for the left slot, 2 for the right one.
 */
internal fun legacySlotOf(x: Double, screenWidth: Int): Int = if (x < screenWidth / 2) 1 else 2

/**
 * Decides whether to tick Event Boost on Final Confirmation, which doubles the TP a career costs.
 *
 * @param tp The current TP, or null when unreadable.
 * @param spend The TP the dialog says it will spend, or null when unreadable.
 * @param bTicked Whether Event Boost is ticked, in which case [spend] is already doubled.
 * @returns True when the TP covers double the normal cost.
 */
internal fun wantsEventBoost(tp: Int?, spend: Int?, bTicked: Boolean): Boolean {
    if (tp == null || spend == null) return false
    return tp >= if (bTicked) spend else spend * 2
}

/**
 * Reads the Training screen's timer.
 *
 * @param text The OCR text, such as "0:49:38 left".
 * @returns The seconds left, or null when no timer could be read.
 */
internal fun parseTimeLeft(text: String): Int? {
    // OCR reads the leading 0 as the letter O.
    val (h, m, sec) = TIME_LEFT.find(text.replace('O', '0').replace('o', '0'))?.destructured ?: return null
    val minutes = m.toInt()
    val seconds = sec.toInt()
    if (minutes > 59 || seconds > 59) return null
    return h.toInt() * 3600 + minutes * 60 + seconds
}

/**
 * How long to wait before looking at the Training screen again: until the timer ends, but never longer than the poll interval, so a dialog such as
 * Date Changed is noticed.
 *
 * @param secondsLeft The timer in seconds, or null when it could not be read.
 * @param pollMinutes The longest wait in minutes.
 * @returns The wait in seconds.
 */
internal fun trainingWaitSeconds(secondsLeft: Int?, pollMinutes: Int): Int {
    val poll = pollMinutes * 60
    return if (secondsLeft == null) poll else minOf(secondsLeft + TRAINING_END_BUFFER_SECONDS, poll)
}

/**
 * Whether an agenda header read on My Agendas names the wanted agenda, ignoring case. The header crop cuts long titles off, so a long enough read also
 * matches the start of the title.
 *
 * @param header The header text read by OCR.
 * @param wanted The agenda title from the settings.
 * @returns True if the header is the wanted agenda.
 */
internal fun matchesAgendaTitle(header: String, wanted: String): Boolean {
    // OCR reads the 1 in "Agenda 1" as I or l.
    val read = header.trim().lowercase().replace(Regex("^agenda [il]$"), "agenda 1")
    val title = wanted.trim().lowercase()
    if (read.isEmpty() || title.isEmpty()) return false
    return read == title || (read.length >= MIN_AGENDA_PREFIX && title.startsWith(read))
}

// //////////////////////////////////////////////////////////////////////////////////////////////////
// //////////////////////////////////////////////////////////////////////////////////////////////////
// Settings

/** The Career Setup settings and catalogs, read once when the setup starts. */
data class CareerSetupSettings(
    /** The scenario to pick on Scenario Select, as stored in `general.scenario`. */
    val scenario: String,
    /** True to keep whichever trainee the game has selected. */
    val usePreselectedTrainee: Boolean,
    /** The trainee outfit's card id, or empty when none is chosen. */
    val trainee: String,
    /** "keep" to keep the last parents (Smart for an empty slot), "smart" to pick by the outfit's derived sparks, or "custom" to pick by [sparkWishList]. */
    val legacyMode: String,
    /** Spark labels in priority order for the custom legacy mode, such as "SPD" or "MILE". */
    val sparkWishList: List<String>,
    /** The support deck to use, 1-10, or 0 to keep the current one. */
    val deckNumber: Int,
    /** The friend support card's support id, or empty for the smart pick. */
    val friendCard: String,
    /** How many times to refresh the Borrow Card list before falling back. */
    val friendMaxRefreshes: Int,
    /** True to start Independent Trainings instead of normal careers and wait for each one to finish. */
    val independentTrainingLoop: Boolean,
    /** The saved agenda title to load on the Independent Training tab, or [KEEP_CURRENT_AGENDA] to leave the remembered one. */
    val independentTrainingAgenda: String,
    /** The longest wait between checks of the Training screen, in minutes. */
    val independentTrainingPollMinutes: Int,
    /** True to tick Event Boost for an Independent Training when the TP covers its doubled cost. Off leaves it unticked. */
    val independentTrainingEventBoost: Boolean,
    /** The Smart Race Solver's character preset while the solver is on, else null. */
    val solverCharacter: String?,
    /** Character names with a goal race that is always a Long race, which Smart Auto-Select ranks Stamina first for. */
    val longGoalCharacters: Set<String>,
    /** Trainee outfits keyed by card id. */
    val traineeCards: Map<String, TraineeCard>,
    /** Support cards keyed by support id. */
    val supportCards: Map<String, SupportCard>,
    /** Friend card favorites keyed by scenario. */
    val friendFavorites: Map<String, FriendFavorites>,
) {
    companion object {
        private val TAG: String = "[${MainActivity.loggerTag}]CareerSetupSettings"

        /** The wish list used when Smart Auto-Select cannot tell which outfit was picked. */
        val FALLBACK_WISH_LIST: List<String> = listOf("SPD", "STA", "PWR")

        /** The Independent Training agenda choice that leaves the game's remembered agenda in place. */
        const val KEEP_CURRENT_AGENDA = "Keep current"

        /**
         * Reads the settings and catalogs from SQLite.
         *
         * @returns The current Career Setup settings.
         */
        fun load(): CareerSetupSettings =
            CareerSetupSettings(
                scenario = SettingsHelper.getStringSetting("general", "scenario"),
                usePreselectedTrainee = SettingsHelper.getBooleanSetting("careerSetup", "usePreselectedTrainee", true),
                trainee = SettingsHelper.getStringSetting("careerSetup", "trainee"),
                legacyMode = SettingsHelper.getStringSetting("careerSetup", "legacyMode").ifEmpty { "keep" },
                sparkWishList = SettingsHelper.getStringArraySetting("careerSetup", "sparkWishList", FALLBACK_WISH_LIST),
                deckNumber = SettingsHelper.getIntSetting("careerSetup", "deckNumber", 0),
                friendCard = SettingsHelper.getStringSetting("careerSetup", "friendCard"),
                friendMaxRefreshes = SettingsHelper.getIntSetting("careerSetup", "friendMaxRefreshes", 3),
                independentTrainingLoop = SettingsHelper.getBooleanSetting("careerSetup", "independentTrainingLoop", false),
                // A custom title replaces the picked agenda's default title, the same way the Racing page's in-game agenda works.
                independentTrainingAgenda =
                    SettingsHelper.getStringSetting("careerSetup", "independentTrainingAgendaTitle").trim().ifEmpty {
                        SettingsHelper.getStringSetting("careerSetup", "independentTrainingAgenda").ifEmpty { KEEP_CURRENT_AGENDA }
                    },
                independentTrainingPollMinutes = SettingsHelper.getIntSetting("careerSetup", "independentTrainingPollMinutes", 5).coerceIn(1, 15),
                independentTrainingEventBoost = SettingsHelper.getBooleanSetting("careerSetup", "independentTrainingEventBoost", false),
                solverCharacter =
                    SettingsHelper.getStringSetting("racing", "smartRaceSolverCharacterPreset").takeIf {
                        it.isNotEmpty() &&
                            SettingsHelper.getBooleanSetting("racing", "enableSmartRaceSolver")
                    },
                longGoalCharacters = parseLongGoalCharacters(),
                traineeCards =
                    parseCatalog("traineeCardData") { id, o ->
                        TraineeCard(id, o.getString("name"), o.getString("title"), o.getJSONObject("aptitudes").toStringMap(), o.getJSONObject("growth").toIntMap())
                    },
                supportCards = parseCatalog("supportCardData") { id, o -> SupportCard(id, o.getString("name"), o.getString("title"), o.getString("type"), o.getString("rarity")) },
                friendFavorites = parseCatalog("friendFavoritesData") { _, o -> FriendFavorites(o.getJSONArray("favorites").toStringList(), o.getJSONArray("types").toStringList()) },
            )

        /**
         * Finds the characters with a goal race that is always a Long race. A choice between races only counts when every option is Long.
         *
         * @returns The character names, or an empty set if the objectives are missing or unreadable.
         */
        private fun parseLongGoalCharacters(): Set<String> =
            runCatching {
                MandatoryRaces.parse(SettingsHelper.getStringSetting("racing", "characterObjectivesData").ifEmpty { "{}" })
                    .filterValues { races -> races.any { race -> race.options.isNotEmpty() && race.options.all { it.distanceType == TrackDistance.LONG } } }
                    .keys
            }.getOrElse {
                MessageLog.e(TAG, "[ERROR] Could not read the character objectives for Career Setup: ${it.message}")
                emptySet()
            }

        /**
         * Parses one catalog that the frontend saved at startup.
         *
         * @param key The `careerSetup` setting key holding the catalog JSON.
         * @param build Builds one entry from its key and JSON object.
         * @returns The catalog, or an empty map if it is missing or unreadable.
         */
        private fun <T> parseCatalog(key: String, build: (String, JSONObject) -> T): Map<String, T> =
            runCatching {
                val json = JSONObject(SettingsHelper.getStringSetting("careerSetup", key).ifEmpty { "{}" })
                json.keys().asSequence().associateWith { build(it, json.getJSONObject(it)) }
            }.getOrElse {
                MessageLog.e(TAG, "[ERROR] Could not read the Career Setup catalog $key: ${it.message}")
                emptyMap()
            }

        /**
         * Reads a JSON object of strings.
         *
         * @returns The object as a map.
         */
        private fun JSONObject.toStringMap(): Map<String, String> = keys().asSequence().associateWith { getString(it) }

        /**
         * Reads a JSON object of numbers.
         *
         * @returns The object as a map.
         */
        private fun JSONObject.toIntMap(): Map<String, Int> = keys().asSequence().associateWith { getInt(it) }

        /**
         * Reads a JSON array of strings.
         *
         * @returns The array as a list.
         */
        private fun JSONArray.toStringList(): List<String> = (0 until length()).map { getString(it) }
    }
}

/**
 * Starts a new career from the Home screen: scenario, trainee, legacy parents, support deck, friend card, Final Confirmation, the opening
 * narration, Quick Mode, and the story skip speed. It then hands back to the normal career loop.
 *
 * Each tick reads which setup screen is showing and runs that step, so it picks up from wherever the game is.
 *
 * @property game Reference to the bot's [Game] instance.
 * @property bDryRun True to stop on Final Confirmation instead of starting the career, as the Career Setup Test does to test the setup without spending TP.
 */
class CareerSetup(private val game: Game, private val bDryRun: Boolean = false) {
    companion object {
        private val TAG: String = "[${MainActivity.loggerTag}]CareerSetup"

        /**
         * Whether the game is outside a career, on Home or one of the career setup screens.
         *
         * @param game Reference to the bot's [Game] instance.
         * @param bitmap The screenshot to check.
         * @returns True when the bottom menu bar, the Final Confirmation dialog, or a running Independent Training's Training screen is showing.
         */
        fun isOutsideCareer(game: Game, bitmap: Bitmap): Boolean =
            ButtonMenuBarHome.check(game.imageUtils, sourceBitmap = bitmap) ||
                DialogUtils.getDialogOrNull(game.imageUtils, bitmap)?.name == DialogFinalConfirmation.name ||
                LabelCareerStartIndependentTrainingTimeLeft.check(game.imageUtils, sourceBitmap = bitmap)
    }

    private val settings = CareerSetupSettings.load()

    /** Whether the legacy parents were picked during this setup. */
    private var bLegacyDone = false

    /** Whether the support deck was set during this setup. */
    private var bDeckDone = false

    /** Whether Start Career! was tapped on Final Confirmation. */
    private var bStartTapped = false

    /** How many cards of each type the deck holds, read before the friend slot is opened. */
    private var deckCounts: Map<String, Int> = emptyMap()

    /** The outfit chosen in settings, or null when the preselected trainee is kept. */
    private val wantedOutfit: TraineeCard? = settings.trainee.takeIf { !settings.usePreselectedTrainee }?.let { settings.traineeCards[it] }

    /** The outfit the career starts with, read on Trainee Select. Smart Auto-Select derives its sparks from it. */
    private var selectedOutfit: TraineeCard? = wantedOutfit

    /** The Trainee Select header's center, which the selected outfit's name lines are measured from. It never moves, so it is found once. */
    private var traineeHeader: Point? = null

    /** Whether a finished Independent Training is being claimed, which ends on its end-of-career screen instead of starting a career. */
    private var bClaimingIndependentTraining = false

    /** Whether the agenda was loaded on the Independent Training tab during this setup. */
    private var bAgendaDone = false

    /** The Smart sparks for the outfit picked on Trainee Select, worked out once when the first parent is picked. */
    private val smartWishList: List<String> by lazy {
        val outfit = selectedOutfit
        if (outfit == null) {
            MessageLog.w(TAG, "[CAREER SETUP] Could not tell which outfit was picked, so Smart Auto-Select uses ${CareerSetupSettings.FALLBACK_WISH_LIST.joinToString(", ")}.")
            CareerSetupSettings.FALLBACK_WISH_LIST
        } else {
            SmartSparks.wishList(outfit.aptitudes, outfit.growth, outfit.name in settings.longGoalCharacters).also {
                MessageLog.i(TAG, "[CAREER SETUP] Smart sparks for ${outfit.label}: ${it.joinToString(", ")}.")
            }
        }
    }

    /**
     * Runs the setup until the career has started and the story skip is at full speed. A finished Independent Training found on the way is
     * claimed instead, and the setup ends on its end-of-career screen so the career loop can complete it.
     *
     * @returns True if a finished Independent Training was claimed instead of starting a career.
     * @throws CampaignBreakpointException When a step cannot be completed, so the run stops with the reason.
     */
    fun run(): Boolean {
        MessageLog.i(TAG, "[CAREER SETUP] Starting a new ${settings.scenario} career.")
        MessageLog.i(TAG, "[CAREER SETUP] Plan: ${describePlan()}.")
        var ticks = 0
        while (ticks < MAX_TICKS) {
            ticks++
            val bitmap = game.imageUtils.getSourceBitmap()
            val dialog = DialogUtils.getDialogOrNull(game.imageUtils, bitmap)
            if (dialog != null) {
                handleDialog(dialog)
            } else if (LabelCareerStartIndependentTrainingTimeLeft.check(game.imageUtils, sourceBitmap = bitmap)) {
                waitForIndependentTraining(bitmap)
                // Its results show on this screen when the timer ends, and are claimed like one finished from the Career dialog.
                bClaimingIndependentTraining = true
                // The wait is progress, so it never counts toward giving up on the setup.
                ticks = 0
            } else if (bClaimingIndependentTraining && ButtonCompleteCareer.check(game.imageUtils, sourceBitmap = bitmap)) {
                MessageLog.i(TAG, "[CAREER SETUP] Claimed the Independent Training. Handing its end-of-career screen to the career loop.")
                return true
            } else if (bClaimingIndependentTraining && ButtonOk.click(game.imageUtils, sourceBitmap = bitmap)) {
                // The Training Log shows the results first.
                game.wait(2.0)
            } else if (ButtonCareerStartStorySkipTwo.check(game.imageUtils, sourceBitmap = bitmap)) {
                MessageLog.i(TAG, "[CAREER SETUP] Career started with the story skip at full speed. Handing over to the career loop.")
                return false
            } else if (ButtonCareerStartStorySkipOff.check(game.imageUtils, sourceBitmap = bitmap) || ButtonCareerStartStorySkipOne.check(game.imageUtils, sourceBitmap = bitmap)) {
                stepUpStorySkip()
            } else if (bStartTapped && ButtonSkip.click(game.imageUtils, sourceBitmap = bitmap)) {
                MessageLog.i(TAG, "[CAREER SETUP] Skipping the opening narration.")
                game.wait(2.0)
            } else if (ButtonHomeFullStats.check(game.imageUtils, sourceBitmap = bitmap)) {
                MessageLog.i(TAG, "[CAREER SETUP] Already inside a career. Handing over to the career loop.")
                return false
            } else if (LabelCareerStartSupportFormationHeader.check(game.imageUtils, sourceBitmap = bitmap)) {
                handleSupportFormation(bitmap)
            } else if (LabelCareerStartLegacySelectHeader.check(game.imageUtils, sourceBitmap = bitmap)) {
                handleLegacySelect(bitmap)
            } else if (LabelCareerStartTraineeSelectHeader.check(game.imageUtils, sourceBitmap = bitmap)) {
                handleTraineeSelect()
            } else if (LabelCareerStartScenarioSelectHeader.check(game.imageUtils, sourceBitmap = bitmap)) {
                handleScenarioSelect(bitmap)
            } else if (ButtonCareerStartHomeCareer.click(game.imageUtils, sourceBitmap = bitmap)) {
                MessageLog.i(TAG, "[CAREER SETUP] Opening Career from the Home screen.")
                game.wait(2.0)
            } else if (!bStartTapped && ButtonMenuBarHome.click(game.imageUtils, sourceBitmap = bitmap)) {
                game.wait(2.0)
            } else {
                game.wait(1.0)
            }
        }
        throw CampaignBreakpointException("Career Setup did not finish. Stopping bot...")
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // Steps

    /**
     * Checks the TP, then taps the right arrow on Scenario Select until the description names the chosen scenario and taps Next.
     *
     * @param bitmap The screenshot of Scenario Select.
     */
    private fun handleScenarioSelect(bitmap: Bitmap) {
        checkTp(bitmap)
        val keyword = SCENARIO_KEYWORDS[settings.scenario] ?: throw CampaignBreakpointException("Career Setup does not know the scenario \"${settings.scenario}\".")
        val next = anchorOf(ButtonNext, game.imageUtils.getSourceBitmap())
        // The description box is centered on screen while Next is pinned to the bottom, so on a tall screen the box sits farther above Next.
        val description = ScreenLayout.region(SCENARIO_DESCRIPTION, Anchor.CENTER, relativeTo = Anchor.BOTTOM)
        repeat(MAX_CAROUSEL_TAPS) {
            val text = ocr(game.imageUtils.getSourceBitmap(), next, description, "scenarioDescription").lowercase().filter { it.isLetter() }
            if (text.contains(keyword)) {
                MessageLog.i(TAG, "[CAREER SETUP] Selected the ${settings.scenario} scenario.")
                ButtonNext.click(game.imageUtils, tries = 5)
                game.wait(2.0)
                return
            }
            ButtonCareerStartArrowRight.click(game.imageUtils, tries = 3)
            game.wait(1.5)
        }
        throw CampaignBreakpointException("Could not find the ${settings.scenario} scenario on Scenario Select.")
    }

    /**
     * Reads the TP and looks for the TP Cost Halved event on Scenario Select. Stops the bot before the rest of the setup when TP cannot pay for the career.
     * A failed read carries on and leaves it to the game's restore prompt. The Career Setup Test spends no TP, so it only logs.
     *
     * @param bitmap The screenshot of Scenario Select.
     */
    private fun checkTp(bitmap: Bitmap) {
        val tp = parseCounter(ocr(bitmap, anchorOf(LabelCareerStartScenarioSelectHeader, bitmap), SCENARIO_TP_COUNTER, "scenarioTp"))?.first
        // The event icon only decides whether to stop when TP is under the full cost, so look again there in case it was still fading in.
        val bShortOfFullCost = tp != null && tp < CAREER_TP_COST
        val bHalved = IconCareerStartTpCostHalved.check(game.imageUtils, sourceBitmap = bitmap) || (bShortOfFullCost && IconCareerStartTpCostHalved.check(game.imageUtils, tries = 3))
        val cost = if (bHalved) CAREER_TP_COST / 2 else CAREER_TP_COST
        val event = if (bHalved) " (TP Cost Halved event is on)" else ""
        MessageLog.i(TAG, "[CAREER SETUP] TP is ${tp ?: "unreadable"}. A career costs $cost TP$event.")
        if (tp != null && tp < cost && !bDryRun) throw CampaignBreakpointException("Out of TP to start the next career ($tp of $cost). Stopping bot...")
    }

    /** Picks the trainee on Trainee Select, or keeps the preselected one, then taps Next. */
    private fun handleTraineeSelect() {
        val current = readSelectedOutfit()
        selectedOutfit =
            if (settings.usePreselectedTrainee) {
                current ?: selectedOutfit
            } else {
                val wanted = wantedOutfit ?: throw CampaignBreakpointException("No trainee is set in Career Setup. Pick one or turn on \"Use Preselected Trainee\".")
                current?.takeIf { it.id == wanted.id } ?: findTrainee(wanted) ?: throw CampaignBreakpointException("Could not find ${wanted.label} on Trainee Select.")
            }
        MessageLog.i(TAG, "[CAREER SETUP] Trainee chosen: ${selectedOutfit?.label ?: "an outfit not in the catalog"}.")
        // The solver plans races from its own character preset, so a different trainee would race to the wrong objectives and aptitudes.
        CareerCatalog.solverMismatch(selectedOutfit, settings.solverCharacter)?.let {
            throw CampaignBreakpointException(
                "${selectedOutfit?.label} does not match the Smart Race Solver's character ($it). Set the solver's character on the Schedule page or turn the solver off.",
            )
        }
        ButtonNext.click(game.imageUtils, tries = 5)
        game.wait(2.0)
    }

    /**
     * Picks both legacy parents on the overview, or backs out of a picker the bot did not open.
     *
     * @param bitmap The current screenshot.
     */
    private fun handleLegacySelect(bitmap: Bitmap) {
        if (ButtonConfirm.check(game.imageUtils, sourceBitmap = bitmap)) {
            // A picker left open from before this setup started. Back out so the slots are filled in order.
            ButtonBack.click(game.imageUtils, sourceBitmap = bitmap)
            game.wait(1.5)
            return
        }
        if (!ButtonAutoSelect.check(game.imageUtils, sourceBitmap = bitmap)) {
            game.wait(1.0)
            return
        }
        if (!bLegacyDone) {
            bLegacyDone = true
            if (settings.legacyMode == "keep") {
                if (!keepLastParents(bitmap)) return
            } else {
                // Clear both slots first so neither picker shows the other slot's parent as taken.
                ButtonCareerStartLegacyReset.click(game.imageUtils, sourceBitmap = bitmap)
                game.wait(1.5)
                val wanted = if (settings.legacyMode == "custom") settings.sparkWishList else smartWishList
                val bSkipGuests = pickParent(1, skipGuests = false, wanted)
                pickParent(2, skipGuests = bSkipGuests, wanted)
            }
        }
        ButtonNext.click(game.imageUtils, tries = 5)
        game.wait(2.0)
    }

    /**
     * Sets the deck, opens the friend slot when it is empty, and taps Start Career! once a friend is borrowed.
     *
     * @param bitmap The current screenshot.
     */
    private fun handleSupportFormation(bitmap: Bitmap) {
        if (!bDeckDone) {
            selectDeck()
            bDeckDone = true
            return
        }
        val borrow = ButtonBorrowSupportCard.findImageWithBitmap(game.imageUtils, bitmap)
        if (borrow != null) {
            deckCounts = readDeckCounts(bitmap, borrow)
            ButtonBorrowSupportCard.click(game.imageUtils, sourceBitmap = bitmap)
            game.wait(1.5)
            return
        }
        if (ButtonStartCareer.click(game.imageUtils, sourceBitmap = bitmap)) game.wait(1.5)
    }

    /** Claims a finished Independent Training from its dialog, or stops while it is still running. */
    private fun handleIndependentTraining() {
        if (!LabelCareerStartIndependentTrainingComplete.check(game.imageUtils)) {
            DialogIndependentTraining.close(game.imageUtils, tries = 3)
            throw CampaignBreakpointException("An Independent Training is still running. Start the bot again once it finishes.")
        }
        // Claiming completes that career, which cannot be undone, so a dry run stops here.
        if (bDryRun) throw CampaignBreakpointException("Career Setup Test: an Independent Training is finished and ready to claim. Stopping before claiming it.")
        MessageLog.i(TAG, "[CAREER SETUP] The Independent Training is complete. Claiming its results.")
        bClaimingIndependentTraining = true
        DialogIndependentTraining.ok(game.imageUtils, tries = 3)
        game.wait(3.0)
    }

    /**
     * Handles the dialogs owned by the setup and passes every other dialog to the task.
     *
     * @param dialog The dialog on screen.
     */
    private fun handleDialog(dialog: DialogInterface) {
        when (dialog.name) {
            DialogFinalConfirmation.name -> handleFinalConfirmation()

            DialogBorrowCard.name -> {
                if (!FriendPicker(game, settings).pick(deckCounts)) throw CampaignBreakpointException("Could not borrow a friend support card.")
                game.wait(1.5)
            }

            DialogConfirmAutoSelect.name -> confirmAutoSelect()

            DialogIndependentTraining.name -> handleIndependentTraining()

            // The daily reset sends the game back to Home with login rewards and announcements the setup does not handle. The training stays claimable.
            DialogDateChanged.name -> {
                if (!settings.independentTrainingLoop) {
                    DialogDateChanged.ok(game.imageUtils)
                } else {
                    throw CampaignBreakpointException("Daily reset (Date Changed) during the Independent Training loop. Start the bot again from Home to claim the training. Stopping bot...")
                }
            }

            // Starting a career with too little TP asks to restore it, which shares its title and buttons with the RP prompt. TP is never restored.
            DialogConfirmRestoreRP.name -> {
                ButtonNo.click(game.imageUtils, tries = 3)
                throw CampaignBreakpointException("Out of TP to start the next career. Stopping bot...")
            }

            else -> {
                val result = game.task.handleDialogs(dialog)
                if (result is DialogHandlerResult.Unhandled) {
                    MessageLog.i(TAG, "[CAREER SETUP] Closing the \"${dialog.name}\" dialog.")
                    dialog.close(game.imageUtils)
                }
            }
        }
        game.wait(1.0)
    }

    /** Makes sure the Normal Career tab is active, then taps Start Career! and checks the career actually starts. */
    private fun handleFinalConfirmation() {
        if (settings.independentTrainingLoop) {
            startIndependentTraining()
            return
        }
        if (ButtonCareerStartIndependentStart.check(game.imageUtils) && !ButtonStartCareer.check(game.imageUtils)) {
            MessageLog.i(TAG, "[CAREER SETUP] Switching Final Confirmation to the Normal Career tab.")
            tapAnchored(game, anchorOf(ButtonCancel, game.imageUtils.getSourceBitmap()), NORMAL_CAREER_TAB_DX, NORMAL_CAREER_TAB_DY)
            game.wait(1.0)
        }
        setEventBoost()
        if (bDryRun) {
            if (!ButtonStartCareer.check(game.imageUtils, tries = 5)) throw CampaignBreakpointException("Could not find Start Career! on Final Confirmation.")
            throw CampaignBreakpointException("Career Setup Test reached Final Confirmation on the Normal Career tab. Stopping before Start Career!.")
        }
        if (!ButtonStartCareer.click(game.imageUtils, tries = 5)) throw CampaignBreakpointException("Could not find Start Career! on Final Confirmation.")
        MessageLog.i(TAG, "[CAREER SETUP] Tapped Start Career!.")
        bStartTapped = true
        game.wait(3.0)
    }

    /**
     * Waits on the Training screen of a running Independent Training, until its timer ends or for at most the poll interval. Stop and pause still end
     * the wait at once.
     *
     * @param bitmap The screenshot of the Training screen.
     * @throws CampaignBreakpointException When the loop is off, since nothing would start the next training.
     */
    private fun waitForIndependentTraining(bitmap: Bitmap) {
        if (!settings.independentTrainingLoop) {
            throw CampaignBreakpointException("An Independent Training is running. Turn on Independent Training Loop to wait for it, or start the bot again once it finishes.")
        }
        val text = ocr(bitmap, anchorOf(LabelCareerStartIndependentTrainingTimeLeft, bitmap), TRAINING_TIME_LEFT, "trainingTimeLeft")
        val secondsLeft = parseTimeLeft(text)
        val waitSeconds = trainingWaitSeconds(secondsLeft, settings.independentTrainingPollMinutes)
        val left = secondsLeft?.let { "%d:%02d:%02d".format(it / 3600, it / 60 % 60, it % 60) } ?: "unreadable (\"$text\")"
        MessageLog.i(TAG, "[CAREER SETUP] Independent Training time left: $left. Checking again in ${waitSeconds / 60}m ${waitSeconds % 60}s.")
        game.wait(waitSeconds.toDouble(), skipWaitingForLoading = true)
    }

    /** Switches Final Confirmation to the Independent Training tab, loads the agenda, then taps Start!. */
    private fun startIndependentTraining() {
        if (!ButtonCareerStartIndependentStart.check(game.imageUtils)) {
            MessageLog.i(TAG, "[CAREER SETUP] Switching Final Confirmation to the Independent Training tab.")
            tapAnchored(game, anchorOf(ButtonCancel, game.imageUtils.getSourceBitmap()), INDEPENDENT_TAB_DX, INDEPENDENT_TAB_DY)
            game.wait(1.0)
            if (!ButtonCareerStartIndependentStart.check(game.imageUtils, tries = 3)) throw CampaignBreakpointException("Could not open the Independent Training tab on Final Confirmation.")
        }
        if (!bAgendaDone) {
            loadIndependentAgenda()
            bAgendaDone = true
        }
        setEventBoost(bAllowBoost = settings.independentTrainingEventBoost)
        if (bDryRun) throw CampaignBreakpointException("Career Setup Test reached Final Confirmation on the Independent Training tab. Stopping before Start!.")
        if (!ButtonCareerStartIndependentStart.click(game.imageUtils, tries = 5)) throw CampaignBreakpointException("Could not find Start! on Final Confirmation.")
        MessageLog.i(TAG, "[CAREER SETUP] Tapped Start! for an Independent Training.")
        game.wait(3.0)
    }

    /**
     * Loads the agenda from the settings on the Independent Training tab: Edit, My Agendas, the agenda's Load List, then back to Final Confirmation.
     * Keep current leaves the agenda the game remembers.
     *
     * @throws CampaignBreakpointException When the agenda cannot be found, so no training starts with the wrong agenda.
     */
    private fun loadIndependentAgenda() {
        val wanted = settings.independentTrainingAgenda
        if (wanted == CareerSetupSettings.KEEP_CURRENT_AGENDA) {
            MessageLog.i(TAG, "[CAREER SETUP] Keeping the agenda the game remembers.")
            return
        }
        MessageLog.i(TAG, "[CAREER SETUP] Loading the \"$wanted\" agenda.")
        if (!ButtonCareerStartIndependentAgendaEdit.click(game.imageUtils, tries = 3)) throw CampaignBreakpointException("Could not find the agenda's Edit button on the Independent Training tab.")
        game.wait(1.5)
        if (!ButtonMyAgendas.click(game.imageUtils, tries = 3)) throw CampaignBreakpointException("Could not find My Agendas on the Agenda screen.")
        game.wait(1.5)
        val bLoaded = findAndLoadAgenda(wanted)
        closeAgendaScreens()
        if (!bLoaded) throw CampaignBreakpointException("Could not find the \"$wanted\" agenda in My Agendas. Check the Independent Training agenda in Career Setup.")
        MessageLog.i(TAG, "[CAREER SETUP] Loaded the \"$wanted\" agenda.")
    }

    /**
     * Scrolls My Agendas until a card's header names the wanted agenda, then taps its Load List and accepts the Overwrite prompt. Overwrite only replaces
     * the schedule being edited, never a saved agenda. Save Here is never tapped.
     *
     * @param wanted The agenda title from the settings.
     * @returns True if the agenda was loaded, false if the list ended without it.
     */
    private fun findAndLoadAgenda(wanted: String): Boolean {
        var lastHeaders: List<String>? = null
        repeat(MAX_AGENDA_SCROLLS) { attempt ->
            val bitmap = game.imageUtils.getSourceBitmap()
            val buttons = ButtonRaceAgendaLoadList.findAll(game.imageUtils, sourceBitmap = bitmap)
            if (buttons.isEmpty()) {
                MessageLog.w(TAG, "[CAREER SETUP] No Load List buttons on My Agendas.")
                return false
            }
            val headers = buttons.associateWith { ocr(bitmap, it, AGENDA_HEADER, "agendaHeader").trim() }
            val match = headers.entries.firstOrNull { matchesAgendaTitle(it.value, wanted) }
            if (match != null) {
                MessageLog.i(TAG, "[CAREER SETUP] Found \"${match.value}\". Tapping its Load List.")
                game.gestureUtils.tap(match.key.x, match.key.y, ButtonRaceAgendaLoadList.template.path)
                game.wait(1.5)
                // A schedule that already has races asks before it is replaced. An empty one loads straight away.
                if (ButtonOverwrite.click(game.imageUtils, tries = 3)) {
                    MessageLog.i(TAG, "[CAREER SETUP] Overwrote the current schedule.")
                    game.wait(1.5)
                }
                return true
            }
            // The same headers after a scroll mean the list is at its end.
            val shown = headers.values.toList()
            if (shown == lastHeaders) return false
            lastHeaders = shown
            MessageLog.i(TAG, "[CAREER SETUP] \"$wanted\" is not among ${shown.joinToString(", ") { "\"$it\"" }}. Scrolling My Agendas (${attempt + 1}/$MAX_AGENDA_SCROLLS).")
            // Drag slowly from the lowest card so the list moves about one card. A fast swipe flings it past cards it never reads.
            val from = buttons.maxBy { it.y }
            game.gestureUtils.swipe(from.x.toFloat(), from.y.toFloat(), from.x.toFloat(), (from.y - AGENDA_SCROLL_DY).toFloat(), duration = 1500L)
            game.wait(1.0)
        }
        return false
    }

    /**
     * Closes My Agendas and the Agenda screen until Final Confirmation shows again. After a load the game closes My Agendas by itself.
     *
     * @throws CampaignBreakpointException When Final Confirmation does not come back.
     */
    private fun closeAgendaScreens() {
        repeat(5) {
            when (DialogUtils.getDialogOrNull(game.imageUtils)?.name) {
                DialogFinalConfirmation.name -> return
                // Only left open if tapping Overwrite failed, so the old schedule stays.
                DialogOverwrite.name -> ButtonCancel.click(game.imageUtils, tries = 3)
                // My Agendas, the Agenda screen, or a title read slightly wrong. Save Here is never near Close, so Close is safe on all of them.
                else -> ButtonClose.click(game.imageUtils, tries = 3)
            }
            game.wait(1.5)
        }
        throw CampaignBreakpointException("Could not get back to Final Confirmation after loading the agenda.")
    }

    /**
     * Ticks Event Boost on Final Confirmation when the TP covers its doubled cost, and unticks it otherwise so the career can still start.
     * Nothing happens when no boost event runs, since the dialog then has no checkbox.
     *
     * @param bAllowBoost False to always leave Event Boost unticked.
     */
    private fun setEventBoost(bAllowBoost: Boolean = true) {
        val bitmap = game.imageUtils.getSourceBitmap()
        val checkbox = Checkbox.findImageWithBitmap(game.imageUtils, bitmap) ?: return
        val bTicked = Checkbox.isTicked(game.imageUtils, bitmap, checkbox)
        val cancel = anchorOf(ButtonCancel, bitmap)
        val spend = ocrInt(bitmap, cancel, FINAL_CONFIRMATION_SPEND_LINE, "finalConfirmationSpend")
        val tp = ocrInt(bitmap, cancel, FINAL_CONFIRMATION_CURRENT_TP, "finalConfirmationTp")
        val bWanted = bAllowBoost && wantsEventBoost(tp, spend, bTicked)
        MessageLog.i(TAG, "[CAREER SETUP] Event Boost ${if (bWanted) "on" else "off"} (TP ${tp ?: "unreadable"}, spend ${spend ?: "unreadable"}${if (bTicked) " while ticked" else ""}).")
        if (bWanted != bTicked) {
            game.tap(checkbox.x, checkbox.y)
            game.wait(0.5)
        }
    }

    /** Accepts the Confirm Auto-Select dialog with "Include guests" unticked, since a guest the game picks may cost monies. */
    private fun confirmAutoSelect() {
        val bitmap = game.imageUtils.getSourceBitmap()
        val checkbox = Checkbox.findImageWithBitmap(game.imageUtils, bitmap)
        if (checkbox != null && Checkbox.isTicked(game.imageUtils, bitmap, checkbox)) {
            MessageLog.i(TAG, "[CAREER SETUP] Unticking \"Include guests\" so Auto-Select never pays for a borrow.")
            game.tap(checkbox.x, checkbox.y)
            game.wait(0.5)
        }
        DialogConfirmAutoSelect.ok(game.imageUtils)
    }

    /**
     * Summarizes what the setup will do, so a pick the user did not expect can be traced to its setting.
     *
     * @returns The plan, such as "keep the selected trainee, keep the last parents, ...".
     */
    private fun describePlan(): String {
        val trainee = if (settings.usePreselectedTrainee) "keep the selected trainee" else "pick ${wantedOutfit?.label ?: "no trainee set"}"
        val parents =
            when (settings.legacyMode) {
                "smart" -> "pick parents by the trainee's smart sparks"
                "custom" -> "pick parents by sparks (${settings.sparkWishList.joinToString(", ")})"
                else -> "keep the last parents (Smart pick for an empty slot)"
            }
        val deck = if (settings.deckNumber in 1..10) "use Deck ${settings.deckNumber}" else "keep the current deck"
        val friend = settings.supportCards[settings.friendCard]?.let { "borrow ${it.label}" } ?: "borrow a smart friend card"
        val start =
            if (settings.independentTrainingLoop) {
                val agenda = if (settings.independentTrainingAgenda == CareerSetupSettings.KEEP_CURRENT_AGENDA) "the remembered agenda" else "the \"${settings.independentTrainingAgenda}\" agenda"
                ", then start an Independent Training with $agenda"
            } else {
                ""
            }
        return "$trainee, $parents, $deck, $friend$start"
    }

    /** Taps the story skip button once and lets its animation finish, moving it one step toward full speed. */
    private fun stepUpStorySkip() {
        MessageLog.i(TAG, "[CAREER SETUP] Raising the story skip speed.")
        ButtonCareerStartStorySkipOff.click(game.imageUtils) || ButtonCareerStartStorySkipOne.click(game.imageUtils)
        game.wait(1.0, skipWaitingForLoading = true)
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // Helpers

    /**
     * Keeps the last legacy parents. An empty slot shows the plus button, for example once the guest borrows run out, and only that slot gets a
     * Smart pick while the other parent stays.
     *
     * @param bitmap The current screenshot.
     * @returns False if the game's Auto-Select was used instead, so Next waits for the next tick.
     */
    private fun keepLastParents(bitmap: Bitmap): Boolean {
        val emptySlots = emptyLegacySlots(ButtonSelectLegacy.findAll(game.imageUtils, sourceBitmap = bitmap).map { it.x }, game.imageUtils.displayWidth)
        if (emptySlots.isNotEmpty()) {
            MessageLog.i(TAG, "[CAREER SETUP] Legacy slot ${emptySlots.joinToString(" and ")} is empty, so it gets a Smart pick and the last parents otherwise stay.")
            // The kept parent may already be the guest borrow, so a single empty slot skips the Guests tab.
            var bSkipGuests = emptySlots.size == 1
            for (slot in emptySlots) bSkipGuests = pickParent(slot, bSkipGuests, smartWishList)
        } else if (ButtonNext.checkDisabled(game.imageUtils, bitmap) == true) {
            MessageLog.i(TAG, "[CAREER SETUP] Next is disabled but no legacy slot looks empty. Using the game's Auto-Select.")
            ButtonAutoSelect.click(game.imageUtils, sourceBitmap = bitmap)
            game.wait(1.5)
            return false
        } else {
            MessageLog.i(TAG, "[CAREER SETUP] Keeping the last legacy parents.")
        }
        return true
    }

    /**
     * Opens a Legacy slot and picks its parent.
     *
     * @param slot 1 for the left slot, 2 for the right one.
     * @param skipGuests True to skip the Guests tab, since the other slot holds a guest or found none free.
     * @param wishList The sparks to score the parents against, most wanted first.
     * @returns True if the next slot should skip the Guests tab: a guest was taken, or none was free to borrow.
     * @throws CampaignBreakpointException When no parent could be picked.
     */
    private fun pickParent(slot: Int, skipGuests: Boolean, wishList: List<String>): Boolean {
        MessageLog.i(TAG, "[CAREER SETUP] Picking legacy parent $slot.")
        // An empty slot shows a large plus button. Tapping the "Select a Legacy" text beside it does nothing.
        val plus =
            ButtonSelectLegacy.findAll(game.imageUtils).firstOrNull { legacySlotOf(it.x, game.imageUtils.displayWidth) == slot }
                ?: throw CampaignBreakpointException("Legacy $slot has no plus button to open its picker.")
        game.tap(plus.x, plus.y)
        game.wait(1.5)
        game.waitForAny(ButtonConfirm, timeoutSec = 10.0, bHandleDialogs = false) ?: throw CampaignBreakpointException("The Legacy $slot picker did not open.")
        val picker = LegacyPicker(game, settings, wishList)
        val bGuest = picker.pick(skipGuests) ?: throw CampaignBreakpointException("Could not pick legacy parent $slot.")
        game.waitForAny(ButtonAutoSelect, timeoutSec = 10.0, bHandleDialogs = false) ?: throw CampaignBreakpointException("Legacy Select did not return after picking parent $slot.")
        return bGuest || picker.noFreeGuest
    }

    /**
     * Reads which outfit is selected at the top of Trainee Select.
     *
     * @returns The outfit, or null if the text matched no outfit in the catalog.
     */
    private fun readSelectedOutfit(): TraineeCard? {
        val bitmap = game.imageUtils.getSourceBitmap()
        val header = traineeHeader ?: anchorOf(LabelCareerStartTraineeSelectHeader, bitmap).also { traineeHeader = it }
        // The trainee panel is centered on screen while the header is pinned to the top, so on a tall screen the lines sit farther below it.
        val name = ocr(bitmap, header, ScreenLayout.region(TRAINEE_NAME_LINE, Anchor.CENTER, relativeTo = Anchor.TOP), "traineeName")
        val title = ocr(bitmap, header, ScreenLayout.region(TRAINEE_OUTFIT_LINE, Anchor.CENTER, relativeTo = Anchor.TOP), "traineeOutfit")
        return CareerCatalog.match(title, name, settings.traineeCards.values).also { MessageLog.d(TAG, "[DEBUG] readSelectedOutfit:: \"$title $name\" -> ${it?.id}") }
    }

    /**
     * Finds the wanted outfit on Trainee Select. The grid is sorted by Name from whichever end is nearer the trainee. Each page's first and last
     * cards are read to decide whether to scroll up, scroll down, or search that page, so the search works from wherever the list starts.
     *
     * @param wanted The outfit to select.
     * @returns The wanted outfit once it is selected, or null if it is not owned.
     */
    private fun findTrainee(wanted: TraineeCard): TraineeCard? {
        val descending = CareerCatalog.searchDescending(wanted.name, settings.traineeCards.values.map { it.name })
        val next = sortTraineesByName(descending)
        // Like the Legacy grid, the scrollbar is painted over the cards and is not a handle.
        val list = ScrollList.create(game, bThumbIsHandle = false) ?: return null
        list.readBottom = game.imageUtils.relY(next.y, TRAINEE_GRID_READ_BOTTOM_DY)
        MessageLog.i(TAG, "[CAREER SETUP] Searching the Name-sorted trainee list ${if (descending) "descending" else "ascending"} for ${wanted.label}.")

        var lastMove: PageMove? = null
        repeat(MAX_TRAINEE_PAGES) {
            val cards = visibleTraineeCards(list)
            if (cards.isEmpty()) {
                if (!list.scrollDownTracked()) return null
                return@repeat
            }
            val first = selectTraineeCard(cards.first())
            if (first?.id == wanted.id) return wanted
            val last = if (cards.size > 1) selectTraineeCard(cards.last()) else first
            if (last?.id == wanted.id) return wanted

            // An unreadable edge card cannot steer the search, so the page is searched and the list stepped down.
            var move = if (first == null || last == null) PageMove.HERE else CareerCatalog.pageMove(first.name, last.name, wanted.name, descending)
            MessageLog.i(TAG, "[CAREER SETUP] Page shows ${first?.label ?: "an unreadable trainee"} to ${last?.label ?: "an unreadable trainee"}: ${move.name.lowercase()}.")
            if (move == PageMove.HERE) {
                if (cards.drop(1).dropLast(1).any { selectTraineeCard(it)?.id == wanted.id }) return wanted
                // Other outfits of the same trainee can carry on past either edge. Otherwise the outfit is not owned.
                move =
                    when {
                        first == null || last == null -> PageMove.DOWN
                        first.name == wanted.name && lastMove != PageMove.DOWN -> PageMove.UP
                        last.name == wanted.name && lastMove != PageMove.UP -> PageMove.DOWN
                        else -> return null
                    }
            }
            // Turning back means the wanted name falls between two pages already read, so the outfit is not owned.
            if (lastMove != null && move != lastMove) return null
            lastMove = move
            if (!(if (move == PageMove.UP) list.scrollUpTracked() else list.scrollDownTracked())) return null
        }
        return null
    }

    /**
     * Sorts the trainee grid by Name in the given direction, opening Display Settings only when the sort is not already Name.
     *
     * @param descending True for Z to A.
     * @returns The Next button's center, which the grid's sort bar is measured from.
     * @throws CampaignBreakpointException When Display Settings does not open.
     */
    private fun sortTraineesByName(descending: Boolean): Point {
        val bitmap = game.imageUtils.getSourceBitmap()
        val next = anchorOf(ButtonNext, bitmap)
        if (!ocr(bitmap, next, TRAINEE_SORT_PILL, "traineeSort").contains("Name", ignoreCase = true)) {
            MessageLog.i(TAG, "[CAREER SETUP] Sorting the trainee list by Name.")
            tapCenter(next, TRAINEE_SORT_PILL)
            game.wait(1.5)
            val dialogBitmap = game.imageUtils.getSourceBitmap()
            if (DialogUtils.getDialogOrNull(game.imageUtils, dialogBitmap)?.name != DialogDisplaySettings.name) throw CampaignBreakpointException("Display Settings did not open on Trainee Select.")
            val ok = anchorOf(ButtonOk, dialogBitmap)
            tapAnchored(game, ok, DISPLAY_SORT_TAB_DX, DISPLAY_SORT_TAB_DY)
            game.wait(0.5)
            tapAnchored(game, ok, DISPLAY_SORT_NAME_DX, DISPLAY_SORT_NAME_DY)
            game.wait(0.5)
            DialogDisplaySettings.ok(game.imageUtils, tries = 5)
            game.wait(1.5)
        }
        val isDescending = ocr(game.imageUtils.getSourceBitmap(), next, TRAINEE_ORDER_TOGGLE, "traineeOrder").contains("Desc", ignoreCase = true)
        if (isDescending != descending) {
            tapCenter(next, TRAINEE_ORDER_TOGGLE)
            game.wait(1.5)
        }
        return next
    }

    /**
     * Finds the trainee cards whose portrait is fully inside the readable part of the grid.
     *
     * @param list The trainee grid.
     * @returns Each card's "Potential Lvl" label center, in reading order.
     */
    private fun visibleTraineeCards(list: ScrollList): List<Point> {
        val portraitTop = list.listBoundingBox.y + game.imageUtils.relHeight(-TRAINEE_PORTRAIT_DY)
        return LabelCareerStartPotentialLvl.findAll(game.imageUtils, region = list.readRegion, sourceBitmap = game.imageUtils.getSourceBitmap())
            .filter { it.y.toInt() >= portraitTop }
            .sortedWith(compareBy({ it.y.toInt() / game.imageUtils.relHeight(40) }, { it.x }))
    }

    /**
     * Taps a trainee card and reads which outfit is now selected.
     *
     * @param label The card's "Potential Lvl" label center.
     * @returns The selected outfit, or null if it is not in the catalog.
     */
    private fun selectTraineeCard(label: Point): TraineeCard? {
        game.tap(label.x, label.y + game.imageUtils.relHeight(TRAINEE_PORTRAIT_DY))
        game.wait(0.8)
        return readSelectedOutfit()
    }

    /**
     * Taps the middle of a region measured at 1080x1920 from a template.
     *
     * @param anchor The template's center on screen.
     * @param offsets The region as dx, dy, width and height from the anchor.
     */
    private fun tapCenter(anchor: Point, offsets: IntArray) = tapAnchored(game, anchor, offsets[0] + offsets[2] / 2, offsets[1] + offsets[3] / 2)

    /**
     * Finds the template that a screen's positions are measured from.
     *
     * @param component The template.
     * @param bitmap The screenshot to search.
     * @returns The template's center.
     * @throws CampaignBreakpointException When the template is not on screen, since nothing on that screen can then be located.
     */
    private fun anchorOf(component: ComponentInterface, bitmap: Bitmap): Point =
        component.findImageWithBitmap(game.imageUtils, bitmap) ?: throw CampaignBreakpointException("Could not find the ${component.template.path} template to locate the rest of the screen from.")

    /**
     * Reads the "xN" count under each card type icon on Support Formation.
     *
     * @param bitmap The current screenshot.
     * @param borrow The Borrow button's center, which the counts are measured from.
     * @returns The count of each type the deck holds. Types with no count shown are left out.
     */
    private fun readDeckCounts(bitmap: Bitmap, borrow: Point): Map<String, Int> =
        DECK_TYPE_COLUMNS.mapNotNull { (type, dx) ->
            val text = ocr(bitmap, borrow, intArrayOf(dx - DECK_COUNT_W / 2, DECK_COUNT_DY, DECK_COUNT_W, DECK_COUNT_H), "deckCount$type")
            // Read the digit next to the "x" rather than every digit, in case the icon edge reads as a number.
            Regex("""[xX]\s*(\d)""").find(text)?.groupValues?.get(1)?.toInt()?.let { type to it }
        }.toMap()

    /** Taps the deck arrows until the "Deck N" title shows the chosen deck. */
    private fun selectDeck() {
        val target = settings.deckNumber
        if (target !in 1..10) return
        val header = anchorOf(LabelCareerStartSupportFormationHeader, game.imageUtils.getSourceBitmap())
        repeat(MAX_CAROUSEL_TAPS) {
            val current = ocrInt(game.imageUtils.getSourceBitmap(), header, ScreenLayout.region(DECK_TITLE, Anchor.CENTER, relativeTo = Anchor.TOP), "deckTitle")
            if (current == target) {
                MessageLog.i(TAG, "[CAREER SETUP] Using Deck $target.")
                return
            }
            // The carousel wraps from Deck 10 back to Deck 1, so go the short way around.
            val forward = current == null || (target - current + 10) % 10 <= 5
            (if (forward) ButtonCareerStartDeckArrowRight else ButtonCareerStartDeckArrowLeft).click(game.imageUtils, tries = 3)
            game.wait(1.0)
        }
        throw CampaignBreakpointException("Could not switch to Deck $target on Support Formation.")
    }

    /**
     * OCRs a region measured at 1080x1920 from a template.
     *
     * @param bitmap The screenshot.
     * @param anchor The template's center on screen.
     * @param offsets The region as dx, dy, width and height from the anchor.
     * @param debugName Name for debug images.
     * @returns The text read.
     */
    private fun ocr(bitmap: Bitmap, anchor: Point, offsets: IntArray, debugName: String): String = ocrAnchored(game.imageUtils, bitmap, anchor, offsets, debugName)

    /**
     * OCRs a region measured at 1080x1920 from a template and reads the first number in it.
     *
     * @param bitmap The screenshot.
     * @param anchor The template's center on screen.
     * @param offsets The region as dx, dy, width and height from the anchor.
     * @param debugName Name for debug images.
     * @returns The first number read, or null when there is none.
     */
    private fun ocrInt(bitmap: Bitmap, anchor: Point, offsets: IntArray, debugName: String): Int? = FIRST_NUMBER.find(ocr(bitmap, anchor, offsets, debugName))?.value?.toIntOrNull()
}
