package com.malt.aster.activities.uno;

import com.malt.aster.activities.ActivityPhase;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.Set;

public abstract class UnoPhase implements ActivityPhase {

    protected final Uno uno;
    protected final Message startUno;
    protected final Set<User> participants;

    public UnoPhase(Uno uno) {
        this.uno = uno;
        this.startUno = uno.unoMessage;
        this.participants = uno.getParticipants();
    }
}
