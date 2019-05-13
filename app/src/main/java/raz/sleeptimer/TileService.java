package raz.sleeptimer;

import android.content.Intent;
import android.service.quicksettings.Tile;

public class TileService extends android.service.quicksettings.TileService
{
    static private Tile tile;

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onTileAdded()
    {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved()
    {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening()
    {
        super.onStartListening();

        tile = getQsTile();
        tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStopListening()
    {
        super.onStopListening();

        tile = getQsTile();
        tile.setState(Tile.STATE_UNAVAILABLE);
        tile.updateTile();
    }

    @Override
    public void onClick()
    {
        super.onClick();

        //Start main activity
        startActivityAndCollapse(new Intent(this, SleepTimer.class));
    }

    static public void setActive()
    {
        try {
            tile.setState(Tile.STATE_ACTIVE);
            tile.updateTile();
        }
        catch (NullPointerException ex)
        {
            //TODO
        }
    }

    static public void setInactive()
    {
        try {
            tile.setState(Tile.STATE_INACTIVE);
            tile.updateTile();
        }
        catch (NullPointerException ex)
        {
            //TODO
        }
    }
}
