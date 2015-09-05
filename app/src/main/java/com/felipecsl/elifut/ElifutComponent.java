package com.felipecsl.elifut;

import android.content.Context;

import com.felipecsl.elifut.activitiy.MainActivity;
import com.felipecsl.elifut.activitiy.TeamDetailsActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { NetworkModule.class, DataModule.class })
public interface ElifutComponent {
  void inject(MainActivity mainActivity);
  void inject(TeamDetailsActivity teamDetailsActivity);

  final class Initializer {
    private Initializer() {
    }

    static ElifutComponent init(Context context) {
      return DaggerElifutComponent.builder()
          .networkModule(new NetworkModule(context))
          .dataModule(new DataModule())
          .build();
    }
  }
}
