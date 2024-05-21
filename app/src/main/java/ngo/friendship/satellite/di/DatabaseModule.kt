package ngo.friendship.satellite.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ngo.friendship.satellite.storage.SatelliteCareDatabaseManager
import javax.inject.Singleton
/**
 * Created by Yeasin Ali on 7/3/2023.
 * friendship.ngo
 * yeasinali@friendship.ngo
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): SatelliteCareDatabaseManager {
        var db =
            SatelliteCareDatabaseManager(context)
        db.initializeDatabase()
        return db
    }
}