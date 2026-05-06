package com.cyclinginserbia.app.data.strava

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StravaService @Inject constructor(
    private val httpClient: HttpClient,
) {
    suspend fun fetchClubEvents(clubId: String = StravaApi.DBB_CLUB_ID): List<StravaClubEventDto> =
        httpClient.get(StravaApi.PROXY_BASE_URL + StravaApi.CLUB_EVENTS_PATH) {
            parameter("club_id", clubId)
        }.body()
}
