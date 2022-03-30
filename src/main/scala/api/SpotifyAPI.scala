package api

import api.MyAPIKey.{CLIENT_ID, CLIENT_SECRET}
import com.nimbusds.jose.util.StandardCharset
import scalaj.http.{Http, HttpOptions, HttpRequest, HttpResponse}

import java.io.IOException
import java.util.Base64
import scala.io.BufferedSource

class SpotifyAPI {
  //var callback_url = "https://oauth.pstmn.io/v1/callback"
  val auth_url = "https://accounts.spotify.com/authorize"
  val token_url = "https://accounts.spotify.com/api/token"
  val token_data = "grant_type=client_credentials"
  val client_creds_b64 = Base64.getEncoder.encodeToString(s"$CLIENT_ID:$CLIENT_SECRET".getBytes)


  def requestAuthorization() {
    var url = ""
    val client_id = "?client_id=" + CLIENT_ID
    val client_secret = CLIENT_SECRET
    val response_type = "&response_type=code"
    val redirect_uri = "&redirect_uri="//+ encodeURI(redirect_uri) - redirect_uri is callback_url for me
    val show_dialog = "&show_dialog=true"
    val scope = "&scope=user-read-private user-read-email user-modify-playback-state"

    url = auth_url + client_id + response_type + redirect_uri + show_dialog + scope
  }
}
