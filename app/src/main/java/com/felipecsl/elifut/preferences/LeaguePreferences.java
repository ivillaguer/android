package com.felipecsl.elifut.preferences;

import android.content.SharedPreferences;

import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.felipecsl.elifut.models.Club;
import com.felipecsl.elifut.models.League;
import com.felipecsl.elifut.models.LeagueRound;
import com.felipecsl.elifut.models.Match;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import rx.Observable;

import static com.felipecsl.elifut.util.CollectionUtils.shuffle;
import static com.felipecsl.elifut.util.CollectionUtils.toList;

public final class LeaguePreferences {
  private static final String KEY_CLUBS = "LC";
  private static final String KEY_OPPONENTS = "OPPONENTS";
  private final JsonPreference<List<Club>> clubsPreference;
  private final JsonPreference<List<LeagueRound>> roundsPreference;

  public LeaguePreferences(SharedPreferences preferences, Moshi moshi) {
    RxSharedPreferences prefs = RxSharedPreferences.create(preferences);
    ParameterizedType clubsType = Types.newParameterizedType(List.class, Club.class);
    ParameterizedType roundsType = Types.newParameterizedType(List.class, LeagueRound.class);
    clubsPreference = new JsonPreference<>(prefs, moshi.adapter(clubsType), KEY_CLUBS);
    roundsPreference = new JsonPreference<>(prefs, moshi.adapter(roundsType), KEY_OPPONENTS);
  }

  public void putClubsAndInitRounds(Observable<Club> allClubs) {
    clubsPreference.set(toList(allClubs));

    if (roundsPreference.get() == null) {
      roundsPreference.set(League.generateRounds(shuffle(toList(allClubs))));
    }
  }

  /**
   * Removes the next round from the list of upcoming rounds on this league and returns
   * the match in that round that includes the provided club or null if none found.
   * TODO Consider breaking this down in 2 methods
   */
  public Match popAndUpdateNextMatch(Club club) {
    List<LeagueRound> nextRounds = roundsPreference.get();
    if (nextRounds == null || nextRounds.isEmpty()) {
      throw new IllegalStateException("no opponents");
    }
    Match match = nextRounds.remove(0).findMatchByClub(club);
    roundsPreference.set(nextRounds);
    return match;
  }

  public Observable<List<Club>> clubsObservable() {
    return clubsPreference.asObservable();
  }

  public JsonPreference<List<Club>> clubsPreference() {
    return clubsPreference;
  }

  public JsonPreference<List<LeagueRound>> roundsPreference() {
    return roundsPreference;
  }

  public Observable<List<LeagueRound>> roundsObservable() {
    return roundsPreference.asObservable();
  }
}