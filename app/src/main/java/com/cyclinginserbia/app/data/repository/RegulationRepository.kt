package com.cyclinginserbia.app.data.repository

import com.cyclinginserbia.app.R
import com.cyclinginserbia.app.data.model.Regulation
import com.cyclinginserbia.app.data.model.RegulationCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegulationRepository @Inject constructor() {

    suspend fun getRegulations(): List<RegulationCategory> = ALL_REGULATIONS

    private companion object {
        val ALL_REGULATIONS = listOf(
            RegulationCategory(
                id = "basic-rules",
                title = "Riding Rules",
                items = listOf(
                    Regulation(
                        id = "basic-1",
                        title = "We ask you to wear a helmet",
                        content = "In a group, not wearing a helmet puts extra liability on others in case the non-wearer gets a head injury — whether through one’s own fault or somebody else’s.\nThus, wearing a helmet is a matter of baseline respect for the others, even though you’re not going to crash.",
                    ),
                    Regulation(
                        id = "basic-2",
                        title = "Come on a road or gravel bike",
                        content = "The narrow handlebars let us ride closer to each other — two abreast and when overtaking. This leaves more space for maneuvers.\nMoreover, road bars don’t tend to get caught on each other. And in case of a crash the rear-facing bar ends are less likely to cause injuries.\n\nPlease don’t ride any of these in the group:\n• time-trial, triathlon bikes, or with clip-on bars\n• flat-bar or other bikes with wide handlebars\n• fixed-gear bikes without at least a front brake\n• bikes with malfunctions (e.g. a failed brake)",
                    ),
                ),
            ),
            RegulationCategory(
                id = "traffic-laws",
                title = "Traffic Laws",
                items = listOf(
                    Regulation(
                        id = "traffic-1",
                        title = "No drunk riding",
                        content = "The tolerated blood alcohol limit for cyclists in Serbia is 0.2 mg/ml.\nA beer should be fine, but two or more may put you in jail. The police do check cyclists, even if rarely. And they will certainly test in case of a traffic accident.",
                    ),
                    Regulation(
                        id = "traffic-2",
                        title = "Use lights when appropriate",
                        content = "A non-blinking white light at the front and a red one at the back must be attached to the bicycle and turned on when it’s dark, foggy, or raining.\nPlease avoid brightly blinking rear lights in the group when there’s no particular reason for those. In the daytime the group is clearly visible from behind.",
                    ),
                    Regulation(
                        id = "traffic-3",
                        title = "No stereo",
                        content = "Riding with earphones in both ears is prohibited by law.\nIn the group, please don’t listen to music at all, including through a single earphone, open-ear or bone-conducting headphones, or a portable speaker.\nYou should be able to hear the others, and they have the right not to share your music tastes, no matter how good they are.",
                    ),
                    Regulation(
                        id = "traffic-4",
                        title = "See the full traffic laws guide",
                        content = "There is a comprehensive and constantly updated guide on the Serbian traffic laws by [Bike Gremlin](https://www.paragraf.rs/propisi/zakon_o_bezbednosti_saobracaja_na_putevima.html).",
                    ),
                ),
            ),
            RegulationCategory(
                id = "etiquette",
                title = "Etiquette",
                items = listOf(
                    Regulation(
                        id = "etiq-1",
                        title = "Don’t overtake the pacer",
                        content = "Some of our rides are paced by a ride leader. Please do not overtake him or her without a really good reason.",
                    ),
                    Regulation(
                        id = "etiq-2",
                        title = "Use hand signals",
                        content = "Cycling etiquette assumes the riders in front of you show potholes & other stuff by hand gestures — and you pass those further on to people behind.\n\n• A pothole large enough to cause a flat tyre is shown by pointing down (or flicking the same-side elbow while keeping both hands on the bar).\n• Pointing to the side horizontally or upwards is a turn signal.\n• If there’s a risk of collision (with a car, pedestrian, parking post, etc.), wave away from the danger with the hand behind your back.\n• Obstacles that we cannot go around (speed bumps or rail tracks) are shown by swinging either hand on your side (but not behind your back).",
                        imageRes = R.drawable.__signs,
                    ),
                    Regulation(
                        id = "etiq-3",
                        title = "Don’t slow down without reason",
                        content = "All your moves in the group should be smooth and predictable. Try to never brake or even stop pedaling. If you start coasting suddenly, that may slow you down enough to cause a disastrous chain reaction behind you.\nParticularly, there is no reason to coast after you see the sign for a speed bump or rail tracks — instead of trying to increase the space in front of you, try shifting to the side (see the checkerboard pattern tip below).",
                    ),
                    Regulation(
                        id = "etiq-4",
                        title = "Be self-sufficient",
                        content = "You should have all you may need in case of a puncture or mechanical failure: a spare tube and/or tyre plugs, a minipump or other inflating device, a multitool. You’re supposed to know how to [fix a flat tyre](https://dropba.rs/blog/fix-flats) without resorting to others’ help (even though we always help each other).\nOn longer rides, it makes sense to carry some carbs and a couple of bottles of water or electrolytes. It’s also a good idea to have a charged-up phone with an internet connection and some emergency cash.",
                    ),
                    Regulation(
                        id = "etiq-5",
                        title = "Take and share pictures",
                        content = "We encourage you to take photos & videos during our rides, provided that doesn’t endanger you or others (particularly when riding with one or no hands on the bars — and please no long selfie sticks in the group).\nPlease share your photos & videos in DBB chats on [Telegram](https://t.me/dropbarbar#), [WhatsApp](https://chat.whatsapp.com/LDYZnmoj4LDCYmFjLSrxcS) & [Viber](https://invite.viber.com/?g2=AQAoXTR0yDzRGlDpONaoGvsn3j%2BKN4tjWYnu4QK%2FhI8rAvaBiwH1Jz3L%2FiLbxzTJ&lang=en).\nBy doing so, you grant us your irrevocable consent to use them in any imaginable way (but mostly on our [Instagram](https://www.instagram.com/dbb.club/)).\nBy coming to our events and riding with the group, you agree to be filmed, and you grant us your irrevocable consent to use images of you in any imaginable way. Thanks!",
                    ),
                ),
            ),
            RegulationCategory(
                id = "tips",
                title = "Tips",
                items = listOf(
                    Regulation(
                        id = "tip-1",
                        title = "Distance to the rider in front",
                        content = "The closer you are behind another rider, the easier it is for you to keep the pace, due to the lesser air resistance. The difference can be huge! Staying in the draft is the key to not struggling with the pace.\nThere is no need to be centimeters-close if you don’t feel comfortable doing so. About half a meter is a good distance that is safe enough, but will also let you have most of the aero benefit.",
                        imageRes = R.drawable.__distance,
                    ),
                    Regulation(
                        id = "tip-2",
                        title = "Riding in a checkerboard pattern",
                        content = "If the rider in front of you skips hand signals or you’re not comfortable being right on their wheel for another reason, consider shifting to the outside of the group by about half a meter. There’ll still be plenty of draft.\nThat way, you’ll see obstacles farther in front of you, and the cyclist behind will also get some space in front of them for extra reaction time. Such spacing also works well before speed bumps and rail tracks.",
                        imageRes = R.drawable.__shift,
                    ),
                    Regulation(
                        id = "tip-3",
                        title = "Never overlap wheels",
                        content = "It’s important to not overlap your wheels with the rider in front. If it’s them who has shifted outside, stay where you are relative to them to maintain the checkerboard order — don’t squeeze them out!\nAlso, avoid riding three abreast: that doesn’t allow the person in the middle to freely maneuver and avoid obstacles.\nWhen standing up on the pedals, be careful not to suddenly roll back your bike underneath you into the wheel of the cyclist behind. Show your intention by double-flicking both your elbows a few seconds before standing up, then shift your weight up smoothly.",
                        imageRes = R.drawable.__overlap,
                    ),
                    Regulation(
                        id = "tip-4",
                        title = "Catching up after a turn or a stop",
                        content = "After a corner or a traffic light, the group will inevitably stretch. The ride leader should gain speed gradually, allowing each member of the group to catch up. Don’t rush, but also don’t hesitate.\nWhen catching up, imagine you are a boat that wants to “moor” to the rider in front without much of a delay — but without brakes. Try to close the distance, but do it without sudden changes in speed.\nIf you happen to overshoot, try not to brake, but shift sideways more than half a meter. You will lose the draft, and the air will slow you down softly, so the rider in the back will not run into you.",
                        imageRes = R.drawable.__overshoot,
                    ),
                    Regulation(
                        id = "tip-5",
                        title = "Sidewind positioning",
                        content = "If there is sidewind, it may be difficult to keep the pace, as the draft is compromised. First, it’s easier to be in the line that is away from the wind. Also, you can try shifting a bit sideways from it if there’s enough space on the road.",
                        imageRes = R.drawable.__wind,
                    ),
                    Regulation(
                        id = "tip-6",
                        title = "Don’t get left behind accidentally",
                        content = "If you have a puncture, or just cannot keep riding at the pace of the group, don’t worry. Say loud and clear that you need to slow down or stop. As soon as this is passed on to the ride leader, he or she will do just that.\nSpecific to DBB rides — if everyone around is accelerating suddenly, don’t feel that you must do that. We have certain segments where some people push hard, but then they wait for the rest. Live location is on the WhatsApp.",
                    ),
                    Regulation(
                        id = "tip-7",
                        title = "Use the best tyres you can",
                        content = "Of all your bike components, tyres are by far the most important for maintaining speed in the group. Apart from that, we all benefit if one doesn’t puncture all the time or crash in a slippery turn.\n\nRecommended gravel tyres:\n• Hutchinson Caracal RACE (avoid the model without the word race)\n• Tufo Thundero or Speedero (both are equally good, no need for HD)\n\nRecommended road tyres:\n• Pirelli P Zero Race RS (RS is also a distinctive part of the name)\n• Continental Grand Prix 5000 S TR\n\nImportantly, inflate your tyres to the optimal pressure, not the one indicated on the sidewall or advised to you anecdotally. We suggest using tyre pressure calculators from [Silca](https://silca.cc/pages/sppc) or [Wolftooth](https://www.wolftoothcomponents.com/pages/tire-pressure-calculator).",
                    ),
                ),
            ),
        )
    }
}
