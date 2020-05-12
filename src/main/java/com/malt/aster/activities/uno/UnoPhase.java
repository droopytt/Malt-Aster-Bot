package com.malt.aster.activities.uno;

import com.malt.aster.activities.ActivityPhase;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public abstract class UnoPhase implements ActivityPhase {

    protected final Uno uno;
    protected final List<User> participants;

    public UnoPhase(Uno uno) {
        this.uno = uno;
        this.participants = uno.participants;
    }
}
