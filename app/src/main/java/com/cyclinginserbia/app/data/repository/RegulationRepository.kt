package com.cyclinginserbia.app.data.repository

import com.cyclinginserbia.app.R
import com.cyclinginserbia.app.data.model.Regulation
import com.cyclinginserbia.app.data.model.RegulationCategory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The rule book is bundled, but its text lives in strings.xml so it can be
 * localized. This repository only holds the structure (ids + @StringRes
 * pointers + images); the actual prose is resolved in the Composable layer,
 * where the user-selected locale is in scope. Resolving here (or in a
 * ViewModel) would pick up the system locale instead of the user's choice.
 */
@Singleton
class RegulationRepository @Inject constructor() {

    suspend fun getRegulations(): List<RegulationCategory> = ALL_REGULATIONS

    private companion object {
        val ALL_REGULATIONS = listOf(
            RegulationCategory(
                id = "basic-rules",
                titleRes = R.string.reg_cat_basic,
                items = listOf(
                    Regulation(
                        id = "basic-1",
                        titleRes = R.string.reg_basic_1_title,
                        contentRes = R.string.reg_basic_1_content,
                    ),
                    Regulation(
                        id = "basic-2",
                        titleRes = R.string.reg_basic_2_title,
                        contentRes = R.string.reg_basic_2_content,
                    ),
                ),
            ),
            RegulationCategory(
                id = "traffic-laws",
                titleRes = R.string.reg_cat_traffic,
                items = listOf(
                    Regulation(
                        id = "traffic-1",
                        titleRes = R.string.reg_traffic_1_title,
                        contentRes = R.string.reg_traffic_1_content,
                    ),
                    Regulation(
                        id = "traffic-2",
                        titleRes = R.string.reg_traffic_2_title,
                        contentRes = R.string.reg_traffic_2_content,
                    ),
                    Regulation(
                        id = "traffic-3",
                        titleRes = R.string.reg_traffic_3_title,
                        contentRes = R.string.reg_traffic_3_content,
                    ),
                    Regulation(
                        id = "traffic-4",
                        titleRes = R.string.reg_traffic_4_title,
                        contentRes = R.string.reg_traffic_4_content,
                    ),
                ),
            ),
            RegulationCategory(
                id = "etiquette",
                titleRes = R.string.reg_cat_etiquette,
                items = listOf(
                    Regulation(
                        id = "etiq-1",
                        titleRes = R.string.reg_etiq_1_title,
                        contentRes = R.string.reg_etiq_1_content,
                    ),
                    Regulation(
                        id = "etiq-2",
                        titleRes = R.string.reg_etiq_2_title,
                        contentRes = R.string.reg_etiq_2_content,
                        imageRes = R.drawable.__signs,
                    ),
                    Regulation(
                        id = "etiq-3",
                        titleRes = R.string.reg_etiq_3_title,
                        contentRes = R.string.reg_etiq_3_content,
                    ),
                    Regulation(
                        id = "etiq-4",
                        titleRes = R.string.reg_etiq_4_title,
                        contentRes = R.string.reg_etiq_4_content,
                    ),
                    Regulation(
                        id = "etiq-5",
                        titleRes = R.string.reg_etiq_5_title,
                        contentRes = R.string.reg_etiq_5_content,
                    ),
                ),
            ),
            RegulationCategory(
                id = "tips",
                titleRes = R.string.reg_cat_tips,
                items = listOf(
                    Regulation(
                        id = "tip-1",
                        titleRes = R.string.reg_tip_1_title,
                        contentRes = R.string.reg_tip_1_content,
                        imageRes = R.drawable.__distance,
                    ),
                    Regulation(
                        id = "tip-2",
                        titleRes = R.string.reg_tip_2_title,
                        contentRes = R.string.reg_tip_2_content,
                        imageRes = R.drawable.__shift,
                    ),
                    Regulation(
                        id = "tip-3",
                        titleRes = R.string.reg_tip_3_title,
                        contentRes = R.string.reg_tip_3_content,
                        imageRes = R.drawable.__overlap,
                    ),
                    Regulation(
                        id = "tip-4",
                        titleRes = R.string.reg_tip_4_title,
                        contentRes = R.string.reg_tip_4_content,
                        imageRes = R.drawable.__overshoot,
                    ),
                    Regulation(
                        id = "tip-5",
                        titleRes = R.string.reg_tip_5_title,
                        contentRes = R.string.reg_tip_5_content,
                        imageRes = R.drawable.__wind,
                    ),
                    Regulation(
                        id = "tip-6",
                        titleRes = R.string.reg_tip_6_title,
                        contentRes = R.string.reg_tip_6_content,
                    ),
                    Regulation(
                        id = "tip-7",
                        titleRes = R.string.reg_tip_7_title,
                        contentRes = R.string.reg_tip_7_content,
                    ),
                ),
            ),
        )
    }
}
